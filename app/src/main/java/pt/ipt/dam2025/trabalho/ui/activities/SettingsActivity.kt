package pt.ipt.dam2025.trabalho.ui.activities

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.model.AlterarPinRequest

 // activity para a página de definições do utilizador

class SettingsActivity : AppCompatActivity() {

    private lateinit var currentPinEditText: EditText
    private lateinit var newPinEditText: EditText
    private lateinit var confirmNewPinEditText: EditText
    private lateinit var savePinButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        currentPinEditText = findViewById(R.id.current_pin)
        newPinEditText = findViewById(R.id.new_pin)
        confirmNewPinEditText = findViewById(R.id.confirm_new_pin)
        savePinButton = findViewById(R.id.save_pin_button)

        savePinButton.setOnClickListener {
            val currentPin = currentPinEditText.text.toString()
            val newPin = newPinEditText.text.toString()
            val confirmNewPin = confirmNewPinEditText.text.toString()

            var isValid = true

            if (currentPin.length != 6) {
                currentPinEditText.error = "O PIN atual deve ter 6 dígitos."
                isValid = false
            }

            if (newPin.length != 6) {
                newPinEditText.error = "O novo PIN deve ter 6 dígitos."
                isValid = false
            }

            if (confirmNewPin.length != 6) {
                confirmNewPinEditText.error = "A confirmação deve ter 6 dígitos."
                isValid = false
            }

            if (newPin != confirmNewPin) {
                confirmNewPinEditText.error = "Os novos PINs não coincidem."
                isValid = false
            }

            if (!isValid) return@setOnClickListener

            val sharedPrefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
            val authToken = sharedPrefs.getString("AUTH_TOKEN", null)

            if (authToken == null) {
                Toast.makeText(this, "Sessão inválida. Faça login novamente.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val request = AlterarPinRequest(pinAtual = currentPin, novoPin = newPin)
                    val response = ApiClient.apiService.alterarPin("Bearer $authToken", request)

                    if (response.isSuccessful) {
                        Toast.makeText(this@SettingsActivity, "PIN alterado com sucesso!", Toast.LENGTH_SHORT).show()
                        finish() // Fecha a atividade de definições
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(this@SettingsActivity, "Erro: ${errorBody ?: "Não foi possível alterar o PIN"}", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@SettingsActivity, "Falha na comunicação: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}