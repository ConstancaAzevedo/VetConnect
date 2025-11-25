package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.data.AppDatabase
import pt.ipt.dam2025.trabalho.R

class TutorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autenticacao_tutor)

        val phoneNumberInput = findViewById<EditText>(R.id.phone_number_input)
        val continueButton = findViewById<Button>(R.id.login_tutor_button)

        continueButton.setOnClickListener {
            val enteredPhoneNumber = phoneNumberInput.text.toString()

            if (enteredPhoneNumber.isBlank()) {
                phoneNumberInput.error = "Por favor, insira o número de telemóvel"
                return@setOnClickListener
            }
            if (enteredPhoneNumber.length != 9) {
                phoneNumberInput.error = "O número de telemóvel deve ter 9 dígitos"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val userDao = AppDatabase.Companion.getDatabase(applicationContext).userDao()
                val user = userDao.findByIdentifier(enteredPhoneNumber)

                if (user != null) {
                    // Utilizador já existe, ir para a tela de login com PIN
                    val intent = Intent(this@TutorActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    // Novo utilizador, vai para o ecrã de registo
                    val intent = Intent(this@TutorActivity, RegisterTutorActivity::class.java).apply {
                        putExtra("USER_IDENTIFIER", enteredPhoneNumber)
                    }
                    startActivity(intent)
                }
                finish()
            }
        }
    }
}