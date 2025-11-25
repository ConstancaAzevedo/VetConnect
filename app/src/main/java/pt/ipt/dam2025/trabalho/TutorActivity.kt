package pt.ipt.dam2025.trabalho

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class TutorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autenticacao_tutor)

        val phoneNumberInput = findViewById<EditText>(R.id.phone_number_input)
        val continueButton = findViewById<Button>(R.id.login_tutor_button)

        continueButton.setOnClickListener {
            val enteredPhoneNumber = phoneNumberInput.text.toString()

            if (enteredPhoneNumber.isBlank()) {
                phoneNumberInput.error = "Por favor, insira o número de telemóvel"
            } else if (enteredPhoneNumber.length != 9) {
                phoneNumberInput.error = "O número de telemóvel deve ter 9 dígitos"
            } else {
                // TODO: Adicionar lógica real de autenticação com API
                val intent = Intent(this, VerificTutorActivity::class.java).apply {
                    putExtra("USER_IDENTIFIER", enteredPhoneNumber)
                }
                startActivity(intent)
                finish()
            }
        }
    }
}