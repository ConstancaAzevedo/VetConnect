package pt.ipt.dam2025.vetconnect.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.vetconnect.databinding.ActivityHomeBinding

/**
 * Activity para a página home da aplicação
 */

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // reencaminhamento para as páginas
        binding.cardMarcarConsulta.setOnClickListener {
            val intent = Intent(this, MarcarConsultaActivity::class.java)
            startActivity(intent)
        }
        binding.cardAgendarVacina.setOnClickListener {
            val intent = Intent(this, AgendarVacinaActivity::class.java)
            startActivity(intent)
        }
        binding.cardConsultas.setOnClickListener {
            val intent = Intent(this, ConsultasActivity::class.java)
            startActivity(intent)
        }
        binding.cardMinhasVacinas.setOnClickListener {
            val intent = Intent(this, VacinasActivity::class.java)
            startActivity(intent)
        }
        binding.cardAnimal.setOnClickListener {
            val intent = Intent(this, AnimalActivity::class.java)
            startActivity(intent)
        }
        binding.cardHistorico.setOnClickListener {
            val intent = Intent(this, HistoricoActivity::class.java)
            startActivity(intent)
        }
        binding.cardPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
        binding.btnDefinicoes.setOnClickListener {
            startActivity(Intent(this, DefinicoesActivity::class.java))
        }

        // sair da aplicação quando se volta atrás
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@HomeActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

}