package pt.ipt.dam2025.trabalho

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class AnimalActivity : AppCompatActivity() {

    private lateinit var animalPhoto: ImageView
    private lateinit var etNomeAnimal: EditText
    private lateinit var etEspecie: EditText
    private lateinit var etRaca: EditText
    private lateinit var etDataNascimentoAnimal: EditText
    private lateinit var btnGuardarAnimal: Button

    private val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uriString = result.data?.getStringExtra("image_uri")
            if (uriString != null) {
                val imageUri = Uri.parse(uriString)
                animalPhoto.setImageURI(imageUri)
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
            // Lógica para guardar os dados do animal (incluindo o URI da imagem) na base de dados
        }
    }
}
