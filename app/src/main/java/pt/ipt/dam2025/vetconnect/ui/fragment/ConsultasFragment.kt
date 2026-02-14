package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle // Importa a classe Bundle para passar dados entre fragments
import android.view.LayoutInflater // Importa o LayoutInflater para inflar layouts XML
import android.view.View // Importa a classe base de todas as Views
import android.view.ViewGroup // Importa a classe base para layouts de Views
import android.widget.Toast // Importa para mostrar mensagens curtas ao utilizador
import androidx.fragment.app.Fragment // Importa a classe base para todos os Fragments
import androidx.lifecycle.ViewModelProvider // Importa para criar e gerir ViewModels
import androidx.navigation.fragment.findNavController // Importa para gerir a navegação entre fragments
import androidx.recyclerview.widget.LinearLayoutManager // Importa o gestor de layout para o RecyclerView
import pt.ipt.dam2025.vetconnect.R // Importa a classe R para aceder aos recursos da aplicação
import pt.ipt.dam2025.vetconnect.databinding.FragmentConsultasBinding // Importa a classe de ViewBinding gerada
import pt.ipt.dam2025.vetconnect.model.Consulta
import pt.ipt.dam2025.vetconnect.ui.adapter.ConsultasAdapter
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.ConsultaViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.ConsultaViewModelFactory

/**
 * Fragment para a página de consultas
 */
class ConsultasFragment : Fragment() {

    // Variável para o ViewBinding que permite aceder às views do layout de forma segura
    private var _binding: FragmentConsultasBinding? = null
    private val binding get() = _binding!!

    // O ViewModel que contém a lógica de negócio e os dados para este ecrã
    private lateinit var viewModel: ConsultaViewModel
    // O Adapter que liga os dados da lista de consultas ao RecyclerView
    private lateinit var consultaAdapter: ConsultasAdapter
    // O gestor de sessão para obter o token e o ID do utilizador
    private lateinit var sessionManager: SessionManager

    /**
     * Chamado para inflar o layout do fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout usando ViewBinding e guarda a referência
        _binding = FragmentConsultasBinding.inflate(inflater, container, false)
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
        val factory = ConsultaViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[ConsultaViewModel::class.java]

        // Configura o RecyclerView e o seu adapter
        setupRecyclerView()
        // Começa a observar os dados do ViewModel
        observeViewModel()
    }

    /**
     * Configura o RecyclerView, o seu LayoutManager e o seu Adapter
     */
    private fun setupRecyclerView() {
        // Inicializa o adapter e define a ação de clique num item
        consultaAdapter = ConsultasAdapter { consulta ->
            // Quando um item é clicado, navega para o ecrã de detalhes
            navigateToDetalhes(consulta)
        }
        // Aplica as configurações ao RecyclerView
        binding.recyclerViewConsultas.apply {
            // Define o gestor de layout (ex: linear, grid)
            layoutManager = LinearLayoutManager(context)
            // Associa o adapter ao RecyclerView
            adapter = consultaAdapter
        }
    }

    /**
     * Configura o observador para os dados das consultas que vêm do ViewModel
     */
    private fun observeViewModel() {
        // Obtém o token e o ID do utilizador da sessão
        val token = sessionManager.getAuthToken()
        val userId = sessionManager.getUserId()

        // Validação de segurança para garantir que a sessão é válida
        if (token == null || userId == -1) {
            Toast.makeText(context, "Sessão inválida. Por favor, reinicie a aplicação.", Toast.LENGTH_LONG).show()
            // Mostra a mensagem de "lista vazia" se a sessão for inválida
            binding.emptyView.visibility = View.VISIBLE
            return
        }

        // Pede ao ViewModel a lista de consultas e começa a observá-la
        viewModel.getConsultas(token, userId).observe(viewLifecycleOwner) { consultas ->
            // Quando a lista de consultas muda, submete a nova lista ao adapter
            // O ListAdapter irá calcular as diferenças e animar as alterações automaticamente
            consultaAdapter.submitList(consultas)
            // Mostra ou esconde a mensagem de "lista vazia" com base no tamanho da lista
            binding.emptyView.visibility = if (consultas.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }

    /**
     * Navega para o ecrã de detalhes da consulta passando o objeto Consulta selecionado
     */
    private fun navigateToDetalhes(consulta: Consulta) {
        // Cria um Bundle para passar o objeto Consulta como argumento
        // O objeto tem de ser Parcelable para isto funcionar
        val bundle = Bundle().apply {
            putParcelable("consulta", consulta)
        }
        // Navega para o DetalhesConsultaFragment usando a ação definida no nav_graph.xml
        findNavController().navigate(R.id.action_consultasFragment_to_detalhesConsultaFragment, bundle)
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
