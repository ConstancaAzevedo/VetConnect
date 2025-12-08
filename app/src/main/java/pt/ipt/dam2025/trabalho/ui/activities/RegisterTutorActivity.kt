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
import retrofit2.HttpException
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
                    val novoUsuario = NovoUsuario(nome = name, email = email, tipo = "tutor")

                    val response = ApiClient.apiService.criarUsuario(novoUsuario)
                    val user = response.user
                    val codigoVerificacao = response.codigoVerificacao

                    val intent = Intent(this@RegisterTutorActivity, VerificTutorActivity::class.java).apply {
                        putExtra("USER_NAME", user.nome)
                        putExtra("USER_EMAIL", user.email)
                        putExtra("VERIFICATION_CODE", codigoVerificacao)
                    }
                    startActivity(intent)
                    finish()

                } catch (e: HttpException) {
                    val errorMessage = if (e.code() == 400) {
                        "Este email já se encontra registado. Tente outro."
                    } else {
                        "Erro do servidor. Por favor, tente mais tarde."
                    }
                    Log.e("RegisterTutorActivity", "Erro de API: ${e.code()}", e)
                    Snackbar.make(rootView, errorMessage, Snackbar.LENGTH_LONG).show()

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
