package pt.ipt.dam2025.trabalho.ui.activities

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.model.Animal
import pt.ipt.dam2025.trabalho.viewmodel.AnimalViewModel
import pt.ipt.dam2025.trabalho.viewmodel.AnimalViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale

class AnimalActivity : AppCompatActivity() {

    private val viewModel: AnimalViewModel by viewModels {
        AnimalViewModelFactory(application, ApiClient.apiService)
    }

    private lateinit var ivAnimalFoto: ImageView
    private lateinit var etAnimalNome: EditText
    private lateinit var etAnimalEspecie: EditText
    private lateinit var etAnimalRaca: EditText
    private lateinit var etAnimalData: EditText
    private lateinit var etAnimalChip: EditText
    private lateinit var btnGuardarAnimal: Button
    private lateinit var btnApagarAnimal: Button

    private var currentAnimalId: Int = -1
    private var userId: Int = -1
    private var authToken: String? = null
    private var isEditMode = false
    private var newFotoUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                tirarFoto()
            } else {
                Toast.makeText(this, "Permissão da câmara negada.", Toast.LENGTH_SHORT).show()
            }
        }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            newFotoUri?.let {
                ivAnimalFoto.setImageURI(it)
                if (currentAnimalId != -1 && authToken != null) {
                    viewModel.uploadFotoAnimal(authToken!!, currentAnimalId, it)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal)

        ivAnimalFoto = findViewById(R.id.ivAnimalFoto)
        etAnimalNome = findViewById(R.id.etAnimalNome)
        etAnimalEspecie = findViewById(R.id.etAnimalEspecie)
        etAnimalRaca = findViewById(R.id.etAnimalRaca)
        etAnimalData = findViewById(R.id.etAnimalData)
        etAnimalChip = findViewById(R.id.etAnimalChip)
        btnGuardarAnimal = findViewById(R.id.btnGuardarAnimal)
        btnApagarAnimal = findViewById(R.id.btnApagarAnimal)

        currentAnimalId = intent.getIntExtra("ANIMAL_ID", -1)
        val sharedPrefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getInt("USER_ID", -1)
        authToken = sharedPrefs.getString("AUTH_TOKEN", null)

        if (userId == -1 || authToken == null) {
            Toast.makeText(this, "Erro: Utilizador não autenticado", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupViewModelObservers()
        setupClickListeners()

        if (currentAnimalId == -1) {
            isEditMode = true
            updateUiForMode()
        } else {
            isEditMode = false
            updateUiForMode()
            viewModel.fetchAnimal(authToken!!, currentAnimalId)
        }
    }

    private fun setupViewModelObservers() {
        viewModel.animal.observe(this) { animal ->
            animal?.let {
                etAnimalNome.setText(it.nome)
                etAnimalEspecie.setText(it.especie)
                etAnimalRaca.setText(it.raca)
                etAnimalData.setText(it.dataNascimento)
                etAnimalChip.setText(it.numeroChip?.toString() ?: "")
                Glide.with(this)
                    .load(it.fotoUrl)
                    .placeholder(R.drawable.perfil_icon) // Imagem padrão
                    .into(ivAnimalFoto)
            }
        }

        viewModel.operationStatus.observe(this) { result ->
            result.onSuccess { message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                if (message.contains("eliminado")) {
                    finish()
                }
            }.onFailure { exception ->
                Toast.makeText(this, "Erro: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupClickListeners() {
        btnGuardarAnimal.setOnClickListener {
            if (isEditMode) {
                saveAnimalData()
            } else {
                isEditMode = true
                updateUiForMode()
            }
        }

        btnApagarAnimal.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        etAnimalData.setOnClickListener {
            if (isEditMode) showDatePickerDialog()
        }

        ivAnimalFoto.setOnClickListener {
            if (isEditMode) pedirPermissaoCamera()
        }
    }

    private fun pedirPermissaoCamera() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                tirarFoto()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun tirarFoto() {
        val photoFile: File = createImageFile()
        newFotoUri = FileProvider.getUriForFile(this, "pt.ipt.dam2025.trabalho.fileprovider", photoFile)
        newFotoUri?.let { takePictureLauncher.launch(it) }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun saveAnimalData() {
        val nome = etAnimalNome.text.toString()
        if (nome.isEmpty()) {
            etAnimalNome.error = "Nome é obrigatório"
            return
        }

        val animal = Animal(
            id = if (currentAnimalId == -1) 0 else currentAnimalId,
            tutorId = this.userId,
            nome = nome,
            especie = etAnimalEspecie.text.toString(),
            raca = etAnimalRaca.text.toString(),
            dataNascimento = etAnimalData.text.toString(),
            numeroChip = etAnimalChip.text.toString().toIntOrNull(),
            fotoUrl = viewModel.animal.value?.fotoUrl // Preserva a foto URL existente
        )

        authToken?.let { viewModel.saveAnimal(it, animal) }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Animal")
            .setMessage("Tem a certeza que deseja eliminar este animal?")
            .setPositiveButton("Sim, Eliminar") { _, _ ->
                authToken?.let { viewModel.deleteAnimal(it, currentAnimalId) }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun updateUiForMode() {
        val isEnabled = isEditMode
        etAnimalNome.isEnabled = isEnabled
        etAnimalEspecie.isEnabled = isEnabled
        etAnimalRaca.isEnabled = isEnabled
        etAnimalData.isClickable = isEnabled
        etAnimalChip.isEnabled = isEnabled

        if (isEnabled) {
            btnGuardarAnimal.text = "Guardar"
            btnApagarAnimal.visibility = if (currentAnimalId != -1) View.VISIBLE else View.GONE
        } else {
            btnGuardarAnimal.text = "Editar"
            btnApagarAnimal.visibility = View.GONE
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            val selectedDate = Calendar.getInstance().apply { set(year, month, day) }
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            etAnimalData.setText(format.format(selectedDate.time))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }
}