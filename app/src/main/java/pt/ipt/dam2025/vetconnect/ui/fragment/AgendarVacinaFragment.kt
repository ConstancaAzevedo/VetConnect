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
import pt.ipt.dam2025.vetconnect.databinding.FragmentAgendarVacinaBinding
import pt.ipt.dam2025.vetconnect.model.AgendarVacinaRequest
import pt.ipt.dam2025.vetconnect.model.Clinica
import pt.ipt.dam2025.vetconnect.model.TipoVacina
import pt.ipt.dam2025.vetconnect.model.Veterinario
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Fragment para a página de agendar vacinas
 */

class AgendarVacinaFragment : Fragment() {

    private var _binding: FragmentAgendarVacinaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: VacinaViewModel

    // Listas locais para guardar os dados dos spinners
    private val tiposVacinaList = mutableListOf<TipoVacina>()
    private val clinicasList = mutableListOf<Clinica>()
    private val veterinariosList = mutableListOf<Veterinario>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgendarVacinaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = VacinaViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[VacinaViewModel::class.java]

        setupListeners()
        observeLists()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.dataAgendada.setOnClickListener { showDatePicker(binding.dataAgendada) }
        binding.btnConfirmar.setOnClickListener { agendarVacina() }

        // Quando uma clínica é selecionada, carrega os veterinários correspondentes
        binding.spinnerClinica.setOnItemClickListener { _, _, position, _ ->
            val selectedClinica = clinicasList.getOrNull(position)
            selectedClinica?.id?.let { viewModel.carregaVeterinarios(it) }
        }
    }

    private fun observeLists() {
        viewModel.tiposVacina.observe(viewLifecycleOwner) { tipos ->
            tiposVacinaList.clear()
            tiposVacinaList.addAll(tipos)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, tipos.map { it.nome })
            binding.spinnerNomeVacina.setAdapter(adapter)
        }

        viewModel.clinicas.observe(viewLifecycleOwner) { clinicas ->
            clinicasList.clear()
            clinicasList.addAll(clinicas)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, clinicas.map { it.nome })
            binding.spinnerClinica.setAdapter(adapter)
        }

        viewModel.veterinarios.observe(viewLifecycleOwner) { veterinarios ->
            veterinariosList.clear()
            veterinariosList.addAll(veterinarios)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, veterinarios.map { it.nome })
            binding.spinnerVeterinario.setAdapter(adapter)
        }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format(Locale.ROOT, "%02d-%02d-%04d", dayOfMonth, month + 1, year)
                editText.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun agendarVacina() {
        // TODO: Obter o ID do animal e o token de forma segura
        val animalId = 1
        val token = "seu_token_aqui"

        // Obtém os nomes dos spinners e encontra os IDs correspondentes
        val tipoVacinaNome = binding.spinnerNomeVacina.text.toString()
        val clinicaNome = binding.spinnerClinica.text.toString()
        val veterinarioNome = binding.spinnerVeterinario.text.toString()

        val tipoVacinaId = tiposVacinaList.find { it.nome == tipoVacinaNome }?.id
        val clinicaId = clinicasList.find { it.nome == clinicaNome }?.id
        val veterinarioId = veterinariosList.find { it.nome == veterinarioNome }?.id

        val displayDate = binding.dataAgendada.text.toString()
        val apiDate = reformatDateForApi(displayDate)
        val observacoes = binding.observacoes.text.toString().takeIf { it.isNotBlank() }

        if (apiDate.isNullOrBlank() || tipoVacinaId == null || clinicaId == null || veterinarioId == null) {
            Toast.makeText(context, "Por favor, preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        // Chama a função no ViewModel com os IDs corretos
        val request = AgendarVacinaRequest(animalId, tipoVacinaId, clinicaId, veterinarioId, apiDate, observacoes)
        viewModel.agendarVacina(token, request)
    }

    private fun observeViewModel() {
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Vacina agendada com sucesso!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }.onFailure { throwable ->
                Toast.makeText(context, "Erro ao agendar vacina: ${throwable.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Converte a data do ecrã (dd-MM-yyyy) de volta para o formato da API (yyyy-MM-dd)
    private fun reformatDateForApi(displayDate: String?): String? {
        if (displayDate.isNullOrBlank()) return null
        return try {
            val parser = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = parser.parse(displayDate)
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            formatter.format(date!!)
        } catch (_: Exception) {
            null // Retorna null se o formato for inválido
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
