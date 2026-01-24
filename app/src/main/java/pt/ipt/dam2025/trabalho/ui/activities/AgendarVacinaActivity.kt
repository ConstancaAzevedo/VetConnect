package pt.ipt.dam2025.trabalho.ui.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.model.AgendarVacinaRequest
import pt.ipt.dam2025.trabalho.model.AnimalResponse
import pt.ipt.dam2025.trabalho.model.TipoVacina
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Activity para agendar uma vacina
class AgendarVacinaActivity : AppCompatActivity() {
// Activity para agendar uma vacina
    private lateinit var spinnerAnimal: Spinner
    private lateinit var spinnerTipoVacina: Spinner
    private lateinit var etDataHora: EditText
    private lateinit var etObservacoes: EditText
    private lateinit var btnAgendar: Button

    // Lista de animais e tipos de vacinas
    private var listaAnimais: List<AnimalResponse> = emptyList()
    private var listaTiposVacina: List<TipoVacina> = emptyList()
    private val calendar: Calendar = Calendar.getInstance()

    // IDs do usuário e token de autenticação
    private var userId: Int = -1
    private var authToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendar_vacina)

        // Carregar IDs do usuário e token de autenticação
        val sharedPrefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getInt("USER_ID", -1)
        authToken = sharedPrefs.getString("AUTH_TOKEN", null)

        // Verificar se os IDs do usuário e token de autenticação são válidos
        if (userId == -1 || authToken == null) {
            Toast.makeText(this, "Erro: Sessão inválida.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupUI()
        loadInitialData()
    }

    // Configuração da UI
    private fun setupUI() {
        spinnerAnimal = findViewById(R.id.spinner_animal_vacina)
        spinnerTipoVacina = findViewById(R.id.spinner_tipo_vacina)
        etDataHora = findViewById(R.id.et_data_hora_vacina)
        etObservacoes = findViewById(R.id.et_observacoes_vacina)
        btnAgendar = findViewById(R.id.btn_agendar_vacina)

        etDataHora.setOnClickListener {
            showDateTimePicker()
        }

        btnAgendar.setOnClickListener {
            agendarVacina()
        }
    }

    // Carregar dados iniciais
    private fun loadInitialData() {
        lifecycleScope.launch {
            // Carregar animais e tipos de vacinas
            try {
                val animaisResponse = ApiClient.apiService.getAnimaisDoTutor("Bearer $authToken", userId)
                if (animaisResponse.isSuccessful) {
                    listaAnimais = animaisResponse.body() ?: emptyList()
                    val nomesAnimais = listOf("Selecione um animal") + listaAnimais.map { it.nome }
                    val animalAdapter = ArrayAdapter(this@AgendarVacinaActivity, android.R.layout.simple_spinner_item, nomesAnimais)
                    animalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerAnimal.adapter = animalAdapter
                }
            } catch (e: Exception) { Log.e("AgendarVacinaActivity", "Falha ao carregar animais", e) }

            // Carregar tipos de vacinas
            try {
                val tiposResponse = ApiClient.apiService.getTiposVacina()
                if (tiposResponse.isSuccessful) {
                    listaTiposVacina = tiposResponse.body()?.tipos ?: emptyList()
                    val nomesTiposVacina = listOf("Selecione um tipo de vacina") + listaTiposVacina.map { it.nome }
                    val tipoVacinaAdapter = ArrayAdapter(this@AgendarVacinaActivity, android.R.layout.simple_spinner_item, nomesTiposVacina)
                    tipoVacinaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerTipoVacina.adapter = tipoVacinaAdapter
                }
            } catch (e: Exception) { Log.e("AgendarVacinaActivity", "Falha ao carregar tipos de vacina", e) }
        }
    }

    // Mostrar o date picker

    private fun showDateTimePicker() {
        DatePickerDialog(this, { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            TimePickerDialog(this, { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                etDataHora.setText(format.format(calendar.time))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    // Agendar a vacina
    private fun agendarVacina() {
        val animalPosition = spinnerAnimal.selectedItemPosition
        val tipoVacinaPosition = spinnerTipoVacina.selectedItemPosition
        val dataAgendada = etDataHora.text.toString()

        // Verificar se os campos obrigatórios foram preenchidos
        if (animalPosition <= 0 || tipoVacinaPosition <= 0 || dataAgendada.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        // Construir o objeto AgendarVacinaRequest
        val request = AgendarVacinaRequest(
            animalId = listaAnimais[animalPosition - 1].id,
            tipoVacinaId = listaTiposVacina[tipoVacinaPosition - 1].id,
            dataAgendada = dataAgendada,
            observacoes = etObservacoes.text.toString().trim()
        )

        // Chamar a API para agendar a vacina
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.agendarVacina("Bearer $authToken", request)
                if (response.isSuccessful) {
                    Toast.makeText(this@AgendarVacinaActivity, "Vacina agendada com sucesso!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@AgendarVacinaActivity, "Erro: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AgendarVacinaActivity, "Falha na comunicação.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}