package pt.ipt.dam2025.trabalho

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.login_button2)

        loginButton.setOnClickListener {
            // Ir para o Home
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)

        }
    }
}
