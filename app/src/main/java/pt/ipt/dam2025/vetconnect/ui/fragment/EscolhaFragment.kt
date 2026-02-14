package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle // Importa a classe Bundle para guardar e recuperar o estado do Fragment
import android.view.LayoutInflater // Importa o LayoutInflater para inflar (criar) as Views a partir de um layout XML
import android.view.View // Importa a classe base de todas as Views
import android.view.ViewGroup // Importa a classe base para layouts de Views
import androidx.fragment.app.Fragment // Importa a classe base para todos os Fragments
import androidx.navigation.fragment.findNavController // Importa para gerir a navegação entre os ecrãs (fragments)
import com.google.android.material.snackbar.Snackbar // Importa para mostrar mensagens mais informativas e interativas do que um Toast
import pt.ipt.dam2025.vetconnect.R // Importa a classe R para aceder aos recursos da aplicação
import pt.ipt.dam2025.vetconnect.databinding.FragmentEscolhaBinding // Importa a classe de ViewBinding gerada para o nosso layout

/**
 * Fragment para a página em que o utilizador qur tipo de perfil irá registar
 * O perfil de veterinário é apenas ilustrativo e não será implementado
 */
class EscolhaFragment : Fragment() {

    // Variável privada e anulável para o objeto de ViewBinding
    // Este padrão é usado para garantir que o binding é nulo quando a view do fragment não existe
    private var _binding: FragmentEscolhaBinding? = null
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
        // Infla o layout 'fragment_escolha.xml' usando o ViewBinding
        _binding = FragmentEscolhaBinding.inflate(inflater, container, false)
        // Retorna a view raiz do nosso layout
        return binding.root
    }

    /**
     * Chamado logo após a view ter sido criada
     * É o local ideal para configurar os listeners de clique
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // botão tutor -> leva diretamente para a página de registo de tutor
        binding.btnTutor.setOnClickListener {
            // Usa o NavController para navegar para o RegistarFragment através da ação definida no nav_graph.xml
            findNavController().navigate(R.id.action_escolhaFragment_to_registarFragment)
        }

        // botão veterinário -> mostra uma mensagem de aviso
        binding.btnVeterinario.setOnClickListener { v ->
            // Mostra uma Snackbar a informar que a funcionalidade ainda não está disponível
            Snackbar.make(v, "Funcionalidade em desenvolvimento", Snackbar.LENGTH_LONG).show()
        }

        //  botão login -> leva diretamente para a página de login
        binding.btnLogin.setOnClickListener {
            // Usa o NavController para navegar para o LoginFragment
            findNavController().navigate(R.id.action_escolhaFragment_to_loginFragment)
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
