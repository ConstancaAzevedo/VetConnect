package pt.ipt.dam2025.vetconnect.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import pt.ipt.dam2025.backup.R

/**
 * Activity para a página home da aplicação
 */

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // referências para os CardViews e botões
        val cardMarcarConsulta = findViewById<CardView>(R.id.cardMarcarConsulta)
        val cardAgendarVacina = findViewById<CardView>(R.id.cardAgendarVacina)
        val cardConsultas = findViewById<CardView>(R.id.cardConsultas)
        val cardMinhasVacinas = findViewById<CardView>(R.id.cardMinhasVacinas)
        val cardAnimal = findViewById<CardView>(R.id.cardAnimal)
        val cardHistorico = findViewById<CardView>(R.id.cardHistorico)
        val cardPerfil = findViewById<CardView>(R.id.cardPerfil)
        val settingsButton = findViewById<Button>(R.id.btnDefinicoes)
        val logoutButton = findViewById<Button>(R.id.btnLogout)

        // reencaminhamento para as páginas
        cardMarcarConsulta.setOnClickListener {
            val intent = Intent(this, MarcarConsultaActivity::class.java)
            startActivity(intent)
        }

        cardAgendarVacina.setOnClickListener {
            val intent = Intent(this, AgendarVacinaActivity::class.java)
            startActivity(intent)
        }

        cardConsultas.setOnClickListener {
            val intent = Intent(this, ConsultasActivity::class.java)
            startActivity(intent)
        }

        cardMinhasVacinas.setOnClickListener {
            val intent = Intent(this, VacinasActivity::class.java)
            startActivity(intent)
        }
        cardAnimal.setOnClickListener {
            val intent = Intent(this, AnimalActivity::class.java)
            startActivity(intent)
        }
        cardHistorico.setOnClickListener {
            val intent = Intent(this, HistoricoActivity::class.java)
            startActivity(intent)
        }
        cardPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
        settingsButton.setOnClickListener {
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