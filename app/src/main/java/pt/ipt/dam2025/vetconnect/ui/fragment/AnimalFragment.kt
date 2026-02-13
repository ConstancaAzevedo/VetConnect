package pt.ipt.dam2025.vetconnect.ui.fragment

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.databinding.FragmentAnimalBinding
import pt.ipt.dam2025.vetconnect.model.AnimalResponse
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.AnimalViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.AnimalViewModelFactory
import java.io.File
import java.util.Calendar
import java.util.Locale

/**
 * Fragment para a página do perfil do animal
 */
class AnimalFragment : Fragment() {

    private var _binding: FragmentAnimalBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AnimalViewModel
    private lateinit var sessionManager: SessionManager
    private var animalId: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnimalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val factory = AnimalViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[AnimalViewModel::class.java]

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
        ouvirResultadoDaCamara()
    }

    private fun ouvirResultadoDaCamara() {
        setFragmentResultListener("requestKey") { _, bundle ->
            val imagePath = bundle.getString("imagePath")
            if (imagePath != null) {
                val fotoUri = Uri.fromFile(File(imagePath))
                binding.animalFoto.setImageURI(fotoUri)
                uploadFoto(fotoUri)
            }
        }
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
        val bundle = bundleOf("pathType" to "animais")
        findNavController().navigate(R.id.action_animalFragment_to_camaraFragment, bundle)
    }

    private fun uploadFoto(fotoUri: Uri) {
        val token = sessionManager.getAuthToken() ?: return
        if (animalId != -1) {
            viewModel.uploadPhoto(token, animalId, fotoUri)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}