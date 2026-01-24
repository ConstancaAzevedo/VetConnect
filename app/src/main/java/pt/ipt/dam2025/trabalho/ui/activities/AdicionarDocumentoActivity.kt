package pt.ipt.dam2025.trabalho.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import pt.ipt.dam2025.trabalho.R
import pt.ipt.dam2025.trabalho.model.QrCodePayload
import pt.ipt.dam2025.trabalho.viewmodel.AdicionarDocumentoViewModel
import pt.ipt.dam2025.trabalho.viewmodel.AdicionarDocumentoViewModelFactory


// Activity para adicionar um documento (receita, vacina ou exame)
class AdicionarDocumentoActivity : AppCompatActivity() {


    // ViewModel para esta Activity
    private val viewModel: AdicionarDocumentoViewModel by viewModels { AdicionarDocumentoViewModelFactory(application) }

    private lateinit var spinnerTipoDocumento: Spinner
    private lateinit var layoutReceita: LinearLayout
    private lateinit var layoutVacina: LinearLayout
    private lateinit var layoutExame: LinearLayout
    private lateinit var buttonAdicionar: Button

    // Campos de data
    private lateinit var editTextData: EditText

    // Campos da Receita
    private lateinit var editTextMedicamento: EditText
    private lateinit var editTextPosologia: EditText
    private lateinit var editTextMedicoReceita: EditText

    // Campos da Vacina
    private lateinit var editTextNomeVacina: EditText
    private lateinit var editTextLote: EditText
    private lateinit var editTextProximaDose: EditText

    // Campos do Exame
    private lateinit var editTextTipoExame: EditText
    private lateinit var editTextResultadoExame: EditText
    private lateinit var editTextLaboratorio: EditText

    private var animalId: Int = -1
    private var authToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_documento)

        animalId = intent.getIntExtra("ANIMAL_ID", -1)
        val sharedPrefs = getSharedPreferences("prefs", MODE_PRIVATE)
        authToken = sharedPrefs.getString("AUTH_TOKEN", null)

        if (animalId == -1 || authToken == null) {
            Toast.makeText(this, "Erro: Sessão ou animal inválido.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupViews()
        setupSpinner()
        setupButton()
        observeViewModel()
    }


    // Configuração das views
    private fun setupViews() {
        spinnerTipoDocumento = findViewById(R.id.spinner_tipo_documento)
        layoutReceita = findViewById(R.id.layout_receita)
        layoutVacina = findViewById(R.id.layout_vacina)
        layoutExame = findViewById(R.id.layout_exame)
        buttonAdicionar = findViewById(R.id.button_adicionar_documento)

        // Campos de data
        editTextData = findViewById(R.id.edit_text_data)

        // Campos da Receita
        editTextMedicamento = findViewById(R.id.edit_text_medicamento)
        editTextPosologia = findViewById(R.id.edit_text_posologia)
        editTextMedicoReceita = findViewById(R.id.edit_text_medico_receita)

        // Campos da Vacina
        editTextNomeVacina = findViewById(R.id.edit_text_nome_vacina)
        editTextLote = findViewById(R.id.edit_text_lote)
        editTextProximaDose = findViewById(R.id.edit_text_proxima_dose)

        // Campos do Exame
        editTextTipoExame = findViewById(R.id.edit_text_tipo_exame)
        editTextResultadoExame = findViewById(R.id.edit_text_resultado_exame)
        editTextLaboratorio = findViewById(R.id.edit_text_laboratorio)
    }

    // Configuração do Spinner para escolher o tipo de documento

    private fun setupSpinner() {
        val tiposDocumento = arrayOf("Receita", "Vacina", "Exame")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposDocumento)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoDocumento.adapter = adapter

        spinnerTipoDocumento.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (parent.getItemAtPosition(position).toString()) {
                    "Receita" -> {
                        layoutReceita.visibility = View.VISIBLE
                        layoutVacina.visibility = View.GONE
                        layoutExame.visibility = View.GONE
                    }
                    "Vacina" -> {
                        layoutReceita.visibility = View.GONE
                        layoutVacina.visibility = View.VISIBLE
                        layoutExame.visibility = View.GONE
                    }
                    "Exame" -> {
                        layoutReceita.visibility = View.GONE
                        layoutVacina.visibility = View.GONE
                        layoutExame.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) { }
        }
    }

    // Configuração do botão para adicionar o documento
    private fun setupButton() {
        buttonAdicionar.setOnClickListener {
            val tipo = spinnerTipoDocumento.selectedItem.toString().lowercase()
            val data = editTextData.text.toString()
            val jsonPayload = when (tipo) {
                "receita" -> {
                    "{\"tipo\":\"$tipo\",\"animalId\":$animalId,\"medicamento\":\"${editTextMedicamento.text}\",\"posologia\":\"${editTextPosologia.text}\",\"medico\":\"${editTextMedicoReceita.text}\",\"data\":\"$data\"}"
                }
                "vacina" -> {
                    "{\"tipo\":\"$tipo\",\"animalId\":$animalId,\"nomeVacina\":\"${editTextNomeVacina.text}\",\"lote\":\"${editTextLote.text}\",\"proximaDose\":\"${editTextProximaDose.text}\",\"data\":\"$data\"}"
                }
                "exame" -> {
                    "{\"tipo\":\"$tipo\",\"animalId\":$animalId,\"tipoExame\":\"${editTextTipoExame.text}\",\"resultado\":\"${editTextResultadoExame.text}\",\"laboratorio\":\"${editTextLaboratorio.text}\",\"data\":\"$data\"}"
                }
                else -> null
            }

            if (jsonPayload != null && authToken != null) {
                viewModel.adicionarDocumento(authToken!!, QrCodePayload(jsonPayload))
            } else {
                Toast.makeText(this, "Erro ao criar documento", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Observa as mudanças no ViewModel
    private fun observeViewModel() {
        viewModel.documentoAdicionado.observe(this, Observer { sucesso ->
            if (sucesso) {
                Toast.makeText(this, "Documento adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Falha ao adicionar documento.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
