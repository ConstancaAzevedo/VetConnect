package pt.ipt.dam2025.trabalho.ui.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.model.User
import pt.ipt.dam2025.trabalho.viewmodel.UsuarioViewModel

class PerfilTutorActivity : AppCompatActivity() {
    private var isEditing = false
    private val viewModel: UsuarioViewModel by viewModels()

    private lateinit var etNome: EditText
    private lateinit var etEmail: EditText
    private lateinit var etTelemovel: EditText
    private lateinit var etNacionalidade: EditText
    private lateinit var etSexo: EditText
    private lateinit var etCC: EditText
    private lateinit var etDataNascimento: EditText
    private lateinit var etMorada: EditText
    private lateinit var editableFields: List<EditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_tutor)

        // Inicialização das Vistas
        val btnEditarGuardar = findViewById<Button>(R.id.btnEditarGuardar)
        etNome = findViewById(R.id.etNome)
        etEmail = findViewById(R.id.etEmail)
        etTelemovel = findViewById(R.id.etTelemovel)
        etNacionalidade = findViewById(R.id.etNacionalidade)
        etSexo = findViewById(R.id.etSexo)
        etCC = findViewById(R.id.etCC)
        etDataNascimento = findViewById(R.id.etDataNascimento)
        etMorada = findViewById(R.id.etMorada)

        editableFields = listOf(
            etNome, etEmail, etTelemovel, etNacionalidade,
            etSexo, etCC, etDataNascimento, etMorada
        )

        // Inicia os campos como desativados
        editableFields.forEach { it.isEnabled = false }

        // Observa os dados do utilizador e preenche a UI
        viewModel.user.observe(this) { user ->
            user?.let { // Garante que o utilizador não é nulo
                etNome.setText(it.nome)
                etEmail.setText(it.email)
                etTelemovel.setText(it.telemovel ?: "")
                etNacionalidade.setText(it.nacionalidade ?: "")
                etSexo.setText(it.sexo ?: "")
                etCC.setText(it.cc ?: "")
                etDataNascimento.setText(it.dataNascimento ?: "")
                etMorada.setText(it.morada ?: "")
            }
        }

        btnEditarGuardar.setOnClickListener {
            isEditing = !isEditing
            if (isEditing) {
                // Entra em modo de edição
                btnEditarGuardar.text = "GUARDAR"
                editableFields.forEach { it.isEnabled = true }
            } else {
                // Sai do modo de edição e guarda os dados
                btnEditarGuardar.text = "EDITAR"
                editableFields.forEach { it.isEnabled = false }

                // Recolhe os dados e chama o ViewModel para atualizar
                val currentUser = viewModel.user.value
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(
                        nome = etNome.text.toString(),
                        email = etEmail.text.toString(),
                        telemovel = etTelemovel.text.toString().takeIf { it.isNotBlank() },
                        nacionalidade = etNacionalidade.text.toString().takeIf { it.isNotBlank() },
                        sexo = etSexo.text.toString().takeIf { it.isNotBlank() },
                        cc = etCC.text.toString().takeIf { it.isNotBlank() },
                        dataNascimento = etDataNascimento.text.toString().takeIf { it.isNotBlank() },
                        morada = etMorada.text.toString().takeIf { it.isNotBlank() }
                    )
                    viewModel.updateUser(updatedUser)
                    Toast.makeText(this, "Perfil atualizado!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Erro: Não foi possível guardar. Tente novamente.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ao voltar à atividade atualiza os dados do utilizador que tem sessão iniciada
    override fun onResume() {
        super.onResume()
        viewModel.loadCurrentUser()
    }
}
