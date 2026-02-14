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
import coil.load
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.databinding.FragmentEditarExameBinding
import pt.ipt.dam2025.vetconnect.model.*
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.HistoricoViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.HistoricoViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditarExameFragment : Fragment() {

    // declara a variável de binding
    private var _binding: FragmentEditarExameBinding? = null
    // esta propriedade é válida apenas entre onCreateView e onDestroyView
    private val binding get() = _binding!!

    private lateinit var viewModel: HistoricoViewModel
    private lateinit var sessionManager: SessionManager
    private var exame: Exame? = null
    private var novaImagemUri: Uri? = null

    // listas locais para guardar os dados dos spinners e permitir encontrar os IDs
    private val tiposExameList = mutableListOf<TipoExame>()
    private val clinicasList = mutableListOf<Clinica>()
    private val veterinariosList = mutableListOf<Veterinario>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarExameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        val factory = HistoricoViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[HistoricoViewModel::class.java]

        // obtém o objeto Exame a partir dos argumentos de navegação
        @Suppress("DEPRECATION")
        exame = arguments?.getParcelable("exame")

        if (exame == null) {
            Toast.makeText(context, "Erro ao carregar dados do exame.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        // chama as funções para configurar o ecrã
        configurarListeners()
        observeLists()
        observeViewModel()
        ouvirResultadoDaCamara()
        populateUi(exame!!)
    }

    /*
     * preenche os campos com os dados iniciais do exame a ser editado
     */
    private fun populateUi(exame: Exame) {
        binding.editDataExame.setText(formatDateForDisplay(exame.dataExame))
        binding.editTextResultado.setText(exame.resultado)
        binding.editTextObservacoes.setText(exame.observacoes)

        if (!exame.ficheiroUrl.isNullOrEmpty()) {
            binding.imageViewPreview.load(exame.ficheiroUrl) {
                placeholder(R.drawable.vetconnectfundo)
                error(R.drawable.vetconnectfundo)
            }
            binding.imageViewPreview.visibility = View.VISIBLE
        }
    }

    /*
     * configura os listeners de clique para os botões e spinners
     */
    private fun configurarListeners() {
        binding.editDataExame.setOnClickListener { showDatePickerDialog(binding.editDataExame) }
        binding.btnGuardar.setOnClickListener { guardarAlteracoes() }
        binding.buttonAlterarFoto.setOnClickListener {
            val bundle = bundleOf("pathType" to "exames")
            findNavController().navigate(R.id.action_editarExameFragment_to_camaraFragment, bundle)
        }

        // quando uma clínica é selecionada pede ao ViewModel para carregar a lista de veterinários
        binding.spinnerClinica.setOnItemClickListener { _, _, position, _ ->
            val selectedClinica = clinicasList.getOrNull(position)
            selectedClinica?.id?.let { viewModel.carregaVeterinarios(it) }
        }
    }

    /*
     * ouve o resultado da câmara para obter o URI da nova imagem
     */
    private fun ouvirResultadoDaCamara() {
        setFragmentResultListener("requestKey") { _, bundle ->
            val imagePath = bundle.getString("imagePath")
            if (imagePath != null) {
                novaImagemUri = Uri.fromFile(File(imagePath))
                binding.imageViewPreview.setImageURI(novaImagemUri)
                binding.imageViewPreview.visibility = View.VISIBLE
            }
        }
    }

    /*
     * observa as LiveData do ViewModel com as listas e popula os spinners
     */
    private fun observeLists() {
        // observa a lista de tipos de exame
        viewModel.tiposExame.observe(viewLifecycleOwner) { tipos ->
            tiposExameList.clear()
            tiposExameList.addAll(tipos)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, tipos.map { it.nome })
            binding.spinnerTipoExame.setAdapter(adapter)

            // pré seleciona
            val tipoAtual = tipos.find { it.id == exame?.tipoExameId }
            binding.spinnerTipoExame.setText(tipoAtual?.nome, false)
        }

        // observa a lista de clínicas
        viewModel.clinicas.observe(viewLifecycleOwner) { clinicas ->
            clinicasList.clear()
            clinicasList.addAll(clinicas)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, clinicas.map { it.nome })
            binding.spinnerClinica.setAdapter(adapter)

            // pré seleciona e carrega veterinários
            val clinicaAtual = clinicas.find { it.id == exame?.clinicaId }
            binding.spinnerClinica.setText(clinicaAtual?.nome, false)
            clinicaAtual?.id?.let { viewModel.carregaVeterinarios(it) }
        }

        // observa a lista de veterinários
        viewModel.veterinarios.observe(viewLifecycleOwner) { veterinarios ->
            veterinariosList.clear()
            veterinariosList.addAll(veterinarios)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, veterinarios.map { it.nome })
            binding.spinnerVeterinario.setAdapter(adapter)

            // pré seleciona
            val vetAtual = veterinarios.find { it.id == exame?.veterinarioId }
            binding.spinnerVeterinario.setText(vetAtual?.nome, false)
        }
    }

    /*
     * recolhe os dados valida-os e envia o pedido de atualização
     */
    private fun guardarAlteracoes() {
        exame?.let { exameAtual ->
            val token = sessionManager.getAuthToken()
            if (token == null) {
                Toast.makeText(context, "Sessão inválida. Não é possível guardar alterações", Toast.LENGTH_LONG).show()
                return@let
            }

            val displayDate = binding.editDataExame.text.toString()
            val resultado = binding.editTextResultado.text.toString()
            val observacoes = binding.editTextObservacoes.text.toString()

            // obtém os nomes e encontra os IDs
            val tipoExameNome = binding.spinnerTipoExame.text.toString()
            val clinicaNome = binding.spinnerClinica.text.toString()
            val veterinarioNome = binding.spinnerVeterinario.text.toString()

            val tipoExameId = tiposExameList.find { it.nome == tipoExameNome }?.id
            val clinicaId = clinicasList.find { it.nome == clinicaNome }?.id
            val veterinarioId = veterinariosList.find { it.nome == veterinarioNome }?.id

            // converte a data de volta para o formato da API
            val apiDate = reformatDateForApi(displayDate)

            // validação Robusta para todos os campos obrigatórios
            if (apiDate.isNullOrBlank() || tipoExameId == null || clinicaId == null || veterinarioId == null) {
                Toast.makeText(context, "Todos os campos obrigatórios devem ser preenchidos", Toast.LENGTH_LONG).show()
                return@let
            }

            viewModel.atualizarExameEFoto(
                token = token,
                exameId = exameAtual.id,
                animalId = exameAtual.animalId,
                tipoExameId = tipoExameId,
                dataExame = apiDate,
                clinicaId = clinicaId,
                veterinarioId = veterinarioId,
                resultado = resultado,
                observacoes = observacoes,
                novaImagemUri = novaImagemUri,
                context = requireContext()
            )
        }
    }

    /*
     * observa o resultado da operação de guardar
     */
    private fun observeViewModel() {
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Operação concluída com sucesso", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }.onFailure { throwable ->
                Toast.makeText(context, "Erro: ${throwable.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /*
     * mostra o DatePicker para o utilizador escolher uma data
     */
    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val displayDate = String.format(Locale.ROOT, "%02d-%02d-%04d", dayOfMonth, month + 1, year)
            editText.setText(displayDate)
        }
        DatePickerDialog(
            requireContext(), dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    /*
     * formata a data da API (yyyy-MM-dd) para mostrar ao utilizador (dd-MM-yyyy)
     */
    private fun formatDateForDisplay(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parser.parse(dateString.substringBefore('T'))
            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            formatter.format(date!!)
        } catch (_: Exception) {
            dateString // em caso de erro devolve o original
        }
    }

    /*
     * converte a data do ecrã (dd-MM-yyyy) de volta para o formato da API (yyyy-MM-dd)
     */
    private fun reformatDateForApi(displayDate: String?): String? {
        if (displayDate.isNullOrBlank()) return null
        return try {
            val parser = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = parser.parse(displayDate)
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            formatter.format(date!!)
        } catch (_: Exception) {
            null // retorna null se o formato for inválido
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}