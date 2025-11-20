package pt.ipt.dam2025.trabalho

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


//tela de abertura da aplicação
class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null // variável para guardar a instância do MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa o MediaPlayer uma única vez para ser reutilizado
        mediaPlayer = MediaPlayer.create(this, R.raw.gato)


        // botão autenticação
        val loginButton = findViewById<Button>(R.id.autent_button)
        loginButton.setOnClickListener {
            playSound() // toca o som
            //ir para a página de escolha de usuário
            //val intent = Intent(this, EscolhaActivity::class.java)
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }


        // botão sobre
        val aboutButton = findViewById<Button>(R.id.about_button)
        aboutButton.setOnClickListener {
            //ir para a página about
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
    }

    // toca o som usando a instância já existente
    private fun playSound() {
        mediaPlayer?.start()
    }

    // liberta os recursos do MediaPlayer quando a Activity é destruída para evitar memory leaks
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
