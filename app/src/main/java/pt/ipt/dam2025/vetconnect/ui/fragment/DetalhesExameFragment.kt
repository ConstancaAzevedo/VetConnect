package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle // Importa a classe Bundle para passar dados entre fragments
import android.view.LayoutInflater // Importa o LayoutInflater para inflar (criar) as Views a partir de um layout XML
import android.view.View // Importa a classe base de todas as Views
import android.view.ViewGroup // Importa a classe base para layouts de Views
import android.widget.Toast // Importa para mostrar mensagens curtas e informativas ao utilizador
import androidx.appcompat.app.AlertDialog // Importa o construtor de diálogos de alerta para confirmações
import androidx.fragment.app.Fragment // Importa a classe base para todos os Fragments
import androidx.lifecycle.ViewModelProvider // Importa para criar e gerir a instância do ViewModel
import androidx.navigation.fragment.findNavController // Importa para gerir a navegação entre os ecrãs (fragments)
import coil.load // Importa a função de extensão da biblioteca Coil para carregar imagens
import pt.ipt.dam2025.vetconnect.R // Importa a classe R para aceder aos recursos da aplicação
import pt.ipt.dam2025.vetconnect.databinding.FragmentDetalhesExameBinding // Importa a classe de ViewBinding gerada
import pt.ipt.dam2025.vetconnect.model.Exame
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.HistoricoViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.HistoricoViewModelFactory
import java.text.SimpleDateFormat // Importa para formatar e fazer parse de datas
import java.util.Locale // Importa para definir a localização para formatação

/**
 * Fragment para a página de detalhes de um exame
 */
class DetalhesExameFragment : Fragment() {

    // Variável para o ViewBinding que garante acesso seguro às views do layout
    private var _binding: FragmentDetalhesExameBinding? = null
    private val binding get() = _binding!!

    // O ViewModel que vai orquestrar as ações do utilizador (ex: apagar exame)
    private lateinit var viewModel: HistoricoViewModel
    // O gestor de sessão para obter o token de autenticação
    private lateinit var sessionManager: SessionManager
    // O objeto Exame que contém os dados a serem mostrados neste ecrã
    private var exame: Exame? = null

    /**
     * Chamado pelo sistema para criar a hierarquia de Views do Fragment
     * É aqui que o nosso layout XML é inflado e se torna um objeto View
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout usando ViewBinding e guarda a referência
        _binding = FragmentDetalhesExameBinding.inflate(inflater, container, false)
        // Retorna a view raiz do nosso layout
        return binding.root
    }

    /**
     * Chamado logo após a view ter sido criada
     * É o local ideal para inicializar componentes obter dados e configurar listeners
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa o SessionManager e o ViewModel
        sessionManager = SessionManager(requireContext())
        val factory = HistoricoViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[HistoricoViewModel::class.java]

        // Obtém o objeto Exame que foi passado como argumento pelo fragment anterior
        // A anotação Suppress é usada porque getParcelable está "deprecated" em APIs mais recentes mas é a forma correta aqui
        @Suppress("DEPRECATION")
        exame = arguments?.getParcelable("exame")

        // Validação crucial Se não recebemos os dados do exame não podemos continuar
        if (exame == null) {
            Toast.makeText(context, "Erro ao carregar dados do exame.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack() // Volta para o ecrã anterior
            return
        }

        // Se recebemos os dados preenchemos a UI com eles
        populateUi(exame!!)
        // Configuramos os botões de editar e apagar
        setupListeners()
        // Começamos a observar os resultados das operações do ViewModel
        observeViewModel()
    }

    /**
     * Preenche todos os campos da UI com os dados do objeto Exame
     * Também gere a visibilidade de campos opcionais
     */
    private fun populateUi(exame: Exame) {
        binding.textViewTipoExameDetalhe.text = exame.tipo
        binding.textViewDataExameDetalhe.text = formatDateForDisplay(exame.dataExame)
        binding.textViewClinicaDetalhe.text = exame.clinicaNome
        binding.textViewVeterinarioDetalhe.text = exame.veterinarioNome

        // Mostra o resultado apenas se o campo não for nulo ou vazio
        if (!exame.resultado.isNullOrBlank()) {
            binding.layoutResultadoDetalhe.visibility = View.VISIBLE
            binding.textViewResultadoDetalhe.text = exame.resultado
        }

        // Mostra as observações apenas se o campo não for nulo ou vazio
        if (!exame.observacoes.isNullOrBlank()) {
            binding.layoutObservacoesDetalhe.visibility = View.VISIBLE
            binding.textViewObservacoesDetalhe.text = exame.observacoes
        }

        // Mostra a foto do relatório apenas se existir um URL
        if (!exame.ficheiroUrl.isNullOrBlank()) {
            binding.layoutFotoDetalhe.visibility = View.VISIBLE
            // Usa a biblioteca Coil para carregar a imagem a partir da URL
            binding.imageViewRelatorioDetalhe.load(exame.ficheiroUrl) {
                placeholder(R.drawable.vetconnectfundo) // Imagem mostrada enquanto a real está a carregar
                error(R.drawable.vetconnectfundo) // Imagem mostrada se ocorrer um erro no carregamento
            }
        }
    }

    /**
     * Configura os listeners de clique para os botões de "Editar" e "Apagar"
     */
    private fun setupListeners() {
        // Listener para o botão de editar
        binding.btnEditar.setOnClickListener {
            // Cria um Bundle para passar o objeto Exame para o ecrã de edição
            val bundle = Bundle().apply {
                putParcelable("exame", exame)
            }
            // Navega para o EditarExameFragment
            findNavController().navigate(R.id.action_detalhesExameFragment_to_editarExameFragment, bundle)
        }

        // Listener para o botão de apagar
        binding.btnApagar.setOnClickListener { // ID do XML
            // Mostra um diálogo de confirmação para evitar que o utilizador apague um exame por acidente
            AlertDialog.Builder(requireContext())
                .setTitle("Apagar Exame") // Título do diálogo
                .setMessage("Tem a certeza que deseja apagar este exame? Esta ação é irreversível.") // Mensagem de confirmação
                .setPositiveButton("Sim") { _, _ ->
                    // O que acontece se o utilizador clicar em "Sim"
                    val token = sessionManager.getAuthToken()
                    if (token == null) {
                        Toast.makeText(context, "Sessão inválida. Não é possível apagar.", Toast.LENGTH_LONG).show()
                        return@setPositiveButton
                    }
                    // Se o token for válido chama o ViewModel para apagar o exame
                    exame?.let {
                        viewModel.deleteExame(token, it.animalId, it.id.toLong())
                    }
                }
                .setNegativeButton("Não", null) // Não faz nada se o utilizador clicar em "Não"
                .show() // Mostra o diálogo
        }
    }

    /**
     * Configura o observador para o resultado da operação de apagar o exame
     */
    private fun observeViewModel() {
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                 // Se a operação for bem-sucedida mostra uma mensagem e volta para o ecrã anterior
                 Toast.makeText(context, "Exame apagado com sucesso!", Toast.LENGTH_SHORT).show()
                 findNavController().popBackStack()
            }.onFailure { throwable ->
                // Se a operação falhar mostra a mensagem de erro
                Toast.makeText(context, "Erro ao apagar exame: ${throwable.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Função de utilidade para formatar a data da API (yyyy-MM-dd) para um formato mais legível
     */
    private fun formatDateForDisplay(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""
        return try {
            // Define o formato de entrada (como a data chega)
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            // Tenta fazer o parse da data
            val date = parser.parse(dateString.substring(0, 10))
            // Define o formato de saída
            val formatter = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("pt-PT"))
            // Retorna a data formatada
            formatter.format(date!!)
        } catch (_: Exception) {
            // Se ocorrer um erro de formatação retorna a string original como fallback
            dateString
        }
    }

    /**
     * Chamado quando a view do Fragment está a ser destruída
     * É crucial limpar a referência ao binding aqui para evitar memory leaks
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpa a referência ao binding
    }
}
