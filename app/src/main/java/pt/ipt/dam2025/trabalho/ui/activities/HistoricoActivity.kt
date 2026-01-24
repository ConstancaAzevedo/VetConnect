package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.ui.adapters.ViewPagerAdapter
import pt.ipt.dam2025.trabalho.ui.fragments.ExamesFragment
import pt.ipt.dam2025.trabalho.ui.fragments.ReceitasFragment
import pt.ipt.dam2025.trabalho.ui.fragments.VacinasFragment
import pt.ipt.dam2025.trabalho.util.SessionManager
import pt.ipt.dam2025.trabalho.viewmodel.HistoricoViewModel
import pt.ipt.dam2025.trabalho.viewmodel.HistoricoViewModelFactory

// Activity para visualizar o histórico de um animal
class HistoricoActivity : AppCompatActivity() {

    private val viewModel: HistoricoViewModel by viewModels { HistoricoViewModelFactory(application) }
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var sessionManager: SessionManager

    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "A atualizar lista...", Toast.LENGTH_SHORT).show()
            refreshData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico)

        sessionManager = SessionManager(this)

        // Obter TODOS os IDs a partir do SessionManager
        val animalId = sessionManager.getAnimalId()
        val authToken = sessionManager.getAuthToken()

        // Validar se os IDs são válidos
        if (animalId == -1 || authToken == null) {
            Toast.makeText(this, "Erro de sessão. Por favor, faça login novamente.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        setupUI(animalId)
        refreshData(authToken, animalId)
    }

    private fun setupUI(animalId: Int) {
        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)
        val fabAddManual = findViewById<FloatingActionButton>(R.id.fab_add_manual)
        val fabScanQr = findViewById<FloatingActionButton>(R.id.fab_scan_qr)

        val adapter = ViewPagerAdapter(this)
        adapter.addFragment(ReceitasFragment(), "Receitas")
        adapter.addFragment(VacinasFragment(), "Vacinas")
        adapter.addFragment(ExamesFragment(), "Exames")

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()

        fabAddManual.setOnClickListener {
            val intent = Intent(this, AdicionarDocumentoActivity::class.java).apply {
                putExtra("ANIMAL_ID", animalId)
            }
            activityResultLauncher.launch(intent)
        }

        fabScanQr.setOnClickListener {
            val intent = Intent(this, ScanQrCodeActivity::class.java).apply {
                putExtra("ANIMAL_ID", animalId)
            }
            activityResultLauncher.launch(intent)
        }
    }

    private fun refreshData(authToken: String, animalId: Int) {
        // A Activity é responsável por iniciar o refresh.
        // Os Fragments observam o ViewModel e atualizam a sua própria UI.
        viewModel.refreshHistorico(authToken, animalId)
    }

    // Overload para o caso de ser chamado sem argumentos
    private fun refreshData() {
        val animalId = sessionManager.getAnimalId()
        val authToken = sessionManager.getAuthToken()
        if (animalId != -1 && authToken != null) {
            refreshData(authToken, animalId)
        }
    }
}