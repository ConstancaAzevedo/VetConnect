package pt.ipt.dam2025.backup.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.backup.R

/**
 * Activity para a página principal da aplicação
 */

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // botão login -> leva diretamente para a página de login
        val loginButton = findViewById<Button>(R.id.btnLogin)
        loginButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

        // botão registar -> leva diretamente para a página de escolha de perfil
        val registerButton = findViewById<Button>(R.id.btnRegistar)
        registerButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, EscolhaActivity::class.java))
        }

        // botão about -> leva diretamente para a página about
        val aboutButton = findViewById<Button>(R.id.btnAbout)
        aboutButton.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }

}