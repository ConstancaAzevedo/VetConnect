package pt.ipt.dam2025.trabalho

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


//tela do perfil do utilizador
class PerfilTutorActivity : AppCompatActivity() {
    private var isEditing = false // inicia os campos de texto como não podem ser editados

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_tutor)

        // configurações do botões e campos de texto da página
        val btnEditarGuardar = findViewById<Button>(R.id.btnEditarGuardar)
        val etNome = findViewById<EditText>(R.id.etNome)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etTelemovel = findViewById<EditText>(R.id.etTelemovel)
        val etNacionalidade = findViewById<EditText>(R.id.etNacionalidade)
        val etSexo = findViewById<EditText>(R.id.etSexo)
        val etCC = findViewById<EditText>(R.id.etCC)
        val etDataNascimento = findViewById<EditText>(R.id.etDataNascimento)
        val etMorada = findViewById<EditText>(R.id.etMorada)


        // habilitar ou desabilitar os campos de texto
        val editableFields = listOf(
            etNome, etEmail, etTelemovel, etNacionalidade,
            etSexo, etCC, etDataNascimento, etMorada
        )


        // quando o botão de editar for clicado
        btnEditarGuardar.setOnClickListener {
            isEditing = !isEditing
            if (isEditing) {
                btnEditarGuardar.text = "GUARDAR"
                editableFields.forEach { it.isEnabled = true }
            } else {
                btnEditarGuardar.text = "EDITAR"
                editableFields.forEach { it.isEnabled = false }


                //adicionar a lógica para guardar os dados
            }
        }

    }
}