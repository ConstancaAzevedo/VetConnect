package pt.ipt.dam2025.trabalho.ui.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.trabalho.databinding.ActivityDetalhesReceitaBinding
import pt.ipt.dam2025.trabalho.model.Receita
import pt.ipt.dam2025.trabalho.util.SessionManager
import pt.ipt.dam2025.trabalho.viewmodel.HistoricoViewModel
import pt.ipt.dam2025.trabalho.viewmodel.HistoricoViewModelFactory

// Activity para visualizar e editar os detalhes de uma receita
class DetalhesReceitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalhesReceitaBinding
    private val viewModel: HistoricoViewModel by viewModels {
        HistoricoViewModelFactory(application)
    }
    private var receita: Receita? = null
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalhesReceitaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        receita = intent.getSerializableExtra("RECEITA_EXTRA") as? Receita

        if (receita == null) {
            finish()
            return
        }

        populateUi(receita!!)

        binding.btnApagarReceita.setOnClickListener {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                viewModel.deleteReceita(token, receita!!)
                finish() // Volta para a lista anterior
            }
        }
    }

    private fun populateUi(receita: Receita) {
        binding.detalhesReceitaMedicamento.text = "Medicamento: ${receita.medicamento}"
        val posologia = "${receita.dosagem ?: ""} ${receita.frequencia ?: ""} ${receita.duracao ?: ""}".trim()
        binding.detalhesReceitaPosologia.text = "Posologia: $posologia"
        binding.detalhesReceitaData.text = "Data: ${receita.dataPrescricao}"
        binding.detalhesReceitaMedico.text = "Médico: ${receita.veterinario}"
    }
}