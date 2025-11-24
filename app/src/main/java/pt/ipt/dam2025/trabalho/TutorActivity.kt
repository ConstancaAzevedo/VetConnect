package pt.ipt.dam2025.trabalho

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TutorActivity : AppCompatActivity() {

    private val validPhoneNumber = "960249058" //número de telemóvel de exemplo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autenticacao_tutor)

        val phoneNumberInput = findViewById<EditText>(R.id.phone_number_input)
        val continueButton = findViewById<Button>(R.id.login_tutor_button)

        continueButton.setOnClickListener {
            val enteredPhoneNumber = phoneNumberInput.text.toString()


            // introduzir numero de telemovel
            if (enteredPhoneNumber.isBlank()) {
                phoneNumberInput.error = "Por favor, insira o número de telemóvel" // se não introduzir nenhum número
            }
            else if (enteredPhoneNumber.length != 9) {
                phoneNumberInput.error = "O número de telemóvel deve ter 9 dígitos" // se o número não tiver 9 dígitos
            }
            else { //se o número for o correto
                // TODO: Adicionar lógica real de autenticação com API
                // 1. avançamos para a verificação do tutor
                val intent = Intent(this, VerificTutorActivity::class.java)
                startActivity(intent)
                finish() // 2. fecha esta atividade
            }
        }
    }
}