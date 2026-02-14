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
import pt.ipt.dam2025.vetconnect.R // Importa a classe R para aceder aos recursos da aplicação (desenhos, strings, etc.)
import pt.ipt.dam2025.vetconnect.databinding.FragmentDetalhesConsultaBinding // Importa a classe de ViewBinding gerada para o nosso layout
import pt.ipt.dam2025.vetconnect.model.Consulta
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.ConsultaViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.ConsultaViewModelFactory
import java.text.SimpleDateFormat // Importa para formatar e fazer parse de datas
import java.util.Locale // Importa para definir a localização para formatação (ex: português)

/**
 * Fragment para a página de detalhes de uma consulta
 */
class DetalhesConsultaFragment : Fragment() {

    // Variável privada e anulável para o objeto de ViewBinding
    // Este padrão é usado para garantir que o binding é nulo quando a view do fragment não existe
    private var _binding: FragmentDetalhesConsultaBinding? = null
    // Propriedade "get-only" não nula para aceder ao binding de forma segura
    // O "!!" força a app a falhar se tentarmos aceder ao binding depois do onDestroyView, o que ajuda a encontrar bugs
    private val binding get() = _binding!!

    // O ViewModel que vai orquestrar as ações do utilizador (ex: cancelar consulta)
    private lateinit var viewModel: ConsultaViewModel
    // O gestor de sessão para obter o token de autenticação
    private lateinit var sessionManager: SessionManager
    // O objeto Consulta que contém os dados a serem mostrados neste ecrã
    private var consulta: Consulta? = null

    /**
     * Chamado pelo sistema para criar a hierarquia de Views do Fragment
     * É aqui que o nosso layout XML é inflado e se torna um objeto View
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout 'fragment_detalhes_consulta.xml' usando o ViewBinding
        _binding = FragmentDetalhesConsultaBinding.inflate(inflater, container, false)
        // Retorna a view raiz do nosso layout
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
        val factory = ConsultaViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[ConsultaViewModel::class.java]

        // Obtém o objeto Consulta que foi passado como argumento pelo fragment anterior
        // A anotação Suppress é usada porque getParcelable está "deprecated" em APIs mais recentes mas ainda é seguro de usar aqui
        @Suppress("DEPRECATION")
        consulta = arguments?.getParcelable("consulta")

        // Validação crucial: Se, por alguma razão, não recebemos os dados da consulta, não podemos continuar
        if (consulta == null) {
            Toast.makeText(context, "Erro ao carregar dados da consulta.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack() // Volta para o ecrã anterior
            return
        }

        // Se recebemos os dados, preenchemos a UI com eles
        populateUi(consulta!!)
        // Configuramos os botões de editar e apagar
        setupListeners()
        // Começamos a observar os resultados das operações do ViewModel
        observeViewModel()
    }

    /**
     * Preenche todos os campos da UI com os dados do objeto Consulta
     */
    private fun populateUi(consulta: Consulta) {
        binding.motivo.text = consulta.motivo
        binding.dataConsulta.text = formatDateForDisplay(consulta.data) // Usa a função para formatar a data
        binding.clinicaConsulta.text = consulta.clinicaNome
        binding.veterinarioConsulta.text = consulta.veterinarioNome
        binding.observacoesConsulta.text = consulta.observacoes

        // Mostra o estado da consulta em maiúsculas
        binding.estadoConsulta.text = consulta.estado.uppercase(Locale.ROOT)
        // Escolhe uma cor de fundo diferente com base no estado da consulta para um feedback visual claro
        val backgroundRes = when (consulta.estado.lowercase(Locale.ROOT)) {
            "marcada" -> R.drawable.background_estado_agendada
            "realizada" -> R.drawable.background_estado_administrada
            "cancelada" -> R.drawable.background_estado_atrasada
            else -> android.R.color.transparent // Cor transparente como fallback
        }
        binding.estadoConsulta.setBackgroundResource(backgroundRes)
    }

    /**
     * Configura os listeners de clique para os botões de "Editar" e "Apagar"
     */
    private fun setupListeners() {
        // Listener para o botão de editar
        binding.btnEditar.setOnClickListener {
            // Cria um Bundle para passar o objeto Consulta para o ecrã de edição
            val bundle = Bundle().apply {
                putParcelable("consulta", consulta)
            }
            // Navega para o EditarConsultaFragment
            findNavController().navigate(R.id.action_detalhesConsultaFragment_to_editarConsultaFragment, bundle)
        }

        // Listener para o botão de apagar/cancelar
        binding.btnApagar.setOnClickListener {
            // Mostra um diálogo de confirmação para evitar cancelamentos acidentais
            AlertDialog.Builder(requireContext())
                .setTitle("Cancelar Consulta") // Título do diálogo
                .setMessage("Tem a certeza que deseja cancelar esta consulta?") // Mensagem de confirmação
                .setPositiveButton("Sim") { _, _ ->
                    // O que acontece se o utilizador clicar em "Sim"
                    val token = sessionManager.getAuthToken()
                    if (token == null) {
                        Toast.makeText(context, "Sessão inválida. Não é possível cancelar.", Toast.LENGTH_LONG).show()
                        return@setPositiveButton
                    }
                    // Se o token for válido chama o ViewModel para cancelar a consulta
                    consulta?.let { viewModel.cancelarConsulta(token, it.id) }
                }
                .setNegativeButton("Não", null) // Não faz nada se o utilizador clicar em "Não"
                .show() // Mostra o diálogo
        }
    }

    /**
     * Configura o observador para o resultado da operação de cancelar a consulta
     */
    private fun observeViewModel() {
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                 // Se a operação for bem-sucedida, mostra uma mensagem e volta para o ecrã anterior
                 Toast.makeText(context, "Consulta cancelada com sucesso!", Toast.LENGTH_SHORT).show()
                 findNavController().popBackStack()
            }.onFailure { throwable ->
                // Se a operação falhar, mostra a mensagem de erro
                Toast.makeText(context, "Erro ao cancelar consulta: ${throwable.message}", Toast.LENGTH_LONG).show()
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
            // Define o formato de saída (como queremos mostrar)
            val formatter = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("pt-PT"))
            // Retorna a data formatada
            formatter.format(date!!)
        } catch (_: Exception) {
            // Se ocorrer um erro de formatação, retorna a string original como fallback
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
