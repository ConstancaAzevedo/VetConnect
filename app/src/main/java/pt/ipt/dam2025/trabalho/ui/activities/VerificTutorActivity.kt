package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.model.VerificationRequest

/**
 * Ecrã para o utilizador inserir o código de verificação recebido (simulado por SMS).
 */
class VerificTutorActivity : AppCompatActivity() {

    private lateinit var userEmail: String
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verific_tutor)

        // Recebe os dados passados do ecrã de registo
        userName = intent.getStringExtra("USER_NAME") ?: ""
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        // Verificação de segurança: se não receber os dados, não devia estar neste ecrã.
        if (userName.isEmpty() || userEmail.isEmpty()) {
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

            // Inicia o processo de verificação através da API
            verificarCodigoComAPI(enteredCode)
        }
    }

    /**
     * Envia o código de verificação para a API e trata da resposta.
     */
    private fun verificarCodigoComAPI(codigo: String) {
        lifecycleScope.launch {
            try {
                val request = VerificationRequest(email = userEmail, codigoVerificacao = codigo)
                val response = ApiClient.apiService.verificarCodigo(request)

                // Assume que uma resposta bem-sucedida (HTTP 2xx) significa que o código é válido
                Toast.makeText(this@VerificTutorActivity, response.message, Toast.LENGTH_SHORT).show()

                // Navega para a criação do PIN, passando o nome do utilizador
                val intent = Intent(this@VerificTutorActivity, CreatePinActivity::class.java).apply {
                    putExtra("USER_NAME", userName)
                }
                startActivity(intent)
                finish()

            } catch (e: Exception) {
                // Trata de erros de rede ou respostas de erro da API (HTTP 4xx, 5xx)
                Log.e("VerificTutorActivity", "Erro na verificação do código", e)
                val errorMessage = e.message ?: "Código de verificação inválido"
                Toast.makeText(this@VerificTutorActivity, errorMessage, Toast.LENGTH_LONG).show()
                findViewById<EditText>(R.id.verification_code_input).text.clear()
            }
        }
    }
}
