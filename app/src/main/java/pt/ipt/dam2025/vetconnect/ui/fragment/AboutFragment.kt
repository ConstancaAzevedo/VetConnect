package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle // Importa a classe Bundle para guardar e recuperar o estado do Fragment
import android.view.LayoutInflater // Importa o LayoutInflater para inflar (criar) as Views a partir de um layout XML
import android.view.View // Importa a classe base de todas as Views
import android.view.ViewGroup // Importa a classe base para layouts de Views
import androidx.fragment.app.Fragment // Importa a classe base para todos os Fragments
import pt.ipt.dam2025.vetconnect.databinding.FragmentAboutBinding // Importa a classe de ViewBinding gerada para o nosso layout

/**
 * Fragment para a página "Sobre"
 */

class AboutFragment : Fragment() {

    // Variável privada e anulável para o objeto de ViewBinding
    // O _binding só é válido entre onCreateView e onDestroyView
    private var _binding: FragmentAboutBinding? = null

    // Propriedade "get-only" não nula para aceder ao binding de forma segura
    // O "!!" garante que se o binding for acedido fora do ciclo de vida válido a app falha (fail-fast)
    private val binding get() = _binding!!

    /**
     * Chamado pelo sistema para criar a hierarquia de Views do Fragment
     * É aqui que o nosso layout XML é inflado e se torna um objeto View
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout 'fragment_about.xml' usando o ViewBinding
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        // Retorna a view raiz do nosso layout (o ConstraintLayout)
        return binding.root
    }

    /**
     * Chamado quando a view do Fragment está a ser destruída
     * É crucial limpar a referência ao binding aqui para evitar memory leaks
     */
    override fun onDestroyView() {
        // Chama a implementação da classe pai primeiro
        super.onDestroyView()
        // Define o _binding como nulo para libertar a memória
        _binding = null
    }
}
