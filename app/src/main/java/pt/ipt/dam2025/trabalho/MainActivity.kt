package pt.ipt.dam2025.trabalho

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val autentButton = findViewById<Button>(R.id.autent_button)
        val aboutButton = findViewById<Button>(R.id.about_button)

        autentButton.setOnClickListener {
            lifecycleScope.launch {
                val userDao = AppDatabase.getDatabase(applicationContext).userDao()
                val user = userDao.getAnyUser()

                val nextActivity = if (user != null) {
                    // Utilizador já registado, vai para o ecrã de PIN
                    LoginActivity::class.java
                } else {
                    // Nenhum utilizador registado, vai para o ecrã de escolha
                    EscolhaActivity::class.java
                }
                startActivity(Intent(this@MainActivity, nextActivity))
            }
        }

        aboutButton.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
    }
}
