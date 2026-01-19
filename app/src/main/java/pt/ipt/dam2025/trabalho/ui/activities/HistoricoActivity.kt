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
import pt.ipt.dam2025.trabalho.VetConnectApplication
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.data.HistoricoRepository
import pt.ipt.dam2025.trabalho.ui.adapters.ViewPagerAdapter
import pt.ipt.dam2025.trabalho.ui.fragments.ExamesFragment
import pt.ipt.dam2025.trabalho.ui.fragments.ReceitasFragment
import pt.ipt.dam2025.trabalho.ui.fragments.VacinasFragment
import pt.ipt.dam2025.trabalho.viewmodel.HistoricoViewModel
import pt.ipt.dam2025.trabalho.viewmodel.HistoricoViewModelFactory

class HistoricoActivity : AppCompatActivity() {

    private val viewModel: HistoricoViewModel by viewModels {
        val database = (application as VetConnectApplication).database
        val repository = HistoricoRepository(database.receitaDao(), database.exameDao(), database.vacinaDao())
        // Agora passamos também o apiService para o factory
        HistoricoViewModelFactory(repository, ApiClient.apiService)
    }

    private val scanQrCodeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val qrCodeValue = data?.getStringExtra("QR_CODE_DATA")
            if (qrCodeValue != null) {
                // Obter o token de autenticação
                val sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
                val authToken = sharedPreferences.getString("AUTH_TOKEN", null)

                if (authToken != null) {
                    // Passar o token para o ViewModel
                    viewModel.processQrCode(qrCodeValue, authToken)
                    Toast.makeText(this, "Documento a ser processado...", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Erro de autenticação. Por favor, faça login novamente.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico)

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val fab = findViewById<FloatingActionButton>(R.id.fab_add_documento)

        val adapter = ViewPagerAdapter(this)
        adapter.addFragment(ReceitasFragment(), "Receitas")
        adapter.addFragment(ExamesFragment(), "Exames")
        adapter.addFragment(VacinasFragment(), "Vacinas")

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()

        fab.setOnClickListener {
            val intent = Intent(this, ScanQrCodeActivity::class.java)
            scanQrCodeLauncher.launch(intent)
        }
    }
}
