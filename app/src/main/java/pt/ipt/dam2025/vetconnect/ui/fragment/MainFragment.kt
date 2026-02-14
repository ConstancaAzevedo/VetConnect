package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle // Importa a classe Bundle para guardar e recuperar o estado do Fragment
import android.view.LayoutInflater // Importa o LayoutInflater para inflar (criar) as Views a partir de um layout XML
import android.view.View // Importa a classe base de todas as Views
import android.view.ViewGroup // Importa a classe base para layouts de Views
import androidx.fragment.app.Fragment // Importa a classe base para todos os Fragments
import androidx.navigation.fragment.findNavController // Importa para gerir a navegação entre os ecrãs (fragments)
import pt.ipt.dam2025.vetconnect.R // Importa a classe R para aceder aos recursos da aplicação
import pt.ipt.dam2025.vetconnect.databinding.FragmentMainBinding // Importa a classe de ViewBinding gerada para o nosso layout

/**
 * Fragment para a página principal
 */
class MainFragment : Fragment() {

    // Variável privada e anulável para o objeto de ViewBinding
    // Este padrão é usado para garantir que o binding é nulo quando a view do fragment não existe
    private var _binding: FragmentMainBinding? = null
    // Propriedade "get-only" não nula para aceder ao binding de forma segura
    // O "!!" força a app a falhar se tentarmos aceder ao binding depois do onDestroyView, o que ajuda a encontrar bugs
    private val binding get() = _binding!!

    /**
     * Chamado pelo sistema para criar a hierarquia de Views do Fragment
     * É aqui que o nosso layout XML é inflado e se torna um objeto View
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout 'fragment_main.xml' usando o ViewBinding
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        // Retorna a view raiz do nosso layout
        return binding.root
    }

    /**
     * Chamado logo após a view ter sido criada
     * É o local ideal para configurar os listeners de clique
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // botão login -> leva diretamente para a página login
        binding.btnLogin.setOnClickListener {
            // Usa o NavController para navegar para o LoginFragment através da ação definida no nav_graph.xml
            findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        }

        // botão registar -> leva diretamente para a página de escolha de perfil
        binding.btnRegistar.setOnClickListener {
            // Navega para o EscolhaFragment, onde o utilizador decide o tipo de perfil
            findNavController().navigate(R.id.action_mainFragment_to_escolhaFragment)
        }

        // botão about -> leva diretamente para a página about
        binding.btnAbout.setOnClickListener {
            // Navega para o AboutFragment, que mostra as informações da aplicação
            findNavController().navigate(R.id.action_mainFragment_to_aboutFragment)
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
