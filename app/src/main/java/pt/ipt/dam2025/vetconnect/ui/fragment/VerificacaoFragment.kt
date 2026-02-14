package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle // Importa a classe Bundle para passar dados entre fragments
import android.view.LayoutInflater // Importa o LayoutInflater para inflar (criar) as Views a partir de um layout XML
import android.view.View // Importa a classe base de todas as Views
import android.view.ViewGroup // Importa a classe base para layouts de Views
import android.widget.Toast // Importa para mostrar mensagens curtas ao utilizador
import androidx.fragment.app.Fragment // Importa a classe base para todos os Fragments
import androidx.lifecycle.ViewModelProvider // Importa para criar e gerir a instância do ViewModel
import androidx.navigation.fragment.findNavController // Importa para gerir a navegação entre os ecrãs (fragments)
import pt.ipt.dam2025.vetconnect.R // Importa a classe R para aceder aos recursos da aplicação
import pt.ipt.dam2025.vetconnect.databinding.FragmentVerificacaoBinding // Importa a classe de ViewBinding gerada
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModelFactory

/**
 * Fragment para a página de verificação de código
 */
class VerificacaoFragment : Fragment() {

    // Variável para o ViewBinding que garante acesso seguro às views do layout
    private var _binding: FragmentVerificacaoBinding? = null
    private val binding get() = _binding!!

    // O ViewModel que contém a lógica de negócio
    private lateinit var viewModel: UtilizadorViewModel
    // O email do utilizador que está a ser verificado
    private lateinit var userEmail: String
    // O nome do utilizador
    private lateinit var userName: String

    /**
     * Chamado pelo sistema para criar a hierarquia de Views do Fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout usando ViewBinding e guarda a referência
        _binding = FragmentVerificacaoBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Chamado logo após a view ter sido criada
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa o ViewModel usando a sua Factory
        val factory = UtilizadorViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[UtilizadorViewModel::class.java]

        // Obtém o email e o nome passados como argumentos do RegistarFragment
        userEmail = arguments?.getString("email") ?: ""
        userName = arguments?.getString("name") ?: ""

        // Validação de segurança se o email não for passado o ecrã não pode funcionar
        if (userEmail.isEmpty()) {
            Toast.makeText(context, "Erro: Email não encontrado", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        // Configura o listener para o botão de verificar
        binding.btnVerificar.setOnClickListener {
            val codigo = binding.codigoVerificacao.text.toString().trim()
            if (codigo.length == 6) {
                // Chama a função do ViewModel para verificar o código
                viewModel.verificarCodigo(userEmail, codigo)
            } else {
                binding.codigoVerificacao.error = "O código deve ter 6 dígitos"
            }
        }

        // Começa a observar os resultados do ViewModel
        observeViewModel()
    }

    /**
     * Configura o observador para o resultado da operação de verificação
     */
    private fun observeViewModel() {
        viewModel.verificationResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { verificationResponse ->
                // Se a verificação for bem-sucedida, mostra uma mensagem e navega para o ecrã de criar PIN
                Toast.makeText(context, verificationResponse.message, Toast.LENGTH_SHORT).show()
                // Cria um Bundle para passar o email e o nome para o CriarPinFragment
                val bundle = Bundle().apply {
                    putString("email", userEmail)
                    putString("name", userName)
                }
                findNavController().navigate(R.id.action_verificacaoFragment_to_criarPinFragment, bundle)
            }.onFailure { throwable ->
                // Se a verificação falhar, mostra a mensagem de erro
                Toast.makeText(context, "Erro na verificação: ${throwable.message}", Toast.LENGTH_LONG).show()
            }
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
