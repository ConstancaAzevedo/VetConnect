
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
import pt.ipt.dam2025.trabalho.model.Vacina
import pt.ipt.dam2025.trabalho.model.TipoVacina
import pt.ipt.dam2025.trabalho.model.UpdateVacinaRequest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Activity para editar os detalhes de uma vacina
class EditarVacinaActivity : AppCompatActivity() {

    private lateinit var spinnerAnimal: Spinner
    private lateinit var spinnerTipoVacina: Spinner
    private lateinit var etDataHora: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnApagar: Button

    private var listaTiposVacina: List<TipoVacina> = emptyList()
    private val calendar: Calendar = Calendar.getInstance()

    private var authToken: String? = null
    private var vacina: Vacina? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_vacina)

        val sharedPrefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        authToken = sharedPrefs.getString("AUTH_TOKEN", null)

        if (authToken == null) {
            Toast.makeText(this, "Erro: Sessão inválida.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        vacina = intent.getSerializableExtra("VACINA_EXTRA") as? Vacina

        if (vacina == null) {
            Toast.makeText(this, "Erro: Não foi possível carregar os dados da vacina.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupUI()
        loadInitialData()
    }

    private fun setupUI() {
        spinnerAnimal = findViewById(R.id.spinner_animal_vacina)
        spinnerTipoVacina = findViewById(R.id.spinner_tipo_vacina)
        etDataHora = findViewById(R.id.et_data_hora_vacina)
        btnGuardar = findViewById(R.id.btn_guardar_alteracoes_vacina)
        btnApagar = findViewById(R.id.btn_apagar_vacina)

        etDataHora.setOnClickListener {
            showDateTimePicker()
        }

        btnGuardar.setOnClickListener {
            guardarAlteracoes()
        }

        btnApagar.setOnClickListener {
            apagarVacina()
        }
    }

    private fun populateData() {
        vacina?.let {
            val animalAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf(it.animalNome ?: "Animal (ID: ${it.animalId})"))
            spinnerAnimal.adapter = animalAdapter
            spinnerAnimal.isEnabled = false

            val tipoPosition = listaTiposVacina.indexOfFirst { tipo -> tipo.nome == it.tipo }
            if (tipoPosition != -1) {
                spinnerTipoVacina.setSelection(tipoPosition + 1)
            }

            etDataHora.setText(it.dataAgendada?.substringBefore("."))

            try {
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date = format.parse(it.dataAgendada)
                calendar.time = date
            } catch (e: Exception) {
                Log.e("EditarVacinaActivity", "Falha ao fazer parse da data", e)
            }
        }
    }

    private fun loadInitialData() {
        lifecycleScope.launch {
             try {
                val tiposResponse = ApiClient.apiService.getTiposVacina()
                if (tiposResponse.isSuccessful) {
                    listaTiposVacina = tiposResponse.body()?.tipos ?: emptyList()
                    val nomesTiposVacina = listOf("Selecione um tipo de vacina") + listaTiposVacina.map { it.nome }
                    val tipoVacinaAdapter = ArrayAdapter(this@EditarVacinaActivity, android.R.layout.simple_spinner_item, nomesTiposVacina)
                    tipoVacinaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerTipoVacina.adapter = tipoVacinaAdapter

                    populateData()
                }
            } catch (e: Exception) {
                Log.e("EditarVacinaActivity", "Falha ao carregar tipos de vacina", e)
                populateData()
            }
        }
    }

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

    private fun guardarAlteracoes() {
        val dataAgendada = etDataHora.text.toString()

        if (dataAgendada.isEmpty()) {
            Toast.makeText(this, "Preencha a data", Toast.LENGTH_SHORT).show()
            return
        }

        val request = UpdateVacinaRequest(
            dataAplicacao = dataAgendada,
            dataProxima = null, // You may want to add a field for this
            observacoes = ""
        )

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.updateVacina("Bearer $authToken", vacina!!.id.toInt(), request)
                if (response.isSuccessful) {
                    Toast.makeText(this@EditarVacinaActivity, "Vacina atualizada com sucesso!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@EditarVacinaActivity, "Erro: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditarVacinaActivity, "Falha na comunicação.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun apagarVacina() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.cancelarVacina("Bearer $authToken", vacina!!.id.toInt())
                if (response.isSuccessful) {
                    Toast.makeText(this@EditarVacinaActivity, "Vacina apagada com sucesso!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                     Toast.makeText(this@EditarVacinaActivity, "Erro ao apagar: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditarVacinaActivity, "Falha na comunicação ao apagar.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
