package pt.ipt.dam2025.vetconnect.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipt.dam2025.vetconnect.databinding.FragmentEditarVacinaBinding
import pt.ipt.dam2025.vetconnect.model.*
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Fragment da página para editar uma vacina 
 */

class EditarVacinaFragment : Fragment() {

    private var _binding: FragmentEditarVacinaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: VacinaViewModel
    private var vacina: Vacina? = null

    // Listas para os spinners
    private val tiposVacinaList = mutableListOf<TipoVacina>()
    private val clinicasList = mutableListOf<Clinica>()
    private val veterinariosList = mutableListOf<Veterinario>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarVacinaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = VacinaViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[VacinaViewModel::class.java]

        // Recebe o objeto Vacina completo
        @Suppress("DEPRECATION")
        vacina = arguments?.getParcelable("vacina")

        if (vacina == null) {
            Toast.makeText(context, "Erro ao carregar dados da vacina.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        setupListeners()
        observeLists()
        observeViewModel() // Função estava por chamar
        populateUi(vacina!!)
    }

    // Preenche os campos de texto iniciais
    private fun populateUi(vacina: Vacina) {
        binding.dataAplicacao.setText(formatDate(vacina.dataAplicacao))
        binding.editObservacoes.setText(vacina.observacoes)
    }

    // Configura os listeners dos botões e spinners
    private fun setupListeners() {
        binding.dataAplicacao.setOnClickListener { showDatePickerDialog(binding.dataAplicacao) }
        binding.buttonGuardarAlteracoes.setOnClickListener { guardarAlteracoes() }

        binding.spinnerClinica.setOnItemClickListener { _, _, position, _ ->
            val selectedClinica = clinicasList.getOrNull(position)
            selectedClinica?.id?.let { viewModel.carregaVeterinarios(it) }
        }
    }

    // Observa as listas que vêm do ViewModel e popula os spinners
    private fun observeLists() {
        viewModel.tiposVacina.observe(viewLifecycleOwner) { tipos ->
            tiposVacinaList.clear()
            tiposVacinaList.addAll(tipos)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, tipos.map { it.nome })
            binding.spinnerNomeVacina.setAdapter(adapter)

            // Pré-seleciona o tipo de vacina
            val tipoVacinaAtual = tipos.find { it.id == vacina?.tipoVacinaId }
            binding.spinnerNomeVacina.setText(tipoVacinaAtual?.nome, false)
        }

        viewModel.clinicas.observe(viewLifecycleOwner) { clinicas ->
            clinicasList.clear()
            clinicasList.addAll(clinicas)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, clinicas.map { it.nome })
            binding.spinnerClinica.setAdapter(adapter)

            // Pré-seleciona a clínica e carrega os veterinários
            val clinicaAtual = clinicas.find { it.id == vacina?.clinicaId }
            binding.spinnerClinica.setText(clinicaAtual?.nome, false)
            clinicaAtual?.id?.let { viewModel.carregaVeterinarios(it) }
        }

        viewModel.veterinarios.observe(viewLifecycleOwner) { veterinarios ->
            veterinariosList.clear()
            veterinariosList.addAll(veterinarios)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, veterinarios.map { it.nome })
            binding.spinnerVeterinario.setAdapter(adapter)

            // Pré-seleciona o veterinário
            val veterinarioAtual = veterinarios.find { it.id == vacina?.veterinarioId }
            binding.spinnerVeterinario.setText(veterinarioAtual?.nome, false)
        }
    }

    // Guarda as alterações feitas
    private fun guardarAlteracoes() {
        val dataAplicacao = binding.dataAplicacao.text.toString()
        val observacoes = binding.editObservacoes.text.toString()

        val tipoVacinaNome = binding.spinnerNomeVacina.text.toString()
        val clinicaNome = binding.spinnerClinica.text.toString()
        val veterinarioNome = binding.spinnerVeterinario.text.toString()

        val tipoVacinaId = tiposVacinaList.find { it.nome == tipoVacinaNome }?.id
        val clinicaId = clinicasList.find { it.nome == clinicaNome }?.id
        val veterinarioId = veterinariosList.find { it.nome == veterinarioNome }?.id

        if (dataAplicacao.isBlank() || tipoVacinaId == null || clinicaId == null || veterinarioId == null) {
            Toast.makeText(context, "Todos os campos são obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        val request = UpdateVacinaRequest(
            tipo_vacina_id = tipoVacinaId,
            dataAplicacao = dataAplicacao,
            clinicaId = clinicaId,
            veterinarioId = veterinarioId,
            observacoes = observacoes
        )

        val token = "seu_token_aqui" // TODO: Obter o token de forma segura
        vacina?.let {
            viewModel.updateVacina(token, it.id, request)
        }
    }

    // Observa o resultado da operação de guardar
    private fun observeViewModel() {
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Vacina atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }.onFailure {
                Toast.makeText(context, "Falha na operação: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Mostra o DatePicker
    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val displayDate = String.format(Locale.ROOT, "%02d-%02d-%04d", dayOfMonth, month + 1, year)
            editText.setText(displayDate) // Mostra e envia no formato dd-MM-yyyy
        }
        DatePickerDialog(
            requireContext(), dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Formata a data para dd-MM-yyyy para mostrar ao utilizador
    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""
        return try {
            // A API pode enviar 'yyyy-MM-dd' ou um timestamp completo. 
            // Esta abordagem extrai apenas a parte da data.
            val datePart = dateString.substringBefore("T")
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parser.parse(datePart)
            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            formatter.format(date!!)
        } catch (e: Exception) {
            // Se a data já estiver em dd-MM-yyyy, ou outro formato, devolve a própria string
            dateString
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}