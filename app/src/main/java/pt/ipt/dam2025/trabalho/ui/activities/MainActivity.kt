package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.data.AppDatabase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val autentButton = findViewById<Button>(R.id.autent_button)
        val aboutButton = findViewById<Button>(R.id.about_button)
        val testButton = findViewById<Button>(R.id.test_button) // Botão de teste

        autentButton.setOnClickListener {
            lifecycleScope.launch {
                val userDao = AppDatabase.getDatabase(applicationContext).userDao()
                val user = userDao.getAnyUser()

                val nextActivity = if (user != null) {
                    LoginActivity::class.java
                } else {
                    EscolhaActivity::class.java
                }
                startActivity(Intent(this@MainActivity, nextActivity))
            }
        }

        aboutButton.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

        // Listener para o botão de teste
        testButton.setOnClickListener {
            val intent = Intent(this, UserListActivity::class.java)
            startActivity(intent)
        }
    }
}
