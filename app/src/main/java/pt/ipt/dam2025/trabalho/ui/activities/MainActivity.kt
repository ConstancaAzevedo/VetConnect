package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import pt.ipt.dam2025.trabalho.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.button_login)
        val registerButton = findViewById<Button>(R.id.button_register)
        val aboutButton = findViewById<Button>(R.id.about_button)
        val rootView = findViewById<android.view.View>(android.R.id.content)

        loginButton.setOnClickListener {
            val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val isRegistered = sharedPrefs.getBoolean("IS_REGISTERED", false)

            if (isRegistered) {
                // Se o utilizador já está registado, vai para o ecrã de login com PIN.
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            } else {
                // Se não, informa o utilizador que precisa de se registar primeiro.
                Snackbar.make(rootView, "Nenhum utilizador registado. Por favor, registe-se primeiro.", Snackbar.LENGTH_LONG).show()
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
