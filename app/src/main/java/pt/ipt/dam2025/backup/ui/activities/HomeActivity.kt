package pt.ipt.dam2025.backup.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import pt.ipt.dam2025.backup.R

/*
 * Activity para a página home da aplicação
 */

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // referências para os CardViews e botões
        val cardMarcarConsulta = findViewById<CardView>(R.id.card_marcar_consulta)
        val cardAgendarVacina = findViewById<CardView>(R.id.card_agendar_vacina)
        val cardConsultas = findViewById<CardView>(R.id.card_consultas)
        val cardMinhasVacinas = findViewById<CardView>(R.id.card_minhas_vacinas)
        val cardAnimal = findViewById<CardView>(R.id.card_animal)
        val cardHistorico = findViewById<CardView>(R.id.card_historico)
        val cardPerfil = findViewById<CardView>(R.id.card_perfil)
        val settingsButton = findViewById<Button>(R.id.settings_button)
        val logoutButton = findViewById<Button>(R.id.logout_button)

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
            val intent = Intent(this, HistoricoActivity::class.java)
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