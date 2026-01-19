package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.model.LoginRequest
import pt.ipt.dam2025.trabalho.model.Usuario
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private val pin = StringBuilder()
    private lateinit var pinDots: List<ImageView>
    private lateinit var accountSpinner: Spinner
    private var selectedEmail: String? = null
    private var registeredAccounts: List<Usuario> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val welcomeTextView = findViewById<TextView>(R.id.welcome_text)
        welcomeTextView.text = getString(R.string.login_welcome)

        accountSpinner = findViewById(R.id.account_spinner)

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
            Toast.makeText(this, "Funcionalidade a implementar", Toast.LENGTH_SHORT).show()
        }

        loadUsersFromApi()
    }

    private fun loadUsersFromApi() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getUsuarios()
                if (response.isSuccessful) {
                    registeredAccounts = response.body() ?: emptyList()

                    if (registeredAccounts.isEmpty()) {
                        Toast.makeText(this@LoginActivity, "Nenhum utilizador registado", Toast.LENGTH_LONG).show()
                        finish()
                        return@launch
                    }

                    val accountNames = registeredAccounts.map { it.nome ?: "Conta Inválida" }
                    val adapter = ArrayAdapter(this@LoginActivity, android.R.layout.simple_spinner_item, accountNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    accountSpinner.adapter = adapter

                    accountSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            selectedEmail = registeredAccounts.getOrNull(position)?.email
                            pin.clear()
                            updatePinDots()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            selectedEmail = null
                        }
                    }
                } else {
                    throw IOException("Erro ao carregar utilizadores: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("LoginActivity", "Falha ao carregar utilizadores da API", e)
                Toast.makeText(this@LoginActivity, "Falha ao carregar utilizadores", Toast.LENGTH_LONG).show()
                finish()
            }
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
        if (selectedEmail == null) {
            Toast.makeText(this, "Selecione uma conta", Toast.LENGTH_SHORT).show()
            pin.clear()
            updatePinDots()
            return
        }

        lifecycleScope.launch {
            try {
                val request = LoginRequest(email = selectedEmail!!, pin = pin.toString())
                val response = ApiClient.apiService.login(request)

                if (response.isSuccessful) {
                    val loginData = response.body()
                    if (loginData != null && loginData.token.isNotEmpty()) {
                        // Guardar dados da sessão
                        val sharedPrefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
                        sharedPrefs.edit {
                            putInt("USER_ID", loginData.user.id)
                            putString("AUTH_TOKEN", loginData.token)
                            putString("USER_NAME", loginData.user.nome)
                            putString("USER_EMAIL", loginData.user.email)
                        }

                        Toast.makeText(this@LoginActivity, loginData.message, Toast.LENGTH_SHORT).show()

                        // Navegar para a HomeActivity
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        throw IOException("Resposta de login inválida do servidor")
                    }
                } else {
                    // Erro de autenticação (ex: PIN incorreto)
                    Toast.makeText(this@LoginActivity, "PIN incorreto. Tente novamente.", Toast.LENGTH_LONG).show()
                    pin.clear()
                    updatePinDots()
                }
            } catch (e: Exception) {
                Log.e("LoginActivity", "Erro inesperado no login", e)
                Toast.makeText(this@LoginActivity, "Ocorreu um erro no login. Verifique a ligação.", Toast.LENGTH_LONG).show()
                pin.clear()
                updatePinDots()
            }
        }
    }
}
