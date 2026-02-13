package pt.ipt.dam2025.vetconnect.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipt.dam2025.vetconnect.databinding.FragmentMarcarConsultaBinding
import pt.ipt.dam2025.vetconnect.model.*
import pt.ipt.dam2025.vetconnect.viewmodel.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Fragment para a página de marcar consulta
 */
class MarcarConsultaFragment : Fragment() {

    private var _binding: FragmentMarcarConsultaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ConsultaViewModel
    private var animalId: Int = -1 // o ID do animal é passado via argumentos

    // listas locais para guardar os dados dos spinners
    private val clinicasList = mutableListOf<Clinica>()
    private val veterinariosList = mutableListOf<Veterinario>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarcarConsultaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // obtém o ID do animal passado como argumento
        animalId = arguments?.getInt("animalId", -1) ?: -1
        if (animalId == -1) {
            Toast.makeText(context, "Erro: Animal ID não encontrado.", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
            return
        }

        val factory = ConsultaViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[ConsultaViewModel::class.java]

        setupListeners()
        observeLists()
        observeViewModel()
    }

    /*
     * configura os listeners de clique para os botões e spinners
     */
    private fun setupListeners() {
        binding.dataConsulta.setOnClickListener { showDatePicker() }
        binding.btnConfirmar.setOnClickListener { marcarConsulta() }

        // quando uma clínica é selecionada carrega os veterinários correspondentes
        binding.clinicaSpinner.setOnItemClickListener { _, _, position, _ ->
            val selectedClinica = clinicasList.getOrNull(position)
            selectedClinica?.id?.let { viewModel.carregaVeterinarios(it) }
        }
    }

    /*
     * observa as LiveData do ViewModel com as listas e popula os spinners
     */
    private fun observeLists() {
        // observa a lista de clínicas
        viewModel.clinicas.observe(viewLifecycleOwner) { clinicas ->
            clinicasList.clear()
            clinicasList.addAll(clinicas)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, clinicas.map { it.nome })
            binding.clinicaSpinner.setAdapter(adapter)
        }

        // observa a lista de veterinários
        viewModel.veterinarios.observe(viewLifecycleOwner) { veterinarios ->
            veterinariosList.clear()
            veterinariosList.addAll(veterinarios)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, veterinarios.map { it.nome })
            binding.veterinarioSpinner.setAdapter(adapter)
        }
    }

    /*
     * recolhe os dados valida-os e envia o pedido de marcação de consulta
     */
    private fun marcarConsulta() {
        val token = "seu_token_aqui" // TODO: Obter o token de forma segura

        // obtém os nomes dos spinners e encontra os IDs correspondentes
        val clinicaNome = binding.clinicaSpinner.text.toString()
        val veterinarioNome = binding.veterinarioSpinner.text.toString()

        val clinicaId = clinicasList.find { it.nome == clinicaNome }?.id
        val veterinarioId = veterinariosList.find { it.nome == veterinarioNome }?.id

        val data = binding.dataConsulta.text.toString()
        val motivo = binding.motivo.text.toString() // 'motivo' é o 'tópico'
        val observacoes = binding.observacoes.text.toString() // 'observacoes' é opcional

        // validação robusta para campos obrigatórios
        if (motivo.isBlank() || clinicaId == null || veterinarioId == null || data.isBlank()) {
            Toast.makeText(context, "Por favor, preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        // converte a data para o formato da API (yyyy-MM-dd)
        val apiDate = reformatDateForApi(data)
        if (apiDate == null) {
            Toast.makeText(context, "Formato de data inválido. Use dd-MM-yyyy.", Toast.LENGTH_SHORT).show()
            return
        }

        val novaConsulta = NovaConsulta(animalId, clinicaId, veterinarioId, apiDate, motivo, observacoes)
        viewModel.marcarConsulta(token, novaConsulta)
    }

    /*
     * observa o resultado da operação de marcar consulta
     */
    private fun observeViewModel() {
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Consulta marcada com sucesso!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }.onFailure { throwable ->
                Toast.makeText(context, "Erro ao marcar consulta: ${throwable.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val displayDate = String.format(Locale.ROOT, "%02d-%02d-%04d", dayOfMonth, month + 1, year)
                binding.dataConsulta.setText(displayDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun reformatDateForApi(displayDate: String?): String? {
        if (displayDate.isNullOrBlank()) return null
        return try {
            val parser = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = parser.parse(displayDate)
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            formatter.format(date!!)
        } catch (_: Exception) {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
