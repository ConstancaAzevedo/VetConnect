package pt.ipt.dam2025.trabalho.ui.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.databinding.ActivityAnimalBinding
import pt.ipt.dam2025.trabalho.model.Animal
import pt.ipt.dam2025.trabalho.util.SessionManager
import pt.ipt.dam2025.trabalho.viewmodel.AnimalViewModel
import pt.ipt.dam2025.trabalho.viewmodel.AnimalViewModelFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Activity para visualizar e editar os detalhes de um animal
class AnimalActivity : AppCompatActivity() {

    // ViewModel para esta Activity
    private lateinit var binding: ActivityAnimalBinding
    private var fotoUri: Uri? = null
    private lateinit var currentPhotoPath: String
    private lateinit var sessionManager: SessionManager

    // ViewModel para esta Activity
    private val viewModel: AnimalViewModel by viewModels {
        AnimalViewModelFactory(application)
    }

    // Solicitar permissão da câmara
    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Permissão da câmara negada.", Toast.LENGTH_SHORT).show()
        }
    }

    // Capturar foto
    private val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            fotoUri = Uri.fromFile(File(currentPhotoPath))
            binding.ivAnimalFoto.setImageURI(fotoUri)
            uploadFoto()
        }
    }

    // Configuração da UI
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        // Obter IDs do SessionManager
        val animalId = sessionManager.getAnimalId()
        val token = sessionManager.getAuthToken()

        // Validar sessão
        if (animalId == -1 || token == null) {
            // Se não houver animal na sessão, pode ser a criação de um novo
            // Não fazemos logout aqui, a menos que seja estritamente necessário
            binding.tvCodigoUnico.visibility = View.GONE
            binding.btnApagarAnimal.visibility = View.GONE
        } else {
            binding.btnApagarAnimal.visibility = View.VISIBLE
            viewModel.getAnimal(token, animalId)
        }

        setupDatePicker()
        setupClickListeners()
        observeViewModel()
    }

    // Observa as mudanças no ViewModel
    private fun observeViewModel() {
        viewModel.animal.observe(this) { animal ->
            animal?.let { populateUI(it) }
        }
        viewModel.operationStatus.observe(this) { status ->
            if (status.isSuccess) {
                Toast.makeText(this, "Operação realizada com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Erro na operação: ${status.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.fotoUrl.observe(this) { url ->
            url?.let {
                Glide.with(this).load(it).into(binding.ivAnimalFoto)
            }
        }
    }

    private fun populateUI(animal: Animal) {
        if (!animal.codigoUnico.isNullOrEmpty()) {
            binding.tvCodigoUnico.text = animal.codigoUnico
            binding.tvCodigoUnico.visibility = View.VISIBLE
        } else {
            binding.tvCodigoUnico.visibility = View.GONE
        }
        binding.etAnimalNome.setText(animal.nome)
        binding.etAnimalEspecie.setText(animal.especie)
        binding.etAnimalRaca.setText(animal.raca)
        binding.etAnimalData.setText(animal.dataNascimento)
        binding.etAnimalChip.setText(animal.numeroChip)
        if (!animal.fotoUrl.isNullOrEmpty()) {
            Glide.with(this).load(animal.fotoUrl).into(binding.ivAnimalFoto)
        }
    }

    // Configuração dos listeners
    private fun setupClickListeners() {
        binding.btnGuardarAnimal.setOnClickListener {
            saveAnimal()
        }
        binding.btnApagarAnimal.setOnClickListener {
            val animalId = sessionManager.getAnimalId()
            val token = sessionManager.getAuthToken()
            if (animalId != -1 && token != null) {
                viewModel.deleteAnimal(token, animalId)
            }
        }
        binding.ivAnimalFoto.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    // Salvar o animal
    private fun saveAnimal() {
        val nome = binding.etAnimalNome.text.toString()
        val especie = binding.etAnimalEspecie.text.toString()
        val raca = binding.etAnimalRaca.text.toString()
        val dataNascimento = binding.etAnimalData.text.toString()
        val chip = binding.etAnimalChip.text.toString()

        if (nome.isEmpty() || especie.isEmpty()) {
            Toast.makeText(this, "Nome e espécie são obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        // Obter IDs do SessionManager
        val animalId = sessionManager.getAnimalId()
        val token = sessionManager.getAuthToken()
        val userId = sessionManager.getUserId()

        if (token == null) return

        // Criar o objeto Animal
        val animal = Animal(
            id = if (animalId == -1) 0 else animalId,
            tutorId = userId,
            nome = nome,
            especie = especie,
            raca = raca,
            dataNascimento = dataNascimento,
            fotoUrl = null, // A foto é tratada em separado
            numeroChip = chip,
            codigoUnico = viewModel.animal.value?.codigoUnico ?: "",
            dataRegisto = null,
            tutorNome = null,
            tutorEmail = null
        )

        viewModel.saveAnimal(token, animal)
    }


    // Configuração do date picker
    private fun setupDatePicker() {
        binding.etAnimalData.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val formattedDate = String.format("%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear)
                binding.etAnimalData.setText(formattedDate)
            }, year, month, day).show()
        }
    }

    // Configuração da captura de foto
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    // Solicitar permissão da câmara
    private fun dispatchTakePictureIntent() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                requestCameraPermission.launch(android.Manifest.permission.CAMERA)
            }
        }
    }
    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also { _ ->
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "pt.ipt.dam2025.trabalho.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePicture.launch(takePictureIntent)
                }
            }
        }
    }
    private fun uploadFoto(){
        fotoUri?.let{
            val animalId = sessionManager.getAnimalId()
            val token = sessionManager.getAuthToken()
            if(animalId != -1 && token != null){
                viewModel.uploadPhoto(token, animalId, it)
            }
        }
    }
}