package pt.ipt.dam2025.backup.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.backup.R

/*
 * Activity para a página principal da aplicação
 */

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        // cada botão reencaminha para uma nova página
        val loginButton = findViewById<Button>(R.id.button_login)
        val registerButton = findViewById<Button>(R.id.button_register)
        val aboutButton = findViewById<Button>(R.id.about_button)

        loginButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
        registerButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, EscolhaActivity::class.java))
        }
        aboutButton.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }

}