package pt.ipt.dam2025.trabalho

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

//tela de autenticação do veterinário
class VeterinarioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autenticacao_vet)


        val loginButton = findViewById<Button>(R.id.login_vet_button)
        loginButton.setOnClickListener {
            // Ir para a LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}