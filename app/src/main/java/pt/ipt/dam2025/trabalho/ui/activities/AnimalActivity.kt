package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.model.Animal
import pt.ipt.dam2025.trabalho.viewmodel.AnimalViewModel

class AnimalActivity : AppCompatActivity() {

    private lateinit var animalPhoto: ImageView
    private lateinit var etNomeAnimal: EditText
    private lateinit var etEspecie: EditText
    private lateinit var etRaca: EditText
    private lateinit var etDataNascimentoAnimal: EditText
    private lateinit var btnGuardarAnimal: Button

    // Variável para guardar o URI da foto
    private var photoUri: Uri? = null
    
    // ViewModel para interagir com a base de dados
    private val animalViewModel: AnimalViewModel by viewModels()

    private val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uriString = result.data?.getStringExtra("image_uri")
            if (uriString != null) {
                // Guarda o URI e atualiza a imagem
                photoUri = Uri.parse(uriString)
                animalPhoto.setImageURI(photoUri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal)

        animalPhoto = findViewById(R.id.animal_photo)
        etNomeAnimal = findViewById(R.id.etNomeAnimal)
        etEspecie = findViewById(R.id.etEspecie)
        etRaca = findViewById(R.id.etRaca)
        etDataNascimentoAnimal = findViewById(R.id.etDataNascimentoAnimal)
        btnGuardarAnimal = findViewById(R.id.btnGuardarAnimal)

        animalPhoto.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            getResult.launch(intent)
        }

        btnGuardarAnimal.setOnClickListener {
            guardarAnimal()
        }
    }

    private fun guardarAnimal() {
        val nome = etNomeAnimal.text.toString()
        val especie = etEspecie.text.toString()
        val raca = etRaca.text.toString()
        val dataNascimento = etDataNascimentoAnimal.text.toString()

        if (nome.isBlank() || especie.isBlank() || raca.isBlank() || dataNascimento.isBlank()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val animal = Animal(
            nome = nome,
            especie = especie,
            raca = raca,
            dataNascimento = dataNascimento,
            fotoUri = photoUri?.toString()
        )

        animalViewModel.insert(animal)
        Toast.makeText(this, "Animal guardado com sucesso!", Toast.LENGTH_SHORT).show()
        finish() // Fecha a atividade após guardar
    }
}
