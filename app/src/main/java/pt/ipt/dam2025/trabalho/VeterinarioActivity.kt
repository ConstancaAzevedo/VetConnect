package pt.ipt.dam2025.trabalho

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

//tela de autenticação do veterinário
class VeterinarioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autenticacao_vet)

        val cedulaInput = findViewById<EditText>(R.id.phone_number_input)
        val loginButton = findViewById<Button>(R.id.login_vet_button)

        loginButton.setOnClickListener {
            val cedula = cedulaInput.text.toString()

            // inserir cedula da ordem de médicos
            if (cedula.isBlank()) {
                cedulaInput.error = "Por favor, insira o número da cédula."
            } else {
                // TODO: Adicionar lógica real de autenticação com API
                // login com PIN
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // Finaliza a activity para não voltar a ela com o botão "back"
            }
        }
    }
}
