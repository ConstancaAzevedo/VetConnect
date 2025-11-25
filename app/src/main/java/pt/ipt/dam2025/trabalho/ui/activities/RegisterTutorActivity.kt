package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.viewmodel.UsuarioViewModel

class RegisterTutorActivity : AppCompatActivity() {

    private lateinit var userIdentifier: String
    private val viewModel: UsuarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_tutor)

        userIdentifier = intent.getStringExtra("USER_IDENTIFIER") ?: ""
        if (userIdentifier.isEmpty()) {
            Toast.makeText(this, "Erro: Identificador de utilizador em falta.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val nameInput = findViewById<EditText>(R.id.name_input)
        val emailInput = findViewById<EditText>(R.id.email_input)
        val registerButton = findViewById<Button>(R.id.register_button)

        setupObservers()

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

            // Chama o ViewModel para adicionar o usuário
            viewModel.adicionarUsuario(name, email, userIdentifier)
        }
    }

    private fun setupObservers() {
        // Observa a mensagem de sucesso
        viewModel.mensagem.observe(this) { mensagem ->
            if (mensagem.isNotBlank()) {
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
                // Navega para a criação do PIN após o sucesso
                val intent = Intent(this, CreatePinActivity::class.java).apply {
                    putExtra("USER_IDENTIFIER", userIdentifier)
                }
                startActivity(intent)
                finish()
            }
        }

        // Observa mensagens de erro
        viewModel.erro.observe(this) { erro ->
            if (erro.isNotBlank()) {
                Toast.makeText(this, "Erro no registo: $erro", Toast.LENGTH_LONG).show()
            }
        }

        // Opcional: Observar o estado de carregamento para mostrar um ProgressBar
        // viewModel.carregando.observe(this) { isLoading ->
        //     // Mostrar/esconder um ProgressBar
        // }
    }
}
