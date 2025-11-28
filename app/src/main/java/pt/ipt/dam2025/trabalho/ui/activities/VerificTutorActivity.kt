package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
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
        val rootView = findViewById<android.view.View>(android.R.id.content)

        // Recebe os dados passados do ecrã de registo
        userName = intent.getStringExtra("USER_NAME") ?: ""
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""
        val verificationCode = intent.getStringExtra("VERIFICATION_CODE") ?: ""

        // Mostra o código de verificação num Snackbar
        if (verificationCode.isNotEmpty()) {
            Snackbar.make(rootView, "SMS Simulado: O seu código é $verificationCode", 5000).show()
        }

        // Verificação de segurança: se não receber os dados, não devia estar neste ecrã.
        if (userName.isEmpty() || userEmail.isEmpty()) {
            Snackbar.make(rootView, "Ocorreu um erro. Tente registar-se novamente.", Snackbar.LENGTH_LONG).show()
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
        val rootView = findViewById<android.view.View>(android.R.id.content)
        lifecycleScope.launch {
            try {
                val request = VerificationRequest(email = userEmail, codigoVerificacao = codigo)
                val response = ApiClient.apiService.verificarCodigo(request)

                Snackbar.make(rootView, response.message, Snackbar.LENGTH_SHORT).addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        // Navega para a criação do PIN, passando o nome e o email do utilizador
                        val intent = Intent(this@VerificTutorActivity, CreatePinActivity::class.java).apply {
                            putExtra("USER_NAME", userName)
                            putExtra("USER_EMAIL", userEmail)
                        }
                        startActivity(intent)
                        finish()
                    }
                }).show()

            } catch (e: Exception) {
                // Trata de erros de rede ou respostas de erro da API (HTTP 4xx, 5xx)
                Log.e("VerificTutorActivity", "Erro na verificação do código", e)
                val errorMessage = e.message ?: "Código de verificação inválido"
                Snackbar.make(rootView, errorMessage, Snackbar.LENGTH_LONG).show()
                findViewById<EditText>(R.id.verification_code_input).text.clear()
            }
        }
    }
}
