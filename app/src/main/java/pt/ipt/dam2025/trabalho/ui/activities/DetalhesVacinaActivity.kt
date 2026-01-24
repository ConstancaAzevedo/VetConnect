package pt.ipt.dam2025.trabalho.ui.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.trabalho.databinding.ActivityDetalhesVacinaBinding
import pt.ipt.dam2025.trabalho.model.Vacina
import pt.ipt.dam2025.trabalho.util.SessionManager
import pt.ipt.dam2025.trabalho.viewmodel.HistoricoViewModel
import pt.ipt.dam2025.trabalho.viewmodel.HistoricoViewModelFactory

// Activity para visualizar e editar os detalhes de uma vacina
class DetalhesVacinaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalhesVacinaBinding
    private val viewModel: HistoricoViewModel by viewModels {
        HistoricoViewModelFactory(application)
    }
    private var vacina: Vacina? = null
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalhesVacinaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        vacina = intent.getSerializableExtra("VACINA_EXTRA") as? Vacina

        if (vacina == null) {
            finish()
            return
        }

        populateUi(vacina!!)

        binding.btnApagarVacina.setOnClickListener {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                viewModel.deleteVacina(token, vacina!!)
                finish() // Volta para a lista anterior
            }
        }
    }

    private fun populateUi(vacina: Vacina) {
        binding.detalhesVacinaNome.text = "Vacina: ${vacina.tipo}"
        binding.detalhesVacinaData.text = "Data: ${vacina.dataAplicacao}"
        binding.detalhesVacinaLote.text = "Lote: ${vacina.lote}"
        binding.detalhesVacinaProximaDose.text = "Próxima dose: ${vacina.dataProxima}"
    }
}