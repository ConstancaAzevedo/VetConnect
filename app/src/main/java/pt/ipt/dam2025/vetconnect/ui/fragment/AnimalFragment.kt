package pt.ipt.dam2025.vetconnect.ui.fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import pt.ipt.dam2025.vetconnect.databinding.FragmentAnimalBinding
import pt.ipt.dam2025.vetconnect.model.AnimalResponse
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.AnimalViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.AnimalViewModelFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Fragment para a página do perfil do animal
 */
class AnimalFragment : Fragment() {

    private var _binding: FragmentAnimalBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AnimalViewModel
    private lateinit var sessionManager: SessionManager
    private var fotoUri: Uri? = null
    private var currentPhotoPath: String? = null
    private var animalId: Int = -1

    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) openCamera() else Toast.makeText(context, "Permissão negada", Toast.LENGTH_SHORT).show()
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            currentPhotoPath?.let {
                fotoUri = Uri.fromFile(File(it))
                binding.animalFoto.setImageURI(fotoUri)
                uploadFoto()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnimalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val factory = AnimalViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(AnimalViewModel::class.java)

        animalId = sessionManager.getAnimalId()
        val token = sessionManager.getAuthToken()

        if (token != null && animalId != -1) {
            viewModel.getAnimal(token, animalId)
        } else {
            // Modo de criação
            binding.codigoUnico.visibility = View.GONE
        }

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.animalData.setOnClickListener { setupDatePicker() }
        binding.btnGuardar.setOnClickListener { saveAnimal() }
        binding.animalFoto.setOnClickListener { dispatchTakePictureIntent() }
    }

    private fun observeViewModel() {
        viewModel.animal.observe(viewLifecycleOwner) { it?.let { populateUI(it) } }
        viewModel.operationStatus.observe(viewLifecycleOwner) {
            it.onSuccess {
                Toast.makeText(context, "Operação bem-sucedida!", Toast.LENGTH_SHORT).show()
                //talvez navegar para a home
            }.onFailure { Toast.makeText(context, "Erro: ${it.message}", Toast.LENGTH_SHORT).show() }
        }
        viewModel.fotoUrl.observe(viewLifecycleOwner) {
            it?.let { Glide.with(this).load(it).into(binding.animalFoto) }
        }
    }

    private fun populateUI(animal: AnimalResponse) {
        binding.codigoUnico.text = animal.codigoUnico
        binding.codigoUnico.visibility = View.VISIBLE
        binding.animalNome.setText(animal.nome)
        binding.animalEspecie.setText(animal.especie)
        binding.animalRaca.setText(animal.raca)
        binding.animalData.setText(animal.dataNascimento)
        binding.animalChip.setText(animal.numeroChip)
        if (!animal.fotoUrl.isNullOrEmpty()) {
            Glide.with(this).load(animal.fotoUrl).into(binding.animalFoto)
        }
    }

    private fun saveAnimal() {
        val nome = binding.animalNome.text.toString()
        val especie = binding.animalEspecie.text.toString()
        if (nome.isEmpty() || especie.isEmpty()) {
            Toast.makeText(context, "Nome e espécie são obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }
        val token = sessionManager.getAuthToken() ?: return

        viewModel.saveAnimal(token, nome, especie, binding.animalRaca.text.toString(), binding.animalData.text.toString(), binding.animalChip.text.toString())
    }

    private fun setupDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, day ->
            binding.animalData.setText(String.format(Locale.ROOT, "%d-%02d-%02d", year, month + 1, day))
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            requestCameraPermission.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        /*
         * tenta criar um ficheiro temporário para guardar a imagem
         * em caso de erro (ex: falta de espaço) mostra uma mensagem e retorna nulo
         */
        val photoFile: File? = try {
            createImageFile()
        } 
        /* _ -> uma convenção em Kotlin para indicar que um parâmetro existe mas não será usado
         * apanhamos o erro IOException mas como a única ação é mostrar uma mensagem genérica
         * não precisamos dos detalhes do erro
        */
        catch (_: IOException) {
            Toast.makeText(context, "Erro ao criar ficheiro de imagem", Toast.LENGTH_SHORT).show()
            null
        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(requireContext(), "pt.ipt.dam2025.vetconnect.fileprovider", it)
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply { putExtra(MediaStore.EXTRA_OUTPUT, photoURI) }
            takePicture.launch(takePictureIntent)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply { currentPhotoPath = absolutePath }
    }

    private fun uploadFoto() {
        val token = sessionManager.getAuthToken() ?: return
        if (animalId != -1) {
            fotoUri?.let { viewModel.uploadPhoto(token, animalId, it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
