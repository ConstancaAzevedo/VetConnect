package pt.ipt.dam2025.vetconnect.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.R

/**
 * Activity para criar o PIN de login
*/

class CreatePinActivity : AppCompatActivity() {

    private val pin = StringBuilder() // armazena o PIN digitado
    private lateinit var pinDots: List<ImageView> // dots que representam os dígitos do PIN
    private lateinit var userName: String // nome do utilizador
    private lateinit var userEmail: String // email do utilizador
    private var userId: Int = -1 // id do utilizador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_pin)
        val rootView = findViewById<android.view.View>(android.R.id.content)

        // lê os dados passados pelo ecrã de verificação
        userName = intent.getStringExtra("USER_NAME") ?: ""
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""
        userId = intent.getIntExtra("USER_ID", -1)

        // verifica se os dados estão corretos
        if (userName.isEmpty() || userEmail.isEmpty() || userId == -1) {
            Snackbar.make(rootView, "Ocorreu um erro. Tente novamente.", Snackbar.LENGTH_LONG).show()
            finish()
            return
        }

        // inicializa os dots
        pinDots = listOf(
            findViewById(R.id.pin_1),
            findViewById(R.id.pin_2),
            findViewById(R.id.pin_3),
            findViewById(R.id.pin_4),
            findViewById(R.id.pin_5),
            findViewById(R.id.pin_6)
        )
        setupNumberButtons()

        // botão para apagar o último dígito do PIN
        findViewById<ImageButton>(R.id.btnDelete).setOnClickListener {
            if (pin.isNotEmpty()) {
                pin.deleteCharAt(pin.length - 1)
                updatePinDots()
            }
        }
    }

    // inicializa os botões de número
    private fun setupNumberButtons() {
        val numberButtonClickListener = View.OnClickListener { view ->
            if (pin.length < 6) {
                val button = view as Button
                pin.append(button.text)
                updatePinDots()
                if (pin.length == 6) {
                    savePinWithApiAndNavigate()
                }
            }
        }

        // inicializa os botões de número
        val buttons = listOf<Button>(
            findViewById(R.id.button_1), findViewById(R.id.button_2), findViewById(R.id.button_3),
            findViewById(R.id.button_4), findViewById(R.id.button_5), findViewById(R.id.button_6),
            findViewById(R.id.button_7), findViewById(R.id.button_8), findViewById(R.id.button_9),
            findViewById(R.id.button_0)
        )
        buttons.forEach { it.setOnClickListener(numberButtonClickListener) }
    }

    // atualiza os dots do PIN
    private fun updatePinDots() {
        for (i in pinDots.indices) {
            if (i < pin.length) {
                pinDots[i].setImageResource(R.drawable.ic_pin_dot_depois)
            } else {
                pinDots[i].setImageResource(R.drawable.ic_pin_dot_antes)
            }
        }
    }

    private fun savePinWithApiAndNavigate() {
        val rootView = findViewById<android.view.View>(android.R.id.content)
        lifecycleScope.launch {
            try {
                val request = CreatePinRequest(email = userEmail, pin = pin.toString())
                val response = ApiClient.apiService.criarPin(request)

                if (response.isSuccessful) {
                    // guarda os dados da conta no novo formato (Nome:::Email)
                    val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    val registeredAccounts = sharedPrefs.getStringSet("REGISTERED_ACCOUNTS", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
                    val accountString = "$userName:::$userEmail"
                    registeredAccounts.add(accountString)

                    // guarda os dados no shared preferences
                    with(sharedPrefs.edit()) {
                        putStringSet("REGISTERED_ACCOUNTS", registeredAccounts)
                        apply()
                    }

                    // mensagem de sucesso
                    val successMessage = "PIN criado com sucesso"
                    Snackbar.make(rootView, successMessage, Snackbar.LENGTH_SHORT).addCallback(object : Snackbar.Callback() {
                        // redireciona para a página home
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            val intent = Intent(this@CreatePinActivity, HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    }).show()

                } else {
                    // mensagem de erro
                    val errorMessage = "Ocorreu um erro, o PIN não foi guardado"
                    Snackbar.make(rootView, errorMessage, Snackbar.LENGTH_LONG).show()
                    pin.clear()
                    updatePinDots()
                }

            } catch (e: Exception) {
                Log.e("CreatePinActivity", "Erro ao criar o PIN", e)
                val errorMessage = "Falha na ligação. Verifique a sua internet e tente novamente"
                Snackbar.make(rootView, errorMessage, Snackbar.LENGTH_LONG).show()
                pin.clear() // limpa o PIN
                updatePinDots() // atualiza os dots
            }
        }
    }
}
