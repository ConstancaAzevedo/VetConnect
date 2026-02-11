package pt.ipt.dam2025.vetconnect.ui.fragment

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.databinding.FragmentAdicionarExameBinding
import pt.ipt.dam2025.vetconnect.viewmodel.HistoricoViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.HistoricoViewModelFactory
import java.io.File
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdicionarExameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = HistoricoViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(HistoricoViewModel::class.java)

        setupUI()
        observeViewModel()

        // Ouve o resultado enviado pelo CamaraFragment
        setFragmentResultListener("requestKey") { _, bundle ->
            val imagePath = bundle.getString("imagePath")
            if (imagePath != null) {
                selectedImageUri = Uri.fromFile(File(imagePath))
                binding.imageViewPreview.setImageURI(selectedImageUri)
                binding.imageViewPreview.visibility = View.VISIBLE
            }
        }
    }

    private fun setupUI() {
        // TODO: Popular os spinners com dados da API
        val tiposExame = arrayOf("Análise Sanguínea", "Raio-X", "Ecografia")
        val clinicas = arrayOf("Clínica A", "Clínica B")
        val veterinarios = arrayOf("Dr. House", "Dr. Dolittle")

        binding.spinnerTipoExame.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, tiposExame))
        binding.spinnerClinica.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, clinicas))
        binding.spinnerVeterinario.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, veterinarios))

        binding.editTextDataExame.setOnClickListener { showDatePicker() }
        
        // Altera o clique do botão para navegar para o fragmento da câmara
        binding.buttonAdicionarFoto.setOnClickListener {
            findNavController().navigate(R.id.action_adicionarExameFragment_to_camaraFragment)
        }
        binding.buttonGuardarExame.setOnClickListener { guardarExame() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format(Locale.ROOT, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                binding.editTextDataExame.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun guardarExame() {
        // TODO: Obter o ID do animal e o token de forma segura
        val animalId = 1
        val token = "seu_token_aqui"

        // TODO: Converter os nomes dos spinners para os IDs correspondentes
        val tipoExameId = 1
        val clinicaId = 1
        val veterinarioId = 1

        val dataExame = binding.editTextDataExame.text.toString()
        val resultado = binding.editTextResultado.text.toString()
        val observacoes = binding.editTextObservacoes.text.toString()

        if (dataExame.isBlank() || binding.spinnerTipoExame.text.isBlank()) {
            Toast.makeText(context, "Por favor, preencha o tipo e a data do exame", Toast.LENGTH_SHORT).show()
            return
        }

        // chama a nova função no ViewModel
        viewModel.adicionarExameEFoto(
            token = token,
            animalId = animalId,
            tipoExameId = tipoExameId,
            dataExame = dataExame,
            clinicaId = clinicaId,
            veterinarioId = veterinarioId,
            resultado = resultado,
            observacoes = observacoes,
            imageUri = selectedImageUri, // Passa o URI da imagem
            context = requireContext()
        )
    }

    private fun observeViewModel() {
        viewModel.operationStatus.observe(viewLifecycleOwner) {
            it.onSuccess {
                Toast.makeText(context, "Exame adicionado com sucesso", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }.onFailure {
                Toast.makeText(context, "Erro ao adicionar exame: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
