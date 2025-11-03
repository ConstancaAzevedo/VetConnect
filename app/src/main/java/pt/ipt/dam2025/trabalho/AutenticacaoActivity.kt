package pt.ipt.dam2025.trabalho

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class AutenticacaoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autenticacao)

        val phoneNumberInput = findViewById<EditText>(R.id.phone_number_input)
        val loginButton = findViewById<Button>(R.id.autent_button)
        
        loginButton.setOnClickListener {
            // 1. Obter o número de telemóvel
            val phoneNumber = phoneNumberInput.text.toString()

            // 2. Ir para a LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
