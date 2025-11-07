package pt.ipt.dam2025.trabalho

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


//tela de escolha da autenticaçáo do perfil
class EscolhaActivity : AppCompatActivity(){
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_autent_escolha)

            // botão tutor
            val tutorButton = findViewById<Button>(R.id.button_tutor)
            tutorButton.setOnClickListener {
                val intent = Intent(this, TutorActivity::class.java)
                startActivity(intent)
            }


            // botão veterinário
            val vetButton = findViewById<Button>(R.id.button_veterinario)
            vetButton.setOnClickListener {
                val intent = Intent(this, VeterinarioActivity::class.java)
                startActivity(intent)
            }
        }
}