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
import java.io.IOException

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
        val verificationCode = intent.getStringExtra("VERIFICATION_CODE")

        // Mostra o código de verificação num Snackbar que fica visível até ser dispensado
        if (!verificationCode.isNullOrEmpty()) {
            val snackbar = Snackbar.make(rootView, "O seu código de verificação é: $verificationCode", Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("OK") { snackbar.dismiss() }
            snackbar.show()
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

                if (response.isSuccessful) {
                    val successMessage = response.body()?.message ?: "Verificado com sucesso!"
                    Snackbar.make(rootView, successMessage, Snackbar.LENGTH_SHORT).addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            // Navega para a criação do PIN
                            val intent = Intent(this@VerificTutorActivity, CreatePinActivity::class.java).apply {
                                putExtra("USER_NAME", userName)
                                putExtra("USER_EMAIL", userEmail)
                            }
                            startActivity(intent)
                            finish()
                        }
                    }).show()
                } else {
                    // Erro da API (ex: código inválido, 400 Bad Request)
                    val errorMessage = when (response.code()) {
                        400 -> "Código de verificação inválido. Tente novamente."
                        404 -> "Utilizador não encontrado."
                        else -> "Ocorreu um erro. Tente novamente."
                    }
                    Snackbar.make(rootView, errorMessage, Snackbar.LENGTH_LONG).show()
                    findViewById<EditText>(R.id.verification_code_input).text.clear()
                }

            } catch (e: IOException) {
                // Erro de rede
                Log.e("VerificTutorActivity", "Erro de rede na verificação", e)
                Snackbar.make(rootView, "Falha na ligação. Verifique a sua internet.", Snackbar.LENGTH_LONG).show()
            } catch (e: Exception) {
                // Outros erros inesperados
                Log.e("VerificTutorActivity", "Erro inesperado na verificação", e)
                Snackbar.make(rootView, "Ocorreu um erro inesperado.", Snackbar.LENGTH_LONG).show()
                findViewById<EditText>(R.id.verification_code_input).text.clear()
            }
        }
    }
}