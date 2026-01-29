package pt.ipt.dam2025.vetconnect.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.databinding.ActivityHomeBinding
import pt.ipt.dam2025.vetconnect.utils.SessionManager

/**
 * Activity para a página home da aplicação
 */

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // inicializa o SessionManager
        sessionManager = SessionManager(this)

        // le o ID do animal e, se for válido, guarda na sessão
        val animalId = intent.getIntExtra("ANIMAL_ID", -1)
        if (animalId != -1) {
            sessionManager.saveAnimalId(animalId)
        }

        // configurar os listeners -> ANIMAL_ID é lido do SessionManager
        binding.cardMarcarConsulta.setOnClickListener {
            val intent = Intent(this, MarcarConsultaActivity::class.java)
            intent.putExtra("ANIMAL_ID", sessionManager.getAnimalId())
            startActivity(intent)
        }
        binding.cardAgendarVacina.setOnClickListener {
            val intent = Intent(this, AgendarVacinaActivity::class.java)
            intent.putExtra("ANIMAL_ID", sessionManager.getAnimalId())
            startActivity(intent)
        }
        binding.cardConsultas.setOnClickListener {
            val intent = Intent(this, ConsultasActivity::class.java)
            intent.putExtra("ANIMAL_ID", sessionManager.getAnimalId())
            startActivity(intent)
        }
        binding.cardMinhasVacinas.setOnClickListener {
            val intent = Intent(this, VacinasActivity::class.java)
            intent.putExtra("ANIMAL_ID", sessionManager.getAnimalId())
            startActivity(intent)
        }
        binding.cardAnimal.setOnClickListener {
            val intent = Intent(this, AnimalActivity::class.java)
            intent.putExtra("ANIMAL_ID", sessionManager.getAnimalId())
            startActivity(intent)
        }
        binding.cardHistorico.setOnClickListener {
            val intent = Intent(this, HistoricoActivity::class.java)
            intent.putExtra("ANIMAL_ID", sessionManager.getAnimalId())
            startActivity(intent)
        }
        binding.cardPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
        binding.btnDefinicoes.setOnClickListener {
            startActivity(Intent(this, DefinicoesActivity::class.java))
        }
        binding.btnLogout.setOnClickListener {
            performLogout()
        }

        // voltar à pagina main quando se volta atrás
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@HomeActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    // funçao de logout
    private fun performLogout() {
        val authToken = sessionManager.getAuthToken()
        lifecycleScope.launch {
            try {
                if (authToken != null) {
                    // garante que a chamada de logout não seja cancelada
                    withContext(NonCancellable) {
                        ApiClient.apiService.logout("Bearer $authToken")
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeActivity", "Falha ao invalidar token no servidor", e)
            } finally {
                // finally será executado mesmo se a corrotina for cancelada
                sessionManager.clearSession()
                Toast.makeText(this@HomeActivity, "Sessão terminada.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}