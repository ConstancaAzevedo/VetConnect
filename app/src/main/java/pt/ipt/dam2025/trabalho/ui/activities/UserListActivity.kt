package pt.ipt.dam2025.trabalho.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pt.ipt.dam2025.trabalho.databinding.ActivityUserListBinding
import pt.ipt.dam2025.trabalho.model.Usuario
import pt.ipt.dam2025.trabalho.ui.adapters.UsuarioAdapter
import pt.ipt.dam2025.trabalho.util.SessionManager
import pt.ipt.dam2025.trabalho.viewmodel.UsuarioViewModel
import pt.ipt.dam2025.trabalho.viewmodel.ViewModelFactory

// Activity para listar e apagar utilizadores
class UserListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserListBinding
    private lateinit var adapter: UsuarioAdapter
    private val viewModel: UsuarioViewModel by viewModels { ViewModelFactory(applicationContext) }
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        setupRecyclerView()
        observeViewModel()

        carregarUsuariosComToken()
    }

    private fun setupRecyclerView() {
        adapter = UsuarioAdapter { usuario ->
            // Ação de clique para apagar (exemplo)
            showDeleteConfirmation(usuario)
        }
        binding.recyclerViewUsuarios.adapter = adapter
        binding.recyclerViewUsuarios.layoutManager = LinearLayoutManager(this)
    }

    private fun observeViewModel() {
        viewModel.usuarios.observe(this) { usuarios ->
            adapter.submitList(usuarios)
        }

        viewModel.deleteResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Utilizador apagado com sucesso", Toast.LENGTH_SHORT).show()
                carregarUsuariosComToken() // Recarrega a lista após apagar
            }.onFailure {
                Toast.makeText(this, "Erro ao apagar utilizador: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmation(usuario: Usuario) {
        // Exemplo de um diálogo de confirmação
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Apagar Utilizador")
            .setMessage("Tem a certeza que deseja apagar o utilizador ${usuario.nome}?")
            .setPositiveButton("Sim") { _, _ ->
                val token = sessionManager.getAuthToken()
                if (token != null) {
                    viewModel.apagarUsuario(token, usuario.id)
                } else {
                    Toast.makeText(this, "Erro de autenticação", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun carregarUsuariosComToken() {
        val token = sessionManager.getAuthToken()
        if (token != null) {
            viewModel.carregarUsuarios(token)
        } else {
            Toast.makeText(this, "Erro de autenticação", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Recarrega os dados sempre que a atividade for retomada
        carregarUsuariosComToken()
    }
}
