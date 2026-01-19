package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.model.NovoUsuario
import pt.ipt.dam2025.trabalho.model.RegistrationResponse
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


class RegisterTutorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_tutor)

        val nameInput = findViewById<EditText>(R.id.name_input)
        val phoneInput = findViewById<EditText>(R.id.phone_input)
        val emailInput = findViewById<EditText>(R.id.email_input)
        val registerButton = findViewById<Button>(R.id.register_button)
        val rootView = findViewById<android.view.View>(android.R.id.content)

        registerButton.setOnClickListener {
            val name = nameInput.text.toString()
            val phone = phoneInput.text.toString().trim()
            val email = emailInput.text.toString().trim()

            if (name.isBlank()) {
                nameInput.error = "O nome é obrigatório"
                return@setOnClickListener
            }
            if (phone.isBlank() || phone.length != 9) {
                phoneInput.error = "Insira um número de telemóvel válido (9 dígitos)"
                return@setOnClickListener
            }
            if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.error = "Insira um email válido"
                return@setOnClickListener
            }

            Snackbar.make(rootView, "A registar...", Snackbar.LENGTH_SHORT).show()

            lifecycleScope.launch {
                try {
                    val novoUsuario = NovoUsuario(nome = name, email = email, telemovel = phone, tipo = "tutor")

                    val response = ApiClient.apiService.criarUsuario(novoUsuario)
                    if (response.isSuccessful) {
                        val registrationData = response.body()
                        if (registrationData != null) {
                            val user = registrationData.user
                            val codigoVerificacao = registrationData.verificationCode

                            val intent = Intent(this@RegisterTutorActivity, VerificTutorActivity::class.java).apply {
                                putExtra("USER_NAME", user.nome)
                                putExtra("USER_EMAIL", user.email)
                                putExtra("USER_PHONE", user.telemovel)
                                putExtra("VERIFICATION_CODE", codigoVerificacao)
                            }
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("RegisterTutorActivity", "Corpo da resposta vazio ou nulo")
                            Snackbar.make(rootView, "Ocorreu um erro inesperado.", Snackbar.LENGTH_LONG).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("RegisterTutorActivity", "Erro de API: ${response.code()} - $errorBody")
                        val errorMessage = when (response.code()) {
                            400 -> "Dados inválidos ou utilizador já existente. Verifique os dados e tente novamente."
                            else -> "Erro do servidor. Por favor, tente mais tarde."
                        }
                        Snackbar.make(rootView, errorMessage, Snackbar.LENGTH_LONG).show()
                    }

                } catch (e: IOException) {
                    // Erro de rede (sem internet, servidor offline)
                    Log.e("RegisterTutorActivity", "Erro de rede", e)
                    Snackbar.make(rootView, "Falha na ligação. Verifique a sua internet.", Snackbar.LENGTH_LONG).show()

                } catch (e: Exception) {
                    // Outros erros inesperados
                    Log.e("RegisterTutorActivity", "Erro inesperado no registo", e)
                    Snackbar.make(rootView, "Ocorreu um erro inesperado.", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}