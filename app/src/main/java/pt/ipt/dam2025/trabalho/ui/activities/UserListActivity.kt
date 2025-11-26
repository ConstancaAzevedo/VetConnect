package pt.ipt.dam2025.trabalho.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pt.ipt.dam2025.trabalho.databinding.ActivityUserListBinding
import pt.ipt.dam2025.trabalho.ui.adapters.UsuarioAdapter
import pt.ipt.dam2025.trabalho.viewmodel.UsuarioViewModel

class UserListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserListBinding
    private val viewModel: UsuarioViewModel by viewModels()
    private lateinit var adapter: UsuarioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        // Carrega os usuários quando a Activity inicia
        viewModel.carregarUsuarios()
    }

    private fun setupRecyclerView() {
        adapter = UsuarioAdapter()
        binding.recyclerViewUsuarios.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewUsuarios.adapter = adapter
    }

    private fun setupObservers() {
        // Observa a lista de usuários
        viewModel.usuarios.observe(this) { usuarios ->
            adapter.submitList(usuarios)
        }

        // Observa mensagens de sucesso
        viewModel.mensagem.observe(this) { mensagem ->
            if (mensagem.isNotBlank()) {
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
            }
        }

        // Observa erros
        viewModel.erro.observe(this) { erro ->
            if (erro.isNotBlank()) {
                Toast.makeText(this, erro, Toast.LENGTH_LONG).show()
            }
        }

        // Observa estado de carregamento
        viewModel.carregando.observe(this) { carregando ->
            binding.progressBar.visibility = if (carregando) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnAdicionar.setOnClickListener {
            val nome = binding.editTextNome.text.toString()
            val email = binding.editTextEmail.text.toString()
            val telefone = binding.editTextTelefone.text.toString()

            if (nome.isNotEmpty() && email.isNotEmpty()) {
                // adiciona os argumentos em falta (password e tipo) com valores padrão
                viewModel.adicionarUsuario(nome, email, telefone.ifEmpty { null }, "password123", "tutor")
                
                // Limpa os campos
                binding.editTextNome.text.clear()
                binding.editTextEmail.text.clear()
                binding.editTextTelefone.text.clear()
            } else {
                Toast.makeText(
                    this,
                    "Preencha nome e email",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnRecarregar.setOnClickListener {
            viewModel.carregarUsuarios()
        }
    }
}
