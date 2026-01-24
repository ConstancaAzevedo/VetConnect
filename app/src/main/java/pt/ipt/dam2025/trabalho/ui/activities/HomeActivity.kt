package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.util.SessionManager

// Activity principal do aplicativo
class HomeActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sessionManager = SessionManager(this)

        // Ler o ID do animal e, se for válido, guardá-lo na sessão
        val animalId = intent.getIntExtra("ANIMAL_ID", -1)
        if (animalId != -1) {
            sessionManager.saveAnimalId(animalId)
        }

        // Obter referências para os CardViews e botões
        val cardMarcarConsulta = findViewById<CardView>(R.id.card_marcar_consulta)
        val cardAgendarVacina = findViewById<CardView>(R.id.card_agendar_vacina)
        val cardConsultas = findViewById<CardView>(R.id.card_consultas)
        val cardMinhasVacinas = findViewById<CardView>(R.id.card_minhas_vacinas)
        val cardAnimal = findViewById<CardView>(R.id.card_animal)
        val cardHistorico = findViewById<CardView>(R.id.card_historico)
        val cardPerfil = findViewById<CardView>(R.id.card_perfil)
        val settingsButton = findViewById<Button>(R.id.settings_button)
        val logoutButton = findViewById<Button>(R.id.logout_button)

        // Configurar os cliques nos cartões, garantindo que o ANIMAL_ID é lido do SessionManager
        cardMarcarConsulta.setOnClickListener {
            startActivity(Intent(this, MarcarConsultaActivity::class.java))
        }

        cardAgendarVacina.setOnClickListener {
            startActivity(Intent(this, AgendarVacinaActivity::class.java))
        }

        cardConsultas.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
        }

        cardMinhasVacinas.setOnClickListener {
            startActivity(Intent(this, MinhasVacinasActivity::class.java))
        }

        cardAnimal.setOnClickListener {
            startActivity(Intent(this, AnimalActivity::class.java))
        }

        cardHistorico.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
        }

        cardPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilTutorActivity::class.java))
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        logoutButton.setOnClickListener {
            performLogout()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@HomeActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    private fun performLogout() {
        val authToken = sessionManager.getAuthToken()

        lifecycleScope.launch {
            try {
                if (authToken != null) {
                    ApiClient.apiService.logout("Bearer $authToken")
                }
            } catch (e: Exception) {
                Log.e("HomeActivity", "Falha ao invalidar token no servidor", e)
            } finally {
                sessionManager.clearAuth()
                Toast.makeText(this@HomeActivity, "Sessão terminada.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}