package pt.ipt.dam2025.trabalho.ui.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
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
import pt.ipt.dam2025.trabalho.viewmodel.MarcarConsultaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MarcarConsultaActivity : AppCompatActivity() {

    private lateinit var spinnerClinica: Spinner
    private lateinit var spinnerVeterinario: Spinner
    private lateinit var etData: EditText
    private lateinit var etHora: EditText
    private lateinit var etAssunto: EditText
    private lateinit var btnConfirmar: Button

    private val viewModel: MarcarConsultaViewModel by viewModels()

    private var listaClinicas: List<Clinica> = emptyList()
    private var listaVeterinarios: List<Veterinario> = emptyList()

    // IDs reais
    private var animalId: Int = -1
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marcar_consulta)

        // Obter os IDs
        animalId = intent.getIntExtra("ANIMAL_ID", -1)
        val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getInt("LOGGED_IN_USER_ID", -1)

        // Validar se os IDs são válidos
        if (animalId == -1 || userId == -1) {
            Toast.makeText(this, "Erro: IDs de utilizador ou animal inválidos", Toast.LENGTH_LONG).show()
            finish()
            return
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
            val nomesClinicas = clinicas.map { it.nome }
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
                if (listaClinicas.isNotEmpty()) {
                    val clinicaSelecionada = listaClinicas[position]
                    viewModel.fetchVeterinariosPorClinica(clinicaSelecionada.id)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnConfirmar.setOnClickListener {
            marcarConsulta()
        }
    }

    private fun marcarConsulta() {
        val clinicaPosition = spinnerClinica.selectedItemPosition
        val veterinarioPosition = spinnerVeterinario.selectedItemPosition

        if (listaClinicas.isEmpty() || clinicaPosition < 0 || listaVeterinarios.isEmpty() || veterinarioPosition < 0) {
            Toast.makeText(this, "Por favor aguarde o carregamento e selecione uma clínica e veterinário", Toast.LENGTH_LONG).show()
            return
        }

        val clinicaSelecionada = listaClinicas[clinicaPosition]
        val veterinarioSelecionado = listaVeterinarios[veterinarioPosition]
        val data = etData.text.toString()
        val hora = etHora.text.toString()
        val motivo = etAssunto.text.toString()

        if (data.isEmpty() || hora.isEmpty() || motivo.isEmpty()) {
            Toast.makeText(this, "Por favor preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val novaConsulta = NovaConsulta(
            userId = this.userId, // <-- ID Real do Utilizador
            animalId = this.animalId, // <-- ID Real do Animal
            clinicaId = clinicaSelecionada.id,
            veterinarioId = veterinarioSelecionado.id,
            data = data,
            hora = hora,
            motivo = motivo
        )

        viewModel.marcarConsulta(novaConsulta)
    }

    private fun updateVeterinariosSpinner(veterinarios: List<Veterinario>) {
        val nomesVeterinarios = veterinarios.map { it.nome }
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
