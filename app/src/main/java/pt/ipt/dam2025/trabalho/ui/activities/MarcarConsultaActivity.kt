package pt.ipt.dam2025.trabalho.ui.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.model.Clinica
import pt.ipt.dam2025.trabalho.model.NovaConsulta
import pt.ipt.dam2025.trabalho.model.Veterinario
import pt.ipt.dam2025.trabalho.util.SessionManager
import pt.ipt.dam2025.trabalho.viewmodel.MarcarConsultaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Activity para marcar uma consulta
class MarcarConsultaActivity : AppCompatActivity() {

    private lateinit var spinnerClinica: Spinner
    private lateinit var spinnerVeterinario: Spinner
    private lateinit var etData: EditText
    private lateinit var etHora: EditText
    private lateinit var etAssunto: EditText
    private lateinit var btnConfirmar: Button

    private val viewModel: MarcarConsultaViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    private var listaClinicas: List<Clinica> = emptyList()
    private var listaVeterinarios: List<Veterinario> = emptyList()

    private var animalId: Int = -1
    private var userId: Int = -1
    private var authToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marcar_consulta)

        sessionManager = SessionManager(this)

        // Obter IDs a partir do SessionManager
        animalId = sessionManager.getAnimalId()
        userId = sessionManager.getUserId()
        authToken = sessionManager.getAuthToken()

        // Validar se a SESSÃO do UTILIZADOR é válida (token e userId)
        // O animalId pode ser validado apenas no momento de marcar, se necessário
        if (userId == -1 || authToken == null) {
            Toast.makeText(this, "Erro de sessão. Por favor, faça login novamente.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        // Se o animalId for -1, avisamos mas não expulsamos o utilizador imediatamente
        if (animalId == -1) {
            Toast.makeText(this, "Aviso: Nenhum animal selecionado.", Toast.LENGTH_SHORT).show()
        }

        // Inicializar as vistas
        spinnerClinica = findViewById(R.id.spinnerClinica)
        spinnerVeterinario = findViewById(R.id.spinnerVeterinario)
        etData = findViewById(R.id.etData)
        etHora = findViewById(R.id.etHora)
        etAssunto = findViewById(R.id.etAssunto)
        btnConfirmar = findViewById(R.id.btnConfirmar)

        // Configurar observadores e lógicas
        observeViewModel()
        setupDateTimePickers()
        setupListeners()
    }

    private fun observeViewModel() {
        viewModel.clinicas.observe(this) { clinicas ->
            listaClinicas = clinicas
            val nomesClinicas = listOf("Selecione uma clínica") + clinicas.map { it.nome }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nomesClinicas)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerClinica.adapter = adapter
        }

        viewModel.veterinarios.observe(this) { veterinarios ->
            listaVeterinarios = veterinarios
            updateVeterinariosSpinner(veterinarios)
        }

        viewModel.consultaResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Consulta marcada com sucesso", Toast.LENGTH_LONG).show()
                finish()
            }.onFailure { exception ->
                val errorMessage = exception.message ?: "Erro desconhecido"
                Toast.makeText(this, "Erro ao marcar consulta: $errorMessage", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupListeners() {
        spinnerClinica.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0 && listaClinicas.isNotEmpty()) {
                    val clinicaSelecionada = listaClinicas[position - 1]
                    viewModel.fetchVeterinariosPorClinica(clinicaSelecionada.id)
                } else {
                    updateVeterinariosSpinner(emptyList())
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnConfirmar.setOnClickListener {
            marcarConsulta()
        }
    }

    private fun marcarConsulta() {
        if (animalId == -1) {
            Toast.makeText(this, "Erro: Não é possível marcar consulta sem um animal selecionado.", Toast.LENGTH_LONG).show()
            return
        }

        val clinicaPosition = spinnerClinica.selectedItemPosition
        val veterinarioPosition = spinnerVeterinario.selectedItemPosition
        val data = etData.text.toString()
        val hora = etHora.text.toString()
        val motivo = etAssunto.text.toString().trim()

        var isValid = true

        if (clinicaPosition <= 0) {
            Toast.makeText(this, "Selecione uma clínica", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (listaVeterinarios.isEmpty() || veterinarioPosition <= 0) {
            Toast.makeText(this, "Selecione um veterinário", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (data.isEmpty()) {
            etData.error = "A data é obrigatória"
            isValid = false
        }

        if (hora.isEmpty()) {
            etHora.error = "A hora é obrigatória"
            isValid = false
        }

        if (motivo.isEmpty()) {
            etAssunto.error = "O motivo é obrigatório"
            isValid = false
        }

        if (!isValid) return

        val clinicaSelecionada = listaClinicas[clinicaPosition - 1]
        val veterinarioSelecionado = listaVeterinarios[veterinarioPosition - 1]

        val novaConsulta = NovaConsulta(
            animalId = this.animalId,
            clinicaId = clinicaSelecionada.id,
            veterinarioId = veterinarioSelecionado.id,
            data = data,
            hora = hora,
            motivo = motivo
        )

        viewModel.marcarConsulta(authToken!!, novaConsulta)
    }

    private fun updateVeterinariosSpinner(veterinarios: List<Veterinario>) {
        val nomesVeterinarios = listOf("Selecione um veterinário") + veterinarios.map { it.nome }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nomesVeterinarios)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerVeterinario.adapter = adapter
    }

    private fun setupDateTimePickers() {
        val calendar = Calendar.getInstance()

        etData.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, day)
                val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                etData.setText(apiFormat.format(selectedDate.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        etHora.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                val time = String.format("%02d:%02d:00", hour, minute)
                etHora.setText(time)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }
    }
}