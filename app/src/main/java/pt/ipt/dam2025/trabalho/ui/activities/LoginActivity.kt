package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.data.AppDatabase
import pt.ipt.dam2025.trabalho.model.LoginRequest
import pt.ipt.dam2025.trabalho.model.User // <-- IMPORT CORRIGIDO
import java.lang.StringBuilder

class LoginActivity : AppCompatActivity() {

    private val pin = StringBuilder()
    private lateinit var pinDots: List<ImageView>
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userEmail = sharedPrefs.getString("USER_EMAIL", null)

        if (userEmail == null) {
            Toast.makeText(this, "Nenhum utilizador encontrado. Por favor, registe-se.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val welcomeTextView = findViewById<TextView>(R.id.welcome_text)
        welcomeTextView.text = "Olá!"

        pinDots = listOf(
            findViewById(R.id.pin_dot_1), findViewById(R.id.pin_dot_2), findViewById(R.id.pin_dot_3),
            findViewById(R.id.pin_dot_4), findViewById(R.id.pin_dot_5), findViewById(R.id.pin_dot_6)
        )

        setupNumberButtons()

        findViewById<ImageButton>(R.id.button_delete).setOnClickListener {
            if (pin.isNotEmpty()) {
                pin.deleteCharAt(pin.length - 1)
                updatePinDots()
            }
        }

        findViewById<TextView>(R.id.text_forgot_pin).setOnClickListener {
            Toast.makeText(this, "Funcionalidade a ser implementada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupNumberButtons() {
        val numberButtonClickListener = View.OnClickListener { view ->
            if (pin.length < 6) {
                val button = view as Button
                pin.append(button.text)
                updatePinDots()
                if (pin.length == 6) {
                    attemptLoginWithApi()
                }
            }
        }

        val buttons = listOf<Button>(
            findViewById(R.id.button_1), findViewById(R.id.button_2), findViewById(R.id.button_3),
            findViewById(R.id.button_4), findViewById(R.id.button_5), findViewById(R.id.button_6),
            findViewById(R.id.button_7), findViewById(R.id.button_8), findViewById(R.id.button_9),
            findViewById(R.id.button_0)
        )
        buttons.forEach { it.setOnClickListener(numberButtonClickListener) }
    }

    private fun updatePinDots() {
        for (i in pinDots.indices) {
            if (i < pin.length) {
                pinDots[i].setImageResource(R.drawable.ic_pin_dot_depois)
            } else {
                pinDots[i].setImageResource(R.drawable.ic_pin_dot_antes)
            }
        }
    }

    private fun attemptLoginWithApi() {
        lifecycleScope.launch {
            try {
                val request = LoginRequest(email = userEmail!!, pin = pin.toString())
                val response = ApiClient.apiService.login(request)

                val userDao = AppDatabase.getDatabase(applicationContext).userDao()
                var user = userDao.getUserByEmail(userEmail!!)

                if (user == null) {
                    user = User(email = userEmail!!, token = response.token)
                    userDao.insert(user)
                } else {
                    user.token = response.token
                    userDao.update(user)
                }

                Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_SHORT).show()

                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

            } catch (e: Exception) {
                Log.e("LoginActivity", "Erro no login", e)
                val errorMessage = e.message ?: "PIN incorreto ou erro de rede"
                Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                pin.clear()
                updatePinDots()
            }
        }
    }
}
