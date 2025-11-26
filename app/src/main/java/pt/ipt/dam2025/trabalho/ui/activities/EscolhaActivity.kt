package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.trabalho.R

/**
 * Ecrã onde o utilizador escolhe o tipo de perfil para o registo.
 * O propósito deste ecrã é apenas direcionar para o fluxo de registo correto.
 */
class EscolhaActivity : AppCompatActivity(){
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_autent_escolha)

            // botão tutor -> leva diretamente para o formulário de registo de tutor
            val tutorButton = findViewById<Button>(R.id.button_tutor)
            tutorButton.setOnClickListener {
                val intent = Intent(this, RegisterTutorActivity::class.java)
                startActivity(intent)
            }

            // botão veterinário -> No futuro, levará para o registo de veterinário
            val vetButton = findViewById<Button>(R.id.button_veterinario)
            vetButton.setOnClickListener {
                // TODO: Implementar o ecrã de registo para o veterinário
                // Exemplo:
                // val intent = Intent(this, RegisterVeterinarioActivity::class.java)
                // startActivity(intent)
            }
        }
}