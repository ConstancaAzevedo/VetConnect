package pt.ipt.dam2025.trabalho

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


//tela de verificação do número de telemóvel do tutor
class VerificTutorActivity : AppCompatActivity() {

    private val correctVerificationCode = "123456789" //código de verificação de exemplo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verific_tutor)

        val verificationCodeInput = findViewById<EditText>(R.id.verification_code_input) //input do código de verificação
        val verifyButton = findViewById<Button>(R.id.verify_button) //botão de verificação

        //botão de verificação é clicado
        verifyButton.setOnClickListener {
            val enteredCode = verificationCodeInput.text.toString()

            //se não introduzir nenhum código
            if (enteredCode.isEmpty()) {
                Toast.makeText(this, "Por favor, insira o código de verificação", Toast.LENGTH_SHORT).show()
            }

            // se o código não tiver 9 digitos
            if (enteredCode.length != 9) {
                Toast.makeText(this, "O código de verificação deve ter 9 dígitos", Toast.LENGTH_SHORT).show()
            }


            //verificar se o código de verificação está correto
            if (enteredCode == correctVerificationCode) {
                // 1. código correto - mensagem de sucesso
                Toast.makeText(this, "Verificação bem-sucedida!", Toast.LENGTH_SHORT).show()

                // 2. Ir para a HomeActivity
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                // 3. fechar a atividade para removê-la do histórico
                finish()
                
            } else {
                // 1. código incorreto - mensagem de erro
                Toast.makeText(this, "Código de verificação inválido", Toast.LENGTH_SHORT).show()
                // 2. limpar o campo de texto
                verificationCodeInput.text.clear()
            }
        }
    }
}
