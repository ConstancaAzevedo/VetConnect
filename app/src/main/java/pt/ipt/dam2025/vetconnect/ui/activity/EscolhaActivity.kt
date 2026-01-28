package pt.ipt.dam2025.vetconnect.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import pt.ipt.dam2025.vetconnect.databinding.ActivityEscolhaBinding

/**
 * Activity para a página em que o utilizador qur tipo de perfil irá registar
 * O perfil de veterinário é apenas ilustrativo e não será implementado
 */

class EscolhaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEscolhaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEscolhaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // botão login -> leva diretamente para a página de login
        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // botão tutor -> leva diretamente para a página de registo de tutor
        binding.btnTutor.setOnClickListener {
            val intent = Intent(this, RegistarActivity::class.java)
            startActivity(intent)
        }

        // botão veterinário -> mostra uma mensagem de aviso
        binding.btnVeterinario.setOnClickListener { view ->
            Snackbar.make(view, "Funcionalidade em desenvolvimento", Snackbar.LENGTH_LONG).show()
        }
    }
}