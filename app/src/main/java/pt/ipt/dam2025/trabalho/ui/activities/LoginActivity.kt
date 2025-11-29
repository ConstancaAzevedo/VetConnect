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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.data.AppDatabase
import pt.ipt.dam2025.trabalho.model.LoginRequest
import pt.ipt.dam2025.trabalho.model.User
import retrofit2.HttpException
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private val pin = StringBuilder()
    private lateinit var pinDots: List<ImageView>
    private lateinit var accountSpinner: Spinner
    private var selectedEmail: String? = null
    private var registeredAccounts: List<String> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        registeredAccounts = sharedPrefs.getStringSet("REGISTERED_ACCOUNTS", null)?.toList() ?: listOf()

        if (registeredAccounts.isEmpty()) {
            Toast.makeText(this, "Nenhum utilizador registado. Por favor, registe-se.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val welcomeTextView = findViewById<TextView>(R.id.welcome_text)
        welcomeTextView.text = "Olá!"

        val accountNames = registeredAccounts.map { it.split(":::").firstOrNull() ?: "Conta Inválida" }

        accountSpinner = findViewById(R.id.account_spinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, accountNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        accountSpinner.adapter = adapter

        accountSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedEmail = registeredAccounts[position].split(":::").getOrNull(1)
                pin.clear()
                updatePinDots()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedEmail = null
            }
        }

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
        if (selectedEmail == null) {
            Toast.makeText(this, "Selecione uma conta para continuar", Toast.LENGTH_SHORT).show()
            pin.clear()
            updatePinDots()
            return
        }

        lifecycleScope.launch {
            try {
                val request = LoginRequest(email = selectedEmail!!, pin = pin.toString())
                val response = ApiClient.apiService.login(request)

                val userFromApi = response.user
                val userDao = AppDatabase.getDatabase(applicationContext).userDao()

                // Lógica de fusão de dados
                val existingUser = withContext(Dispatchers.IO) {
                    userDao.getById(userFromApi.id)
                }

                val userToSave = existingUser?.copy(
                    nome = userFromApi.nome, // Atualiza o nome
                    email = userFromApi.email, // Atualiza o email
                    telemovel = userFromApi.telemovel, // Atualiza o telemóvel
                    token = response.token // Atualiza o token
                ) ?: User(
                    id = userFromApi.id,
                    nome = userFromApi.nome,
                    email = userFromApi.email,
                    telemovel = userFromApi.telemovel,
                    token = response.token
                )

                userDao.insertOrUpdate(userToSave)

                val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                with(sharedPrefs.edit()) {
                    putInt("LOGGED_IN_USER_ID", userToSave.id)
                    apply()
                }

                Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_SHORT).show()

                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()

            } catch (e: HttpException) {
                Log.e("LoginActivity", "Erro de API: ${e.code()}", e)
                val errorMessage = when (e.code()) {
                    401 -> "PIN incorreto. Tente novamente."
                    else -> "Erro no servidor. Tente mais tarde."
                }
                Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                pin.clear()
                updatePinDots()
            } catch (e: IOException) {
                Log.e("LoginActivity", "Erro de rede", e)
                val errorMessage = "Erro de rede. Verifique a sua ligação."
                Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                pin.clear()
                updatePinDots()
            } catch (e: Exception) {
                Log.e("LoginActivity", "Erro inesperado no login: ", e)
                val errorMessage = "Ocorreu um erro inesperado. Tente novamente."
                Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                pin.clear()
                updatePinDots()
            }
        }
    }
}
