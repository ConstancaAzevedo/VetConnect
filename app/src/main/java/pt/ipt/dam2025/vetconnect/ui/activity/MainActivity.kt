package pt.ipt.dam2025.vetconnect.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.vetconnect.databinding.ActivityMainBinding

/**
 * Activity para a página principal da aplicação
 */

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // botão login -> leva diretamente para a página de login
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

        // botão registar -> leva diretamente para a página de escolha de perfil
        binding.btnRegistar.setOnClickListener {
            startActivity(Intent(this@MainActivity, EscolhaActivity::class.java))
        }

        // botão about -> leva diretamente para a página about
        binding.btnAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }

}
