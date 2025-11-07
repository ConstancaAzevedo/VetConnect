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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loginButton = findViewById<Button>(R.id.autent_button)
        loginButton.setOnClickListener {
            playSound()
            val intent = Intent(this, EscolhaActivity::class.java)
            startActivity(intent)
        }
    }

    private fun playSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.gato)
        mediaPlayer.start()
    }
}