package pt.ipt.dam2025.backup.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import pt.ipt.dam2025.backup.R

/**
 * Activity para a página em que o utilizador qur tipo de perfil irá registar
 * O perfil de veterinário é apenas ilustrativo e não será implementado
 */
class EscolhaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_escolha)

        // botão tutor -> leva diretamente para a página de registo de tutor
        val tutorButton = findViewById<Button>(R.id.button_tutor)
        tutorButton.setOnClickListener {
            val intent = Intent(this, RegistarActivity::class.java)
            startActivity(intent)
        }

        // botão veterinário -> mostra uma mensagem de aviso
        val vetButton = findViewById<Button>(R.id.button_veterinario)
        vetButton.setOnClickListener { view ->
            Snackbar.make(view, "Funcionalidade em desenvolvimento", Snackbar.LENGTH_LONG).show()
        }
    }
}