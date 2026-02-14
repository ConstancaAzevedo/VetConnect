package pt.ipt.dam2025.vetconnect.ui.fragment

import android.app.DatePickerDialog // Importa o diálogo para selecionar uma data
import android.net.Uri // Importa a classe Uri para lidar com caminhos de ficheiros
import android.os.Bundle // Importa a classe Bundle para passar dados entre fragments
import android.view.LayoutInflater // Importa o LayoutInflater para inflar layouts XML
import android.view.View // Importa a classe base de todas as Views
import android.view.ViewGroup // Importa a classe base para layouts de Views
import android.widget.ArrayAdapter // Importa o ArrayAdapter para popular spinners e AutoCompleteTextViews
import android.widget.EditText // Importa a classe EditText
import android.widget.Toast // Importa para mostrar mensagens curtas ao utilizador
import androidx.core.os.bundleOf // Importa uma função de ajuda para criar Bundles
import androidx.fragment.app.Fragment // Importa a classe base para todos os Fragments
import androidx.fragment.app.setFragmentResultListener // Importa o listener para receber resultados de outros fragments
import androidx.lifecycle.ViewModelProvider // Importa para criar e gerir ViewModels
import androidx.navigation.fragment.findNavController // Importa para gerir a navegação entre fragments
import pt.ipt.dam2025.vetconnect.R // Importa a classe R para aceder aos recursos da aplicação
import pt.ipt.dam2025.vetconnect.databinding.FragmentAdicionarExameBinding // Importa a classe de ViewBinding gerada
import pt.ipt.dam2025.vetconnect.model.*
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.HistoricoViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.HistoricoViewModelFactory
import java.io.File // Importa a classe File para manipulação de ficheiros
import java.text.SimpleDateFormat // Importa para formatar datas
import java.util.Calendar // Importa a classe Calendar para trabalhar com datas
import java.util.Locale // Importa para definir a localização para formatação

/**
 * Fragment para a página de adicionar um novo exame ao histórico
 */
class AdicionarExameFragment : Fragment() {

    // Variável para o ViewBinding que permite aceder às views do layout de forma segura
    private var _binding: FragmentAdicionarExameBinding? = null
    private val binding get() = _binding!!

    // O ViewModel que contém a lógica de negócio e os dados para este ecrã
    private lateinit var viewModel: HistoricoViewModel
    // O URI da imagem selecionada pela câmara (se existir)
    private var selectedImageUri: Uri? = null
    // O gestor de sessão para obter o token e o ID do animal
    private lateinit var sessionManager: SessionManager

    // Listas locais para guardar os dados dos spinners para depois encontrar os IDs
    private val tiposExameList = mutableListOf<TipoExame>()
    private val clinicasList = mutableListOf<Clinica>()
    private val veterinariosList = mutableListOf<Veterinario>()

    /**
     * Chamado para inflar o layout do fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout usando ViewBinding e guarda a referência
        _binding = FragmentAdicionarExameBinding.inflate(inflater, container, false)
        // Retorna a view raiz do layout
        return binding.root
    }

    /**
     * Chamado depois da view ter sido criada
     * É aqui que a maior parte da lógica é inicializada
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa o SessionManager
        sessionManager = SessionManager(requireContext())

        // Inicializa o ViewModel usando a sua Factory
        val factory = HistoricoViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[HistoricoViewModel::class.java]

        // Configura todos os listeners de clique
        setupListeners()
        // Começa a observar as listas de dados para os spinners
        observeLists()
        // Começa a observar o estado das operações (ex: guardar exame)
        observeViewModel()

        // Regista um listener para receber o resultado do CamaraFragment
        setFragmentResultListener("requestKey") { _, bundle ->
            // Obtém o caminho da imagem a partir do bundle
            val imagePath = bundle.getString("imagePath")
            if (imagePath != null) {
                // Converte o caminho para um URI e guarda-o
                selectedImageUri = Uri.fromFile(File(imagePath))
                // Mostra a preview da imagem no ImageView
                binding.imageViewPreview.setImageURI(selectedImageUri)
                binding.imageViewPreview.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Configura todos os listeners de clique para os botões e outros elementos interativos
     */
    private fun setupListeners() {
        // Mostra o seletor de data quando o campo de data é clicado
        binding.editTextDataExame.setOnClickListener { showDatePicker(binding.editTextDataExame) }
        // Navega para o fragment da câmara quando o botão de adicionar foto é clicado
        binding.btnAdicionarFoto.setOnClickListener {
            val bundle = bundleOf("pathType" to "exames") // Passa o tipo de caminho para a câmara
            findNavController().navigate(R.id.action_adicionarExameFragment_to_camaraFragment, bundle)
        }
        // Chama a função para guardar o exame quando o botão de guardar é clicado
        binding.btnGuardar.setOnClickListener { guardarExame() }

        // Listener para o spinner de clínicas
        binding.spinnerClinica.setOnItemClickListener { _, _, position, _ ->
            // Quando uma clínica é selecionada, obtém o seu ID
            val selectedClinica = clinicasList.getOrNull(position)
            // E pede ao ViewModel para carregar a lista de veterinários dessa clínica
            selectedClinica?.id?.let { viewModel.carregaVeterinarios(it) }
        }
    }

    /**
     * Configura os observadores para as listas de dados (LiveData) vindas do ViewModel
     * Quando os dados mudam no ViewModel, este código é executado para atualizar a UI
     */
    private fun observeLists() {
        // Observa a lista de tipos de exame
        viewModel.tiposExame.observe(viewLifecycleOwner) { tipos ->
            tiposExameList.clear()
            tiposExameList.addAll(tipos)
            // Cria um adapter com os nomes dos tipos de exame e define-o no spinner
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, tipos.map { it.nome })
            binding.spinnerTipoExame.setAdapter(adapter)
        }

        // Observa a lista de clínicas
        viewModel.clinicas.observe(viewLifecycleOwner) { clinicas ->
            clinicasList.clear()
            clinicasList.addAll(clinicas)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, clinicas.map { it.nome })
            binding.spinnerClinica.setAdapter(adapter)
        }

        // Observa a lista de veterinários (que é carregada dinamicamente)
        viewModel.veterinarios.observe(viewLifecycleOwner) { veterinarios ->
            veterinariosList.clear()
            veterinariosList.addAll(veterinarios)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, veterinarios.map { it.nome })
            binding.spinnerVeterinario.setAdapter(adapter)
        }
    }

    /**
     * Mostra um diálogo para o utilizador selecionar uma data
     */
    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Formata a data selecionada como dd-MM-yyyy
                val selectedDate = String.format(Locale.ROOT, "%02d-%02d-%04d", dayOfMonth, month + 1, year)
                // Define o texto no EditText
                editText.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR), // Ano inicial
            calendar.get(Calendar.MONTH), // Mês inicial
            calendar.get(Calendar.DAY_OF_MONTH) // Dia inicial
        ).show()
    }

    /**
     * Recolhe todos os dados do formulário, valida-os e chama o ViewModel para guardar o exame
     */
    private fun guardarExame() {
        // Obtém o token e o ID do animal da sessão
        val token = sessionManager.getAuthToken()
        val animalId = sessionManager.getAnimalId()

        // Validação de segurança para garantir que a sessão é válida
        if (token == null || animalId == -1) {
            Toast.makeText(context, "Sessão inválida. Por favor, reinicie a aplicação.", Toast.LENGTH_LONG).show()
            return
        }

        // Obtém os nomes selecionados nos spinners
        val tipoExameNome = binding.spinnerTipoExame.text.toString()
        val clinicaNome = binding.spinnerClinica.text.toString()
        val veterinarioNome = binding.spinnerVeterinario.text.toString()

        // Encontra os IDs correspondentes aos nomes selecionados nas listas locais
        val tipoExameId = tiposExameList.find { it.nome == tipoExameNome }?.id
        val clinicaId = clinicasList.find { it.nome == clinicaNome }?.id
        val veterinarioId = veterinariosList.find { it.nome == veterinarioNome }?.id

        // Obtém os restantes dados dos campos de texto
        val displayDate = binding.editTextDataExame.text.toString()
        val apiDate = reformatDateForApi(displayDate)
        val resultado = binding.editTextResultado.text.toString()
        val observacoes = binding.editTextObservacoes.text.toString()

        // Validação dos campos obrigatórios
        if (apiDate.isNullOrBlank() || tipoExameId == null || clinicaId == null || veterinarioId == null) {
            Toast.makeText(context, "Por favor, preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        // Se a validação passar, chama a função do ViewModel para adicionar o exame e a foto
        viewModel.adicionarExameEFoto(
            token = token,
            animalId = animalId,
            tipoExameId = tipoExameId,
            dataExame = apiDate,
            clinicaId = clinicaId,
            veterinarioId = veterinarioId,
            resultado = resultado,
            observacoes = observacoes,
            imageUri = selectedImageUri, // Passa o URI da foto (pode ser nulo)
            context = requireContext()
        )
    }

    /**
     * Observa o estado da operação de guardar o exame
     * Mostra uma mensagem de sucesso ou de erro ao utilizador
     */
    private fun observeViewModel() {
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            // O resultado é um objeto Result que pode ser de sucesso ou falha
            result.onSuccess {
                Toast.makeText(context, "Exame adicionado com sucesso", Toast.LENGTH_SHORT).show()
                // Volta para o ecrã anterior
                findNavController().popBackStack()
            }.onFailure { throwable ->
                // Mostra a mensagem de erro que vem da exceção
                Toast.makeText(context, "Erro ao adicionar exame: ${throwable.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Converte a data do formato do ecrã (dd-MM-yyyy) para o formato da API (yyyy-MM-dd)
     * Função de utilidade para garantir que os dados são enviados corretamente
     */
    private fun reformatDateForApi(displayDate: String?): String? {
        if (displayDate.isNullOrBlank()) return null
        return try {
            val parser = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = parser.parse(displayDate)
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            formatter.format(date!!)
        } catch (_: Exception) {
            null // Retorna nulo se o formato da data de entrada for inválido
        }
    }

    /**
     * Chamado quando a view do Fragment está a ser destruída
     * Limpa a referência ao binding para evitar memory leaks
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
