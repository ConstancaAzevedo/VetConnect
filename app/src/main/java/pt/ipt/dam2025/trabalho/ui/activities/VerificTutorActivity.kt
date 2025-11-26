package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.trabalho.R

/**
 * Ecrã para o utilizador inserir o código de verificação recebido (simulado por SMS).
 */
class VerificTutorActivity : AppCompatActivity() {

    private lateinit var correctVerificationCode: String
    private lateinit var userName: String // ALTERADO para userName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verific_tutor)

        // Recebe os dados passados do ecrã de registo
        userName = intent.getStringExtra("USER_NAME") ?: ""
        correctVerificationCode = intent.getStringExtra("VERIFICATION_CODE") ?: ""

        // Verificação de segurança: se não receber os dados, não devia estar neste ecrã.
        if (userName.isEmpty() || correctVerificationCode.isEmpty()) {
            Toast.makeText(this, "Ocorreu um erro. Tente registar-se novamente.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val verificationCodeInput = findViewById<EditText>(R.id.verification_code_input)
        val verifyButton = findViewById<Button>(R.id.verify_button)

        verifyButton.setOnClickListener {
            val enteredCode = verificationCodeInput.text.toString().trim()

            if (enteredCode.isBlank() || enteredCode.length != 6) {
                verificationCodeInput.error = "Insira um código de 6 dígitos"
                return@setOnClickListener
            }

            // Compara o código inserido com o código correto que veio da API (via ecrã anterior)
            if (enteredCode == correctVerificationCode) {
                Toast.makeText(this, "Verificação bem-sucedida!", Toast.LENGTH_SHORT).show()

                // Navega para a criação do PIN, passando o nome do utilizador
                val intent = Intent(this, CreatePinActivity::class.java).apply {
                    putExtra("USER_NAME", userName)
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
