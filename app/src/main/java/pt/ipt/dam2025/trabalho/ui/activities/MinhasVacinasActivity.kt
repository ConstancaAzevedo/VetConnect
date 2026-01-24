package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pt.ipt.dam2025.trabalho.databinding.ActivityMinhasVacinasBinding
import pt.ipt.dam2025.trabalho.model.Vacina
import pt.ipt.dam2025.trabalho.ui.adapters.MinhasVacinasAdapter
import pt.ipt.dam2025.trabalho.util.SessionManager
import pt.ipt.dam2025.trabalho.viewmodel.VacinaViewModel

// Activity para visualizar as vacinas agendadas
class MinhasVacinasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMinhasVacinasBinding
    private lateinit var adapter: MinhasVacinasAdapter
    private lateinit var sessionManager: SessionManager

    private val viewModel: VacinaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMinhasVacinasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        // Obter IDs do SessionManager
        val animalId = sessionManager.getAnimalId()
        val token = sessionManager.getAuthToken()

        // Validar sessão
        if (animalId == -1 || token == null) {
            Toast.makeText(this, "Erro de sessão. Por favor, faça login novamente.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        setupRecyclerView()
        observeViewModel()

        // Carregar dados
        viewModel.fetchVacinasAgendadas(token, animalId)
    }

    private fun setupRecyclerView() {
        adapter = MinhasVacinasAdapter { vacina ->
            editarVacina(vacina)
        }
        binding.recyclerViewVacinas.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewVacinas.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.vacinas.observe(this) { vacinas ->
            adapter.submitList(vacinas)
        }
        viewModel.operationStatus.observe(this) { status ->
            if (status) {
                Toast.makeText(this, "Operação realizada com sucesso!", Toast.LENGTH_SHORT).show()
                // Recarregar dados após operação
                val animalId = sessionManager.getAnimalId()
                val token = sessionManager.getAuthToken()
                if (animalId != -1 && token != null) {
                    viewModel.fetchVacinasAgendadas(token, animalId)
                }
            } else {
                Toast.makeText(this, "Erro ao realizar operação.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editarVacina(vacina: Vacina) {
        val intent = Intent(this, EditarVacinaActivity::class.java).apply {
            putExtra("VACINA_EXTRA", vacina)
        }
        startActivity(intent)
    }
}