package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import pt.ipt.dam2025.trabalho.R

/**
 * Ecrã onde o utilizador escolhe o tipo de perfil para o registo.
 * O propósito deste ecrã é apenas direcionar para o fluxo de registo correto.
 */
class EscolhaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autent_escolha)

        // botão tutor -> leva diretamente para o formulário de registo de tutor
        val tutorButton = findViewById<Button>(R.id.button_tutor)
        tutorButton.setOnClickListener {
            val intent = Intent(this, RegisterTutorActivity::class.java)
            startActivity(intent)
        }

        // botão veterinário -> mostra uma mensagem de funcionalidade em desenvolvimento
        val vetButton = findViewById<Button>(R.id.button_veterinario)
        vetButton.setOnClickListener { view ->
            Snackbar.make(view, "Funcionalidade em desenvolvimento", Snackbar.LENGTH_LONG).show()
        }
    }
}