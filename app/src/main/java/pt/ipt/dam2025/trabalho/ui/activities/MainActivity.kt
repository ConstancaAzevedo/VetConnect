package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.data.AppDatabase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.button_login)
        val registerButton = findViewById<Button>(R.id.button_register)
        val aboutButton = findViewById<Button>(R.id.about_button)

        loginButton.setOnClickListener {
            lifecycleScope.launch {
                // Verifica se existe um token de utilizador guardado na base de dados
                val token = AppDatabase.getDatabase(applicationContext).userDao().getAuthToken()

                if (token != null) {
                    // Se existir um token, significa que um utilizador já se autenticou antes.
                    // Vai para o ecrã de login com PIN.
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                } else {
                    // Se não, informa o utilizador que precisa de se registar primeiro
                    Toast.makeText(this@MainActivity, "Nenhum utilizador registado. Por favor, registe-se primeiro.", Toast.LENGTH_LONG).show()
                }
            }
        }

        registerButton.setOnClickListener {
            // Leva o utilizador para o ecrã de escolha de perfil para registo
            startActivity(Intent(this@MainActivity, EscolhaActivity::class.java))
        }

        aboutButton.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }
}
