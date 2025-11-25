package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.model.NovoUsuario
import pt.ipt.dam2025.trabalho.api.RetrofitInstance

class RegisterTutorActivity : AppCompatActivity() {

    private lateinit var userIdentifier: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_tutor)

        userIdentifier = intent.getStringExtra("USER_IDENTIFIER") ?: ""
        if (userIdentifier.isEmpty()) {
            // Se não houver identificador, não podemos continuar
            Toast.makeText(this, "Erro: Identificador de utilizador em falta.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val nameInput = findViewById<EditText>(R.id.name_input)
        val emailInput = findViewById<EditText>(R.id.email_input)
        val registerButton = findViewById<Button>(R.id.register_button)

        registerButton.setOnClickListener {
            val name = nameInput.text.toString()
            val email = emailInput.text.toString()

            if (name.isBlank()) {
                nameInput.error = "O nome é obrigatório"
                return@setOnClickListener
            }
            if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.error = "Insira um email válido"
                return@setOnClickListener
            }

            // Dados válidos, simular registo e navegar
            simulateRegistration()
        }
    }

    private fun simulateRegistration() {
        // SIMULAÇÃO: Fingir que a chamada à API foi bem-sucedida.
        // Mais tarde, o código da API será reativado aqui.
        Toast.makeText(this, "Registo (simulado) bem-sucedido!", Toast.LENGTH_SHORT).show()

        // Navegar para a criação do PIN
        val intent = Intent(this, CreatePinActivity::class.java).apply {
            putExtra("USER_IDENTIFIER", userIdentifier)
        }
        startActivity(intent)
        finish()
    }

    private fun registerUser(name: String, email: String) {
        // ESTE CÓDIGO ESTÁ TEMPORARIAMENTE DESATIVADO ATÉ A API ESTAR PRONTA
        lifecycleScope.launch {
            try {
                val novoUsuario = NovoUsuario(nome = name, email = email, telefone = userIdentifier)
                val response = RetrofitInstance.api.createUser(novoUsuario)

                if (response.isSuccessful) {
                    // API call foi um sucesso
                    Toast.makeText(this@RegisterTutorActivity, "Registo bem-sucedido!", Toast.LENGTH_SHORT).show()
                    // Navegar para a criação do PIN
                    val intent = Intent(this@RegisterTutorActivity, CreatePinActivity::class.java).apply {
                        putExtra("USER_IDENTIFIER", userIdentifier)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    // A API retornou um erro (ex: 4xx, 5xx)
                    val errorBody = response.errorBody()?.string() ?: "Erro desconhecido"
                    Log.e("RegisterTutorActivity", "API Error: $errorBody")
                    Toast.makeText(this@RegisterTutorActivity, "Erro no registo: $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                // Erro de rede ou outro problema
                Log.e("RegisterTutorActivity", "Network Exception: ", e)
                Toast.makeText(this@RegisterTutorActivity, "Falha na ligação. Verifique a sua internet.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
