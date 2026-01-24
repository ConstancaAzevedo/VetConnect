package pt.ipt.dam2025.trabalho.ui.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.trabalho.databinding.ActivityDetalhesExameBinding
import pt.ipt.dam2025.trabalho.model.Exame
import pt.ipt.dam2025.trabalho.util.SessionManager
import pt.ipt.dam2025.trabalho.viewmodel.HistoricoViewModel
import pt.ipt.dam2025.trabalho.viewmodel.HistoricoViewModelFactory

// Activity para visualizar e editar os detalhes de um exame
class DetalhesExameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalhesExameBinding
    private val viewModel: HistoricoViewModel by viewModels {
        HistoricoViewModelFactory(application)
    }
    private var exame: Exame? = null
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalhesExameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        exame = intent.getSerializableExtra("EXAME_EXTRA") as? Exame

        if (exame == null) {
            finish()
            return
        }

        populateUi(exame!!)

        binding.btnApagarExame.setOnClickListener {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                viewModel.deleteExame(token, exame!!)
                finish() // Volta para a lista anterior
            }
        }
    }

    private fun populateUi(exame: Exame) {
        binding.detalhesExameTipo.text = "Exame: ${exame.tipo}"
        binding.detalhesExameData.text = "Data: ${exame.dataExame}"
        binding.detalhesExameResultado.text = "Resultado: ${exame.resultado}"
        binding.detalhesExameLaboratorio.text = "Laboratório: ${exame.laboratorio}"
    }
}