package pt.ipt.dam2025.trabalho

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * Ecrã de arranque da aplicação
 * Verifica se já existe um utilizador para decidir qual o ecrã a mostrar
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Este ecrã não tem interface, apenas decide para onde navegar.
        lifecycleScope.launch {
            val userDao = AppDatabase.getDatabase(applicationContext).userDao()
            val user = userDao.getAnyUser()

            // Decide qual será o próximo ecrã
            val nextActivity = if (user != null) {
                // Utilizador já registado, vai para o ecrã de PIN
                LoginActivity::class.java
            } else {
                // Nenhum utilizador registado, vai para o ecrã de escolha
                EscolhaActivity::class.java
            }

            // Navega para o ecrã decidido
            startActivity(Intent(this@MainActivity, nextActivity))

            // Finaliza a MainActivity para que não seja possível voltar a ela
            finish()
        }
    }
}





