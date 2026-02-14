package pt.ipt.dam2025.vetconnect.ui.fragment

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.databinding.FragmentAdicionarExameBinding
import pt.ipt.dam2025.vetconnect.model.*
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.HistoricoViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.HistoricoViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Fragment para a página de adicionar um novo exame ao histórico
 */

class AdicionarExameFragment : Fragment() {

    private var _binding: FragmentAdicionarExameBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HistoricoViewModel
    private var selectedImageUri: Uri? = null
    private lateinit var sessionManager: SessionManager

    // listas locais para guardar os dados dos spinners
    private val tiposExameList = mutableListOf<TipoExame>()
    private val clinicasList = mutableListOf<Clinica>()
    private val veterinariosList = mutableListOf<Veterinario>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdicionarExameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        val factory = HistoricoViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[HistoricoViewModel::class.java]

        setupListeners()
        observeLists()
        observeViewModel()

        // ouve o resultado enviado pelo CamaraFragment
        setFragmentResultListener("requestKey") { _, bundle ->
            val imagePath = bundle.getString("imagePath")
            if (imagePath != null) {
                selectedImageUri = Uri.fromFile(File(imagePath))
                binding.imageViewPreview.setImageURI(selectedImageUri)
                binding.imageViewPreview.visibility = View.VISIBLE
            }
        }
    }

    private fun setupListeners() {
        binding.editTextDataExame.setOnClickListener { showDatePicker(binding.editTextDataExame) }
        binding.btnAdicionarFoto.setOnClickListener {
            val bundle = bundleOf("pathType" to "exames")
            findNavController().navigate(R.id.action_adicionarExameFragment_to_camaraFragment, bundle)
        }
        binding.btnGuardar.setOnClickListener { guardarExame() }

        // Quando uma clínica é selecionada, carrega os veterinários correspondentes
        binding.spinnerClinica.setOnItemClickListener { _, _, position, _ ->
            val selectedClinica = clinicasList.getOrNull(position)
            selectedClinica?.id?.let { viewModel.carregaVeterinarios(it) }
        }
    }

    private fun observeLists() {
        viewModel.tiposExame.observe(viewLifecycleOwner) { tipos ->
            tiposExameList.clear()
            tiposExameList.addAll(tipos)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, tipos.map { it.nome })
            binding.spinnerTipoExame.setAdapter(adapter)
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

    private fun guardarExame() {
        val token = sessionManager.getAuthToken()
        val animalId = sessionManager.getAnimalId()

        if (token == null || animalId == -1) {
            Toast.makeText(context, "Sessão inválida. Por favor, reinicie a aplicação.", Toast.LENGTH_LONG).show()
            return
        }

        // obtém os nomes dos spinners e encontra os IDs correspondentes
        val tipoExameNome = binding.spinnerTipoExame.text.toString()
        val clinicaNome = binding.spinnerClinica.text.toString()
        val veterinarioNome = binding.spinnerVeterinario.text.toString()

        val tipoExameId = tiposExameList.find { it.nome == tipoExameNome }?.id
        val clinicaId = clinicasList.find { it.nome == clinicaNome }?.id
        val veterinarioId = veterinariosList.find { it.nome == veterinarioNome }?.id

        val displayDate = binding.editTextDataExame.text.toString()
        val apiDate = reformatDateForApi(displayDate)

        val resultado = binding.editTextResultado.text.toString()
        val observacoes = binding.editTextObservacoes.text.toString()

        if (apiDate.isNullOrBlank() || tipoExameId == null || clinicaId == null || veterinarioId == null) {
            Toast.makeText(context, "Por favor, preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        // chama a função no ViewModel com os IDs corretos
        viewModel.adicionarExameEFoto(
            token = token,
            animalId = animalId,
            tipoExameId = tipoExameId,
            dataExame = apiDate,
            clinicaId = clinicaId,
            veterinarioId = veterinarioId,
            resultado = resultado,
            observacoes = observacoes,
            imageUri = selectedImageUri,
            context = requireContext()
        )
    }

    private fun observeViewModel() {
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Exame adicionado com sucesso", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }.onFailure { throwable ->
                Toast.makeText(context, "Erro ao adicionar exame: ${throwable.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // converte a data do ecrã (dd-MM-yyyy) de volta para o formato da API (yyyy-MM-dd)
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
