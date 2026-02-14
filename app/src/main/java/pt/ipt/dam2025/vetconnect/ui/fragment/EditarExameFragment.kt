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
import androidx.lifecycle.ViewModelProvider // Importa para criar e gerir a instância do ViewModel
import androidx.navigation.fragment.findNavController // Importa para gerir a navegação entre os ecrãs (fragments)
import coil.load // Importa a função de extensão da biblioteca Coil para carregar imagens
import pt.ipt.dam2025.vetconnect.R // Importa a classe R para aceder aos recursos da aplicação
import pt.ipt.dam2025.vetconnect.databinding.FragmentEditarExameBinding // Importa a classe de ViewBinding gerada
import pt.ipt.dam2025.vetconnect.model.*
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.HistoricoViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.HistoricoViewModelFactory
import java.io.File // Importa a classe File para manipulação de ficheiros
import java.text.SimpleDateFormat // Importa para formatar e fazer parse de datas
import java.util.Calendar // Importa a classe Calendar para trabalhar com datas
import java.util.Locale // Importa para definir a localização para formatação

/**
 * Fragment para a página de editar um exame
 */
class EditarExameFragment : Fragment() {

    // Variável para o ViewBinding que garante acesso seguro às views do layout
    private var _binding: FragmentEditarExameBinding? = null
    private val binding get() = _binding!!

    // O ViewModel que vai orquestrar a lógica de negócio e os dados para este ecrã
    private lateinit var viewModel: HistoricoViewModel
    // O gestor de sessão para obter o token de autenticação
    private lateinit var sessionManager: SessionManager
    // O objeto Exame que contém os dados a serem editados (pode ser nulo inicialmente)
    private var exame: Exame? = null
    // O URI da nova imagem selecionada pelo utilizador (se houver)
    private var novaImagemUri: Uri? = null

    // Listas locais para guardar os dados dos spinners para depois encontrar os IDs
    private val tiposExameList = mutableListOf<TipoExame>()
    private val clinicasList = mutableListOf<Clinica>()
    private val veterinariosList = mutableListOf<Veterinario>()

    /**
     * Chamado pelo sistema para criar a hierarquia de Views do Fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout usando ViewBinding e guarda a referência
        _binding = FragmentEditarExameBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Chamado logo após a view ter sido criada
     * É o local ideal para inicializar componentes, obter dados e configurar listeners
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa o SessionManager e o ViewModel
        sessionManager = SessionManager(requireContext())
        val factory = HistoricoViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[HistoricoViewModel::class.java]

        // Obtém o objeto Exame que foi passado como argumento pelo fragment anterior
        @Suppress("DEPRECATION")
        exame = arguments?.getParcelable("exame")

        // Validação crucial: Se não recebemos os dados do exame, não podemos continuar
        if (exame == null) {
            Toast.makeText(context, "Erro ao carregar dados do exame.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack() // Volta para o ecrã anterior
            return
        }

        // Se recebemos os dados, o fluxo de inicialização continua
        configurarListeners() // Configura os listeners dos botões e spinners
        observeLists() // Começa a observar as listas de dados (clínicas, etc.) do ViewModel
        observeViewModel() // Começa a observar o resultado da operação de guardar as alterações
        ouvirResultadoDaCamara() // Começa a ouvir por uma nova foto
        populateUi(exame!!) // Preenche o formulário com os dados do exame que recebemos
    }

    /**
     * Preenche os campos do formulário com os dados existentes do exame
     * Esta função é chamada uma vez no início para popular o ecrã
     */
    private fun populateUi(exame: Exame) {
        binding.editDataExame.setText(formatDateForDisplay(exame.dataExame))
        binding.editTextResultado.setText(exame.resultado)
        binding.editTextObservacoes.setText(exame.observacoes)

        // Se o exame já tiver uma foto, carrega-a usando a biblioteca Coil
        if (!exame.ficheiroUrl.isNullOrEmpty()) {
            binding.imageViewPreview.load(exame.ficheiroUrl) {
                placeholder(R.drawable.vetconnectfundo) // Imagem de placeholder enquanto carrega
                error(R.drawable.vetconnectfundo) // Imagem de erro se o carregamento falhar
            }
            binding.imageViewPreview.visibility = View.VISIBLE
        }
    }

    /**
     * Configura os listeners de clique para os botões e spinners do ecrã
     */
    private fun configurarListeners() {
        binding.editDataExame.setOnClickListener { showDatePickerDialog(binding.editDataExame) }
        binding.btnGuardar.setOnClickListener { guardarAlteracoes() }
        binding.buttonAlterarFoto.setOnClickListener {
            val bundle = bundleOf("pathType" to "exames") // Passa o tipo de caminho para a câmara
            findNavController().navigate(R.id.action_editarExameFragment_to_camaraFragment, bundle)
        }

        // Quando uma nova clínica é selecionada no spinner
        binding.spinnerClinica.setOnItemClickListener { _, _, position, _ ->
            // obtém a clínica selecionada
            clinicasList.getOrNull(position)?.let {
                // e pede ao ViewModel para carregar a lista de veterinários dessa clínica.
                viewModel.carregaVeterinarios(it.id)
            }
        }
    }

    /**
     * Regista um listener para receber o resultado do CamaraFragment
     */
    private fun ouvirResultadoDaCamara() {
        setFragmentResultListener("requestKey") { _, bundle ->
            val imagePath = bundle.getString("imagePath")
            if (imagePath != null) {
                // Guarda o URI da nova imagem e mostra a sua preview
                novaImagemUri = Uri.fromFile(File(imagePath))
                binding.imageViewPreview.setImageURI(novaImagemUri)
                binding.imageViewPreview.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Configura os observadores para as listas de dados do ViewModel
     * Esta função é o coração da UI reativa, garantindo que os spinners são preenchidos e pré-selecionados corretamente
     */
    private fun observeLists() {
        // Observa a lista de todos os tipos de exame disponíveis
        viewModel.tiposExame.observe(viewLifecycleOwner) { tipos ->
            tiposExameList.clear(); tiposExameList.addAll(tipos)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, tipos.map { it.nome })
            binding.spinnerTipoExame.setAdapter(adapter)
            // Pré-seleciona o tipo de exame atual
            val tipoAtual = tipos.find { it.id == exame?.tipoExameId }
            binding.spinnerTipoExame.setText(tipoAtual?.nome, false)
        }

        // Observa a lista de todas as clínicas
        viewModel.clinicas.observe(viewLifecycleOwner) { clinicas ->
            clinicasList.clear(); clinicasList.addAll(clinicas)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, clinicas.map { it.nome })
            binding.spinnerClinica.setAdapter(adapter)
            // Pré-seleciona a clínica atual e despoleta o carregamento dos veterinários
            val clinicaAtual = clinicas.find { it.id == exame?.clinicaId }
            binding.spinnerClinica.setText(clinicaAtual?.nome, false)
            clinicaAtual?.id?.let { viewModel.carregaVeterinarios(it) }
        }

        // Observa a lista de veterinários (que é carregada dinamicamente)
        viewModel.veterinarios.observe(viewLifecycleOwner) { veterinarios ->
            veterinariosList.clear(); veterinariosList.addAll(veterinarios)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, veterinarios.map { it.nome })
            binding.spinnerVeterinario.setAdapter(adapter)
            // Pré-seleciona o veterinário atual DEPOIS da lista correta ter sido carregada
            val vetAtual = veterinarios.find { it.id == exame?.veterinarioId }
            binding.spinnerVeterinario.setText(vetAtual?.nome, false)
        }
    }

    /**
     * Recolhe todos os dados do formulário, valida-os e chama o ViewModel para guardar as alterações
     */
    private fun guardarAlteracoes() {
        exame?.let { exameAtual ->
            val token = sessionManager.getAuthToken()
            if (token == null) {
                Toast.makeText(context, "Sessão inválida. Não é possível guardar alterações", Toast.LENGTH_LONG).show()
                return@let
            }

            // Obtém os valores dos campos do formulário
            val displayDate = binding.editDataExame.text.toString()
            val resultado = binding.editTextResultado.text.toString()
            val observacoes = binding.editTextObservacoes.text.toString()

            // Encontra os IDs correspondentes aos nomes selecionados nos spinners
            val tipoExameId = tiposExameList.find { it.nome == binding.spinnerTipoExame.text.toString() }?.id
            val clinicaId = clinicasList.find { it.nome == binding.spinnerClinica.text.toString() }?.id
            val veterinarioId = veterinariosList.find { it.nome == binding.spinnerVeterinario.text.toString() }?.id

            // Converte a data para o formato que a API espera
            val apiDate = reformatDateForApi(displayDate)

            // Validação dos campos obrigatórios
            if (apiDate.isNullOrBlank() || tipoExameId == null || clinicaId == null || veterinarioId == null) {
                Toast.makeText(context, "Todos os campos obrigatórios devem ser preenchidos", Toast.LENGTH_LONG).show()
                return@let
            }

            // Chama a função do ViewModel para atualizar o exame
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
                novaImagemUri = novaImagemUri, // Passa o URI da nova imagem (pode ser nulo)
                context = requireContext()
            )
        }
    }

    /**
     * Configura o observador para o resultado da operação de atualizar o exame
     */
    private fun observeViewModel() {
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Exame atualizado com sucesso", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack() // Volta para o ecrã de detalhes
            }.onFailure { throwable ->
                Toast.makeText(context, "Erro ao atualizar exame: ${throwable.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Mostra um diálogo para o utilizador selecionar uma data
     */
    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(), { _, year, month, dayOfMonth ->
                val displayDate = String.format(Locale.ROOT, "%02d-%02d-%04d", dayOfMonth, month + 1, year)
                editText.setText(displayDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    /**
     * Formata a data da API (yyyy-MM-dd) para mostrar ao utilizador (dd-MM-yyyy)
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

    /**
     * Converte a data do ecrã (dd-MM-yyyy) de volta para o formato da API (yyyy-MM-dd)
     */
    private fun reformatDateForApi(displayDate: String?): String? {
        if (displayDate.isNullOrBlank()) return null
        return try {
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(displayDate)?.let {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
            }
        } catch (_: Exception) {
            null // retorna null se o formato for inválido
        }
    }

    /**
     * Chamado quando a view do Fragment está a ser destruída
     * É crucial limpar a referência ao binding aqui para evitar memory leaks
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
