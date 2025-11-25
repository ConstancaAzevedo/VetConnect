package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.trabalho.R


//tela de verificação do número de telemóvel do tutor
class VerificTutorActivity : AppCompatActivity() {

    private val correctVerificationCode = "123456" //código de verificação de exemplo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verific_tutor)

        val userIdentifier = intent.getStringExtra("USER_IDENTIFIER")
        val verificationCodeInput = findViewById<EditText>(R.id.verification_code_input)
        val verifyButton = findViewById<Button>(R.id.verify_button)

        verifyButton.setOnClickListener {
            val enteredCode = verificationCodeInput.text.toString()

            if (enteredCode.isBlank()) {
                verificationCodeInput.error = "Por favor, insira o código de verificação"
            } else if (enteredCode.length != 6) {
                verificationCodeInput.error = "O código de verificação deve ter 6 dígitos"
            } else if (enteredCode == correctVerificationCode) {
                // TODO: Adicionar lógica real de verificação do código com API

                Toast.makeText(this, "Verificação bem-sucedida!", Toast.LENGTH_SHORT).show()
                // Navegar para a CreatePinActivity para definir o PIN
                val intent = Intent(this, CreatePinActivity::class.java).apply {
                    putExtra("USER_IDENTIFIER", userIdentifier)
                }
                startActivity(intent)
                finish()
                
            } else {
                verificationCodeInput.error = "Código de verificação inválido"
                verificationCodeInput.text.clear()
            }
        }
    }
}
