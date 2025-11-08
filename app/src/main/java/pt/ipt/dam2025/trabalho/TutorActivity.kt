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


            //se não introduzir nenhum número
            if (enteredPhoneNumber.isEmpty()) {
                Toast.makeText(this, "Por favor, insira o código de verificação", Toast.LENGTH_SHORT).show()
            }

            // se o código não tiver 9 digitos
            if (enteredPhoneNumber.length != 9) {
                Toast.makeText(this, "O código de verificação deve ter 9 dígitos", Toast.LENGTH_SHORT).show()
            }

            if (enteredPhoneNumber == validPhoneNumber) {
                // 1. número correto - vai para a próxima atividade
                val intent = Intent(this, VerificTutorActivity::class.java)
                startActivity(intent)
                // 2. fecha esta atividade
                finish()
            } else {
                // 1. número incorreto - mensagem de erro
                Toast.makeText(this, "Número de telemóvel inválido", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
