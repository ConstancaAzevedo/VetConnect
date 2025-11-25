package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import pt.ipt.dam2025.trabalho.R


//tela principal
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        //encontrar cardviews por id
        val cardMarcarConsulta = findViewById<CardView>(R.id.card_marcar_consulta)
        val cardAnimal = findViewById<CardView>(R.id.card_animal)
        val cardHistorico = findViewById<CardView>(R.id.card_historico)
        val cardPerfil = findViewById<CardView>(R.id.card_perfil)



        //ação de clique para o cartão "Marcar Consulta"
        cardMarcarConsulta.setOnClickListener {
            //ir para a pagina de marcar consulta
            val intent = Intent(this, MarcarConsultaActivity::class.java) // Substitua "MarcarConsultaActivity" pelo nome real da sua atividade
            startActivity(intent)
        }

        //ação de clique para o cartão "O Meu Animal"
        cardAnimal.setOnClickListener {
            // Navegar para a página do perfil do animal
            val intent = Intent(this, AnimalActivity::class.java) // Substitua pelo nome correto
            startActivity(intent)
        }

        //ação de clique para o cartão "Histórico"
        cardHistorico.setOnClickListener {
            //ir para a página do histórico do animal
            val intent = Intent(this, HistoricoActivity::class.java) // Substitua pelo nome correto
            startActivity(intent)
        }

        //ação de clique para o cartão "Perfil"
        cardPerfil.setOnClickListener {
        //ir para a página do perfil do utilziador
            val intent = Intent(this, PerfilTutorActivity::class.java) // Substitua pelo nome correto
            startActivity(intent)
        }
    }
}