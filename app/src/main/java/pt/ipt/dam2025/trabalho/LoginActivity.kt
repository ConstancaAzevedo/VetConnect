package pt.ipt.dam2025.trabalho

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.lang.StringBuilder


//tela de login após primeira abertura do app
class LoginActivity : AppCompatActivity() {

    //variável privada para armazenar o PIN inserido pelo utilizador
    private val pin = StringBuilder()

    //lista de pontos do PIN
    private lateinit var pinDots: List<ImageView>

    private val correctPin = "123456" // PIN de exemplo

    private val nome = "Constança" // nome de exemplo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // adicionar o nome do utilizador ao texto de entrada
        val welcomeTextView = findViewById<TextView>(R.id.welcome_text)
        val userName = nome // TODO: Este nome deve vir da autenticação real
        welcomeTextView.text = "Olá, $userName"


        // lista de pontos do PIN
        pinDots = listOf(
            findViewById(R.id.pin_dot_1),
            findViewById(R.id.pin_dot_2),
            findViewById(R.id.pin_dot_3),
            findViewById(R.id.pin_dot_4),
            findViewById(R.id.pin_dot_5),
            findViewById(R.id.pin_dot_6)
        )

        // listener para os botões dos números
        setupNumberButtons()

        // quando o botão de apagar é clicado
        findViewById<ImageButton>(R.id.button_delete).setOnClickListener {
            if (pin.isNotEmpty()) {
                pin.deleteCharAt(pin.length - 1)
                updatePinDots()
            }
        }

        // quando o botão "Esqueci o PIN" é clicado
        findViewById<TextView>(R.id.text_forgot_pin).setOnClickListener {
            Toast.makeText(this, "Funcionalidade a ser implementada", Toast.LENGTH_SHORT).show()
        }
    }

    // configuração dos botões numéricos
    private fun setupNumberButtons() {
        val numberButtonClickListener = View.OnClickListener { view ->
            //verificar se o PIN ainda não atingiu o comprimento máximo de 6 dígitos
            if (pin.length < 6) {
                val button = view as Button
                pin.append(button.text)
                updatePinDots()
                // se o PIN atingir 6 dígitos após adicionar o novo número
                if (pin.length == 6) {
                    attemptLogin()
                }
            }
        }

        // associar o listener acima a todos os botões numéricos
        findViewById<Button>(R.id.button_1).setOnClickListener(numberButtonClickListener)
        findViewById<Button>(R.id.button_2).setOnClickListener(numberButtonClickListener)
        findViewById<Button>(R.id.button_3).setOnClickListener(numberButtonClickListener)
        findViewById<Button>(R.id.button_4).setOnClickListener(numberButtonClickListener)
        findViewById<Button>(R.id.button_5).setOnClickListener(numberButtonClickListener)
        findViewById<Button>(R.id.button_6).setOnClickListener(numberButtonClickListener)
        findViewById<Button>(R.id.button_7).setOnClickListener(numberButtonClickListener)
        findViewById<Button>(R.id.button_8).setOnClickListener(numberButtonClickListener)
        findViewById<Button>(R.id.button_9).setOnClickListener(numberButtonClickListener)
        findViewById<Button>(R.id.button_0).setOnClickListener(numberButtonClickListener)
    }

    // função para atualizar o estado dos pontos do PIN (antes e depois)
    private fun updatePinDots() {
        for (i in pinDots.indices) {
            if (i < pin.length) {
                pinDots[i].setImageResource(R.drawable.ic_pin_dot_depois)
            } else {
                pinDots[i].setImageResource(R.drawable.ic_pin_dot_antes)
            }
        }
    }

    // função para verificar se o PIN inserido está correto e fazer login
    private fun attemptLogin() {
        if (pin.toString() == correctPin) { // // Compara o PIN inserido (convertido para String) com o PIN correto
            // 1. mensagem de sucesso
            Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
            // 2. navegar para a HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Impede o utilizador de voltar a este ecrã com o botão "back"
        } else { // PIN errado
            // 1. mensagem de insucesso
            Toast.makeText(this, "PIN incorreto", Toast.LENGTH_SHORT).show()
            // 2. limpar o PIN para uma nova tentativa
            pin.clear()
            updatePinDots()
        }
    }
}
