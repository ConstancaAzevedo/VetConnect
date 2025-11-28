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
                    val novoUsuario = NovoUsuario(nome = name, email = email, telefone = phone, tipo = "tutor")

                    val response = ApiClient.apiService.criarUsuario(novoUsuario)

                    val user = response.user
                    val verificationCode = user.codigoVerificacao

                    val intent = Intent(this@RegisterTutorActivity, VerificTutorActivity::class.java).apply {
                        putExtra("USER_NAME", user.nome)
                        putExtra("USER_EMAIL", user.email)
                        putExtra("VERIFICATION_CODE", verificationCode)
                    }
                    startActivity(intent)
                    finish()

                } catch (e: Exception) {
                    Log.e("RegisterTutorActivity", "Erro ao registar utilizador", e)
                    Snackbar.make(rootView, "Erro no registo: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}
