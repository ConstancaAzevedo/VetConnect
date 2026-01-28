package pt.ipt.dam2025.vetconnect.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.vetconnect.databinding.ActivityAnimalBinding
import androidx.core.net.toUri

/**
 * Activity para a página do perfil do animal
 */

class AnimalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimalBinding

    // launcher para a atividade da câmara para obter um resultado
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val uriString = result.data?.getStringExtra("image_uri")
            if (uriString != null) {
                val imageUri = uriString.toUri()
                // define a imagem capturada para o ImageView
                binding.animalFoto.setImageURI(imageUri)
                // TODO: guardar na base de dados
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // listener na foto do animal para iniciar a câmara
        binding.animalFoto.setOnClickListener {
            val intent = Intent(this, CamaraActivity::class.java)
            cameraLauncher.launch(intent)
        }
    }
}
