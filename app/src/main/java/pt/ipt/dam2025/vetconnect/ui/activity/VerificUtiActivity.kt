package pt.ipt.dam2025.vetconnect.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.databinding.ActivityVerificUtiBinding
import pt.ipt.dam2025.vetconnect.model.VerificationRequest
import java.io.IOException

/**
 * Activity para a página de verificação do registo do utilizador
 */

class VerificUtiActivity : AppCompatActivity() {

    // Variável para o View Binding, que permite aceder às Views do layout de forma segura.
    private lateinit var binding: ActivityVerificUtiBinding
    // Variáveis para armazenar o email e o nome do utilizador, recebidos da Activity anterior.
    private lateinit var userEmail: String
    private lateinit var userName: String

    /**
     * Método chamado quando a Activity é criada.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Infla o layout da Activity usando View Binding e define-o como o conteúdo da janela.
        binding = ActivityVerificUtiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recebe os dados (nome, email e código de verificação) passados através do Intent da Activity de registo.
        userName = intent.getStringExtra("USER_NAME") ?: ""
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""
        val verificationCode = intent.getStringExtra("VERIFICATION_CODE")

        // Se um código de verificação foi recebido, mostra-o num Snackbar que permanece visível até ser dispensado pelo utilizador.
        if (!verificationCode.isNullOrEmpty()) {
            val snackbar = Snackbar.make(binding.root, "O seu código de verificação é: $verificationCode", Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("OK") { snackbar.dismiss() } // Adiciona um botão "OK" para fechar o Snackbar.
            snackbar.show()
        }

        // Verificação de segurança: se o nome ou o email do utilizador não forem recebidos, mostra um erro e fecha a Activity.
        if (userName.isEmpty() || userEmail.isEmpty()) {
            Snackbar.make(binding.root, "Ocorreu um erro. Tente registar-se novamente.", Snackbar.LENGTH_LONG).show()
            finish() // Fecha a Activity.
            return   // Interrompe a execução do método onCreate.
        }

        // Define um listener para o clique no botão de verificação.
        binding.verifyButton.setOnClickListener {
            // Obtém o código inserido pelo utilizador, removendo espaços em branco.
            val enteredCode = binding.verificationCodeInput.text.toString().trim()

            // Valida se o código inserido não está em branco e tem 6 dígitos.
            if (enteredCode.isBlank() || enteredCode.length != 6) {
                binding.verificationCodeInput.error = "Insira um código de 6 dígitos" // Mostra uma mensagem de erro no campo de texto.
                return@setOnClickListener // Interrompe o listener do clique.
            }

            // Inicia o processo de verificação do código através da chamada à API.
            verificarCodigoComAPI(enteredCode)
        }
    }

    /**
     * Envia o código de verificação para a API e trata da resposta.
     * Utiliza uma coroutine para não bloquear a thread principal.
     * @param codigo O código de 6 dígitos inserido pelo utilizador.
     */
    private fun verificarCodigoComAPI(codigo: String) {
        // Lança uma coroutine no escopo do ciclo de vida da Activity.
        lifecycleScope.launch {
            try {
                // Cria o objeto de requisição para a API.
                val request = VerificationRequest(email = userEmail, codigo = codigo)
                // Faz a chamada à API para verificar o código.
                val response = ApiClient.apiService.verificarCodigo(request)

                // Verifica se a resposta da API foi bem-sucedida (código 2xx).
                if (response.isSuccessful) {
                    val userId = response.body()?.userId // Obtém o ID do utilizador da resposta.
                    val successMessage = response.body()?.message ?: "Verificado com sucesso!" // Mensagem de sucesso.

                    // Mostra um Snackbar de sucesso e, quando este desaparece, navega para a próxima Activity.
                    Snackbar.make(binding.root, successMessage, Snackbar.LENGTH_SHORT).addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            // Navega para a Activity de criação de PIN, passando os dados do utilizador.
                            val intent = Intent(this@VerificUtiActivity, CreatePinActivity::class.java).apply {
                                putExtra("USER_NAME", userName)
                                putExtra("USER_EMAIL", userEmail)
                                putExtra("USER_ID", userId)
                            }
                            startActivity(intent)
                            finish() // Fecha a Activity atual para que o utilizador não possa voltar a ela.
                        }
                    }).show()
                } else {
                    // Trata os erros da API (ex: código inválido, utilizador não encontrado).
                    val errorMessage = when (response.code()) {
                        400 -> "Código de verificação inválido. Tente novamente."
                        404 -> "Utilizador não encontrado."
                        else -> "Ocorreu um erro. Tente novamente."
                    }
                    Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                    binding.verificationCodeInput.text.clear() // Limpa o campo de texto.
                }

            } catch (e: IOException) {
                // Trata erros de rede (ex: sem ligação à internet).
                Log.e("VerificUtiActivity", "Erro de rede na verificação", e)
                Snackbar.make(binding.root, "Falha na ligação. Verifique a sua internet.", Snackbar.LENGTH_LONG).show()
            } catch (e: Exception) {
                // Trata outros erros inesperados que possam ocorrer.
                Log.e("VerificUtiActivity", "Erro inesperado na verificação", e)
                Snackbar.make(binding.root, "Ocorreu um erro inesperado.", Snackbar.LENGTH_LONG).show()
                binding.verificationCodeInput.text.clear() // Limpa o campo de texto.
            }
        }
    }
}
