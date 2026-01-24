package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.model.LoginRequest
import pt.ipt.dam2025.trabalho.model.RecuperarPinRequest
import pt.ipt.dam2025.trabalho.model.RedefinirPinRequest
import pt.ipt.dam2025.trabalho.model.Usuario
import pt.ipt.dam2025.trabalho.util.SessionManager
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private val pin = StringBuilder()
    private lateinit var pinDots: List<ImageView>
    private lateinit var accountSpinner: Spinner
    private var selectedEmail: String? = null
    private var registeredAccounts: List<Usuario> = listOf()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        findViewById<TextView>(R.id.welcome_text).text = getString(R.string.login_welcome)
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

        findViewById<TextView>(R.id.forgot_pin).setOnClickListener {
            showRecoverPinDialog()
        }

        loadUsers()
    }

    private fun loadUsers() {
        val users = getCachedUsers()
        if (users.isEmpty()) {
            // Se não houver utilizadores em cache, pede o email e tenta o login direto
            promptForEmailAndLogin()
        } else {
            // Se houver utilizadores em cache, mostra o seletor
            setupSpinner(users)
        }
    }

    private fun promptForEmailAndLogin() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Primeiro Login")
        builder.setMessage("Como é a primeira vez, por favor, insira o seu email.")

        val input = EditText(this)
        input.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val email = input.text.toString().trim()
            if (email.isNotEmpty()) {
                selectedEmail = email
                findViewById<TextView>(R.id.welcome_text).text = "Bem-vindo, $email"
                // Esconde o spinner porque o email foi inserido manualmente
                accountSpinner.visibility = View.GONE
                dialog.dismiss()
            } else {
                Toast.makeText(this, "O email não pode ser vazio.", Toast.LENGTH_SHORT).show()
                promptForEmailAndLogin() // Mostra o diálogo novamente
            }
        }
        builder.setNegativeButton("Cancelar") { _, _ -> finish() }
        builder.setCancelable(false)
        builder.show()
    }

    private fun setupSpinner(users: List<Usuario>) {
        registeredAccounts = users
        val accountNames = registeredAccounts.map { it.nome }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, accountNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        accountSpinner.adapter = adapter
        accountSpinner.visibility = View.VISIBLE

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
    }

    private fun attemptLoginWithApi() {
        if (selectedEmail == null) {
            Toast.makeText(this, "Selecione ou insira uma conta de email.", Toast.LENGTH_SHORT).show()
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
                        sessionManager.saveUserId(loginData.user.id)
                        sessionManager.saveAuthToken(loginData.token)

                        // Após o login bem-sucedido, busca e armazena a lista de utilizadores
                        fetchAllUsersAndCache(loginData.token)

                        Toast.makeText(this@LoginActivity, loginData.message, Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        throw IOException("Resposta de login inválida do servidor")
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Email ou PIN incorreto. Tente novamente.", Toast.LENGTH_LONG).show()
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

    private fun fetchAllUsersAndCache(token: String) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getUsuarios("Bearer $token")
                if (response.isSuccessful) {
                    response.body()?.let { cacheUsers(it) }
                }
            } catch (e: Exception) {
                Log.e("LoginActivity", "Falha ao buscar e armazenar utilizadores.", e)
            }
        }
    }

    private fun cacheUsers(users: List<Usuario>) {
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val json = Gson().toJson(users)
        prefs.edit().putString("user_list", json).apply()
    }

    private fun getCachedUsers(): List<Usuario> {
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("user_list", null)
        return if (json != null) {
            val type = object : TypeToken<List<Usuario>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
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

    private fun showRecoverPinDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Recuperar PIN")
        builder.setMessage("Insira o seu email para receber um código de recuperação.")

        val input = EditText(this)
        input.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        builder.setView(input)

        builder.setPositiveButton("Enviar") { dialog, _ ->
            val email = input.text.toString().trim()
            if (email.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        val request = RecuperarPinRequest(email = email)
                        val response = ApiClient.apiService.recuperarPin(request)
                        if (response.isSuccessful) {
                            Toast.makeText(this@LoginActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                            showResetPinDialog(email)
                        } else {
                            Toast.makeText(this@LoginActivity, "Email não encontrado.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("LoginActivity", "Erro ao recuperar PIN", e)
                        Toast.makeText(this@LoginActivity, "Falha na comunicação com o servidor.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Email não pode ser vazio.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun showResetPinDialog(email: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Redefinir PIN")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 20, 50, 20)

        val codigoInput = EditText(this)
        codigoInput.hint = "Código de Recuperação"
        layout.addView(codigoInput)

        val novoPinInput = EditText(this)
        novoPinInput.hint = "Novo PIN (6 dígitos)"
        novoPinInput.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        layout.addView(novoPinInput)

        builder.setView(layout)

        builder.setPositiveButton("Redefinir") { dialog, _ ->
            val codigoRecuperacao = codigoInput.text.toString().trim()
            val novoPin = novoPinInput.text.toString().trim()

            if (codigoRecuperacao.isNotEmpty() && novoPin.length == 6) {
                lifecycleScope.launch {
                    try {
                        val request = RedefinirPinRequest(email, codigoRecuperacao, novoPin)
                        val response = ApiClient.apiService.redefinirPin(request)
                        if (response.isSuccessful) {
                            Toast.makeText(this@LoginActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(this@LoginActivity, "Erro: ${errorBody ?: "Falha ao redefinir PIN"}", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Log.e("LoginActivity", "Erro ao redefinir PIN", e)
                        Toast.makeText(this@LoginActivity, "Falha na comunicação com o servidor.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Código ou PIN inválido.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }

        builder.show()
    }
}