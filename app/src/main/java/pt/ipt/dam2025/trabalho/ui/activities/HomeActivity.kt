package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import pt.ipt.dam2025.trabalho.R


//tela principal
class HomeActivity : AppCompatActivity() {

    private var currentAnimalId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Ler o ID do animal passado para esta atividade
        currentAnimalId = intent.getIntExtra("ANIMAL_ID", -1)

        //encontrar cardviews por id
        val cardMarcarConsulta = findViewById<CardView>(R.id.card_marcar_consulta)
        val cardAnimal = findViewById<CardView>(R.id.card_animal)
        val cardHistorico = findViewById<CardView>(R.id.card_historico)
        val cardPerfil = findViewById<CardView>(R.id.card_perfil)

        //ação de clique para o cartão "Marcar Consulta"
        cardMarcarConsulta.setOnClickListener {
            val intent = Intent(this, MarcarConsultaActivity::class.java)
            // Adicionar o ID do animal ao Intent
            intent.putExtra("ANIMAL_ID", currentAnimalId)
            startActivity(intent)
        }

        //ação de clique para o cartão "O Meu Animal"
        cardAnimal.setOnClickListener {
            val intent = Intent(this, AnimalActivity::class.java)
            intent.putExtra("ANIMAL_ID", currentAnimalId)
            startActivity(intent)
        }

        //ação de clique para o cartão "Histórico"
        cardHistorico.setOnClickListener {
            val intent = Intent(this, HistoricoActivity::class.java)
            intent.putExtra("ANIMAL_ID", currentAnimalId)
            startActivity(intent)
        }

        //ação de clique para o cartão "Perfil"
        cardPerfil.setOnClickListener {
        //ir para a página do perfil do utilziador
            val intent = Intent(this, PerfilTutorActivity::class.java)
            startActivity(intent)
        }

        // adicionar callback para o botão de voltar
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@HomeActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }
}