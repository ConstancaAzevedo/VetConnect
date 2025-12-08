package pt.ipt.dam2025.trabalho.ui.activities

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
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.model.CreatePinRequest

class CreatePinActivity : AppCompatActivity() {

    private val pin = StringBuilder()
    private lateinit var pinDots: List<ImageView>
    private lateinit var userName: String
    private lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_pin)
        val rootView = findViewById<android.view.View>(android.R.id.content)

        // Lê os dados passados pelo ecrã de verificação
        userName = intent.getStringExtra("USER_NAME") ?: ""
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        if (userName.isEmpty() || userEmail.isEmpty()) {
            Snackbar.make(rootView, "Ocorreu um erro. Tente novamente.", Snackbar.LENGTH_LONG).show()
            finish()
            return
        }

        pinDots = listOf(
            findViewById(R.id.pin_dot_1),
            findViewById(R.id.pin_dot_2),
            findViewById(R.id.pin_dot_3),
            findViewById(R.id.pin_dot_4),
            findViewById(R.id.pin_dot_5),
            findViewById(R.id.pin_dot_6)
        )

        setupNumberButtons()

        findViewById<ImageButton>(R.id.button_delete).setOnClickListener {
            if (pin.isNotEmpty()) {
                pin.deleteCharAt(pin.length - 1)
                updatePinDots()
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
                    savePinWithApiAndNavigate()
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

    private fun savePinWithApiAndNavigate() {
        val rootView = findViewById<android.view.View>(android.R.id.content)
        lifecycleScope.launch {
            try {
                val request = CreatePinRequest(nome = userName, pin = pin.toString())
                val response = ApiClient.apiService.criarPin(request)

                // Guarda os dados da conta no novo formato (Nome:::Email)
                val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val registeredAccounts = sharedPrefs.getStringSet("REGISTERED_ACCOUNTS", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
                val accountString = "$userName:::$userEmail"
                registeredAccounts.add(accountString)

                with(sharedPrefs.edit()) {
                    putStringSet("REGISTERED_ACCOUNTS", registeredAccounts)
                    apply()
                }

                Snackbar.make(rootView, response.message, Snackbar.LENGTH_SHORT).addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        val intent = Intent(this@CreatePinActivity, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }).show()

            } catch (e: Exception) {
                Log.e("CreatePinActivity", "Erro ao criar o PIN", e)
                val errorMessage = e.message ?: "Não foi possível criar o PIN. Tente novamente."
                Snackbar.make(rootView, errorMessage, Snackbar.LENGTH_LONG).show()
                pin.clear()
                updatePinDots()
            }
        }
    }
}
