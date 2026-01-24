package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.databinding.ActivityRegisterTutorBinding
import pt.ipt.dam2025.trabalho.model.NovoUsuario

// Activity de registo de um tutor
class RegisterTutorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterTutorBinding

    // Objeto para parsing da mensagem de erro da API
    data class ErrorResponse(@SerializedName("error") val error: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterTutorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val name = binding.nameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val phone = binding.phoneInput.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Snackbar.make(binding.root, "Todos os campos são obrigatórios", Snackbar.LENGTH_LONG).show()
            } else {
                lifecycleScope.launch {
                    try {
                        val novoUsuario = NovoUsuario(nome = name, email = email, telemovel = phone, tipo = "tutor")
                        val response = ApiClient.apiService.criarUsuario(novoUsuario)

                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            val successMessage = responseBody?.message ?: "Registo bem sucedido!"
                            Toast.makeText(this@RegisterTutorActivity, successMessage, Toast.LENGTH_LONG).show()
                            
                            // Extrai os dados necessários para o próximo ecrã
                            val verificationCode = responseBody?.verificationCode

                            // Leva para a atividade de verificação após o sucesso do registo
                            val intent = Intent(this@RegisterTutorActivity, VerificTutorActivity::class.java).apply {
                                putExtra("USER_NAME", name)
                                putExtra("USER_EMAIL", email)
                                putExtra("VERIFICATION_CODE", verificationCode)
                            }
                            startActivity(intent)
                            finish() // Finaliza a atividade de registo
                        } else {
                            // Tenta extrair uma mensagem de erro amigável do corpo da resposta
                            val errorBody = response.errorBody()?.string()
                            val errorMessage = if (errorBody.isNullOrEmpty()) {
                                "Ocorreu um erro desconhecido."
                            } else {
                                try {
                                    val gson = Gson()
                                    val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                                    errorResponse.error
                                } catch (e: JsonSyntaxException) {
                                    // Se o JSON for malformado, mostra o corpo do erro (ou uma mensagem genérica)
                                    Log.e("RegisterTutorActivity", "Erro ao fazer parsing do JSON de erro: $errorBody")
                                    "Ocorreu um erro no registo."
                                }
                            }
                            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Log.e("RegisterTutorActivity", "Falha na ligação à API", e)
                        Snackbar.make(binding.root, "Falha na ligação. Verifique a sua internet.", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
