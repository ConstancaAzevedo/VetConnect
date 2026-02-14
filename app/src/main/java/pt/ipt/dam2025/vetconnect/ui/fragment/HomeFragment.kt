package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle // Importa a classe Bundle para guardar e recuperar o estado do Fragment
import android.view.LayoutInflater // Importa o LayoutInflater para inflar (criar) as Views a partir de um layout XML
import android.view.View // Importa a classe base de todas as Views
import android.view.ViewGroup // Importa a classe base para layouts de Views
import androidx.activity.OnBackPressedCallback // Importa para intercetar o clique no botão "voltar" do sistema
import androidx.fragment.app.Fragment // Importa a classe base para todos os Fragments
import androidx.lifecycle.ViewModelProvider // Importa para criar e gerir a instância do ViewModel
import androidx.navigation.fragment.findNavController // Importa para gerir a navegação entre os ecrãs (fragments)
import pt.ipt.dam2025.vetconnect.R // Importa a classe R para aceder aos recursos da aplicação
import pt.ipt.dam2025.vetconnect.databinding.FragmentHomeBinding // Importa a classe de ViewBinding gerada para o nosso layout
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.AnimalViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.AnimalViewModelFactory

/**
 * Fragment para a página home da aplicação
 */
class HomeFragment : Fragment() {

    // Variável para o ViewBinding que permite aceder às views do layout de forma segura
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // O gestor de sessão para obter o token e os IDs guardados
    private lateinit var sessionManager: SessionManager
    // O ViewModel de Animal para ir buscar a lista de animais
    private lateinit var animalViewModel: AnimalViewModel

    /**
     * Chamado pelo sistema para criar a hierarquia de Views do Fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout usando ViewBinding e guarda a referência
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Chamado logo após a view ter sido criada
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // inicializa o SessionManager e o ViewModel
        sessionManager = SessionManager(requireContext())
        val factory = AnimalViewModelFactory(requireActivity().application)
        animalViewModel = ViewModelProvider(this, factory)[AnimalViewModel::class.java]

        // Configura todos os listeners de clique para os cartões do menu
        setupListeners()
        // Começa a observar os dados do ViewModel
        observeViewModel()

        // Pede ao ViewModel para ir buscar a lista de animais do tutor
        val token = sessionManager.getAuthToken()
        val userId = sessionManager.getUserId()
        if (token != null && userId != -1) {
            animalViewModel.getAnimaisDoTutor(token, userId)
        }

        // Interceta o botão "voltar" do sistema
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Em vez de voltar ao ecrã de login, fecha a aplicação
                // Este é o comportamento esperado para um ecrã principal
                requireActivity().finish()
            }
        })
    }

    /**
     * Observa o LiveData da lista de animais
     */
    private fun observeViewModel() {
        animalViewModel.animais.observe(viewLifecycleOwner) { animais ->
            // Se a lista não for vazia, guarda o ID do primeiro animal na sessão
            // Este ID será usado em toda a aplicação
            animais?.firstOrNull()?.let {
                sessionManager.saveAnimalId(it.id)
            }
        }
    }

    /**
     * Configura os listeners de clique para cada um dos cartões do menu
     * Cada clique navega para o fragment correspondente
     */
    private fun setupListeners() {
        binding.cardMarcarConsulta.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_marcarConsultaFragment)
        }
        binding.cardAgendarVacina.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_agendarVacinaFragment)
        }
        binding.cardConsultas.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_consultasFragment)
        }
        binding.cardMinhasVacinas.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_vacinasFragment)
        }
        binding.cardAnimal.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_animalFragment)
        }
        binding.cardHistorico.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_historicoFragment)
        }
        binding.cardPerfil.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_perfilFragment)
        }
        binding.btnDefinicoes.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_definicoesFragment)
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
