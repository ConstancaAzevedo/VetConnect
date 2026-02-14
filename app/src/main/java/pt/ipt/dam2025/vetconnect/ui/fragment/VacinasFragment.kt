package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle // Importa a classe Bundle para passar dados entre fragments
import android.view.LayoutInflater // Importa o LayoutInflater para inflar (criar) as Views a partir de um layout XML
import android.view.View // Importa a classe base de todas as Views
import android.view.ViewGroup // Importa a classe base para layouts de Views
import android.widget.Toast // Importa para mostrar mensagens curtas ao utilizador
import androidx.fragment.app.Fragment // Importa a classe base para todos os Fragments
import androidx.lifecycle.ViewModelProvider // Importa para criar e gerir a instância do ViewModel
import androidx.navigation.fragment.findNavController // Importa para gerir a navegação entre os ecrãs (fragments)
import androidx.recyclerview.widget.LinearLayoutManager // Importa o gestor de layout para o RecyclerView
import pt.ipt.dam2025.vetconnect.R // Importa a classe R para aceder aos recursos da aplicação
import pt.ipt.dam2025.vetconnect.databinding.FragmentVacinasBinding // Importa a classe de ViewBinding gerada
import pt.ipt.dam2025.vetconnect.model.Vacina
import pt.ipt.dam2025.vetconnect.ui.adapter.VacinaAdapter
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.VacinaViewModelFactory

/**
 * Fragment para a página das vacinas de um animal
 */
class VacinasFragment : Fragment() {

    // Variável para o ViewBinding que permite aceder às views do layout de forma segura
    private var _binding: FragmentVacinasBinding? = null
    private val binding get() = _binding!!

    // O ViewModel que contém a lógica de negócio e os dados para este ecrã
    private lateinit var viewModel: VacinaViewModel
    // O Adapter que liga os dados da lista de vacinas ao RecyclerView
    private lateinit var vacinaAdapter: VacinaAdapter
    // O gestor de sessão para obter o token e o ID do animal
    private lateinit var sessionManager: SessionManager

    /**
     * Chamado para inflar o layout do fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout usando ViewBinding e guarda a referência
        _binding = FragmentVacinasBinding.inflate(inflater, container, false)
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

        // Inicializa o ViewModel usando a sua Factory para injetar as dependências
        val factory = VacinaViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[VacinaViewModel::class.java]

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
        vacinaAdapter = VacinaAdapter { vacina ->
            // Quando um item é clicado, navega para o ecrã de detalhes dessa vacina
            navigateToDetalhes(vacina)
        }
        // Aplica as configurações ao RecyclerView
        binding.recyclerViewVacinas.apply {
            // Define o gestor de layout (neste caso, uma lista linear vertical)
            layoutManager = LinearLayoutManager(context)
            // Associa o adapter ao RecyclerView
            adapter = vacinaAdapter
        }
    }

    /**
     * Configura o observador para os dados das vacinas que vêm do ViewModel
     */
    private fun observeViewModel() {
        // Obtém o token e o ID do animal guardado na sessão
        val token = sessionManager.getAuthToken()
        val animalId = sessionManager.getAnimalId()

        // Validação de segurança para garantir que a sessão é válida
        if (token == null || animalId == -1) {
            Toast.makeText(context, "Sessão inválida. Por favor, reinicie a aplicação.", Toast.LENGTH_LONG).show()
            // Mostra a mensagem de "lista vazia" se a sessão for inválida
            binding.emptyView.visibility = View.VISIBLE
            return
        }

        // Pede ao ViewModel a lista de vacinas e começa a observá-la
        viewModel.getVacinas(token, animalId).observe(viewLifecycleOwner) { vacinas ->
            // Quando a lista de vacinas muda, submete a nova lista ao adapter
            // O ListAdapter irá calcular as diferenças e animar as alterações automaticamente
            vacinaAdapter.submitList(vacinas)
            // Mostra ou esconde a mensagem de "lista vazia" com base no tamanho da lista
            binding.emptyView.visibility = if (vacinas.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }

    /**
     * Navega para o ecrã de detalhes da vacina, passando o objeto Vacina selecionado
     */
    private fun navigateToDetalhes(vacina: Vacina) {
        // Cria um Bundle para passar o objeto Vacina como argumento
        // O objeto tem de ser Parcelable para isto funcionar
        val bundle = Bundle().apply {
            putParcelable("vacina", vacina)
        }
        // Navega para o DetalhesVacinaFragment usando a ação definida no nav_graph.xml
        findNavController().navigate(R.id.action_vacinasFragment_to_detalhesVacinaFragment, bundle)
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
