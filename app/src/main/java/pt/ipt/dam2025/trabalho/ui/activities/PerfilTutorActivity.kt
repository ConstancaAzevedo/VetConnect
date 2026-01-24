package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.trabalho.databinding.ActivityPerfilTutorBinding
import pt.ipt.dam2025.trabalho.model.UpdateUserRequest
import pt.ipt.dam2025.trabalho.model.Usuario
import pt.ipt.dam2025.trabalho.util.SessionManager
import pt.ipt.dam2025.trabalho.viewmodel.UsuarioViewModel
import pt.ipt.dam2025.trabalho.viewmodel.ViewModelFactory

// Activity para visualizar e editar o perfil do tutor
class PerfilTutorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilTutorBinding
    private lateinit var sessionManager: SessionManager
    private var currentUser: Usuario? = null

    private val viewModel: UsuarioViewModel by viewModels { ViewModelFactory(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilTutorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        observeViewModel()
        setupClickListeners()

        val token = sessionManager.getAuthToken()
        val userId = sessionManager.getUserId()

        if (token != null && userId != -1) {
            viewModel.refreshUser(token, userId)
        } else {
            handleAuthenticationError()
        }
    }

    private fun observeViewModel() {
        viewModel.refreshResult.observe(this) { result ->
            result.onSuccess { user ->
                currentUser = user
                populateUI(user)
            }.onFailure {
                handleAuthenticationError()
            }
        }

        viewModel.updateResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(this, "Erro ao atualizar o perfil: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun populateUI(user: Usuario) {
        binding.etNome.setText(user.nome)
        binding.etEmail.setText(user.email)
        binding.etTelemovel.setText(user.telemovel)
        binding.etNacionalidade.setText(user.nacionalidade)
        binding.etSexo.setText(user.sexo)
        binding.etCC.setText(user.cc)
        binding.etDataNascimento.setText(user.dataNascimento)
        binding.etMorada.setText(user.morada)
    }

    private fun setupClickListeners() {
        binding.btnEditarGuardar.setOnClickListener {
            saveProfileChanges()
        }
    }

    private fun saveProfileChanges() {
        val token = sessionManager.getAuthToken()
        val userId = sessionManager.getUserId()

        if (token == null || userId == -1) {
            handleAuthenticationError()
            return
        }

        val request = UpdateUserRequest(
            nome = binding.etNome.text.toString(),
            email = binding.etEmail.text.toString(),
            tipo = currentUser?.tipo ?: "tutor" // Assume 'tutor' como default se não estiver definido
        )

        viewModel.updateUser(token, userId, request)
    }

    private fun handleAuthenticationError() {
        Toast.makeText(this, "Erro de autenticação. Por favor, faça login novamente.", Toast.LENGTH_LONG).show()
        // Opcionalmente, redirecionar para a tela de login. Ex:
        // val intent = Intent(this, LoginActivity::class.java)
        // startActivity(intent)
        // finish()
    }
}
