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
import pt.ipt.dam2025.vetconnect.databinding.FragmentRegistarBinding // Importa a classe de ViewBinding gerada
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModelFactory

/**
 * Fragment para a página de registo do utilizador
 */
class RegistarFragment : Fragment() {

    // Variável para o ViewBinding que garante acesso seguro às views do layout
    private var _binding: FragmentRegistarBinding? = null
    private val binding get() = _binding!!

    // O ViewModel que contém a lógica de negócio para o registo
    private lateinit var viewModel: UtilizadorViewModel

    /**
     * Chamado pelo sistema para criar a hierarquia de Views do Fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout usando ViewBinding e guarda a referência
        _binding = FragmentRegistarBinding.inflate(inflater, container, false)
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

        // Configura os listeners dos botões
        setupListeners()
        // Começa a observar os resultados do ViewModel
        observeViewModel()
    }

    /**
     * Configura o listener de clique para o botão de registo
     */
    private fun setupListeners() {
        binding.btnRegistar.setOnClickListener {
            // Obtém os dados dos campos de texto, removendo espaços em branco no início e no fim
            val nome = binding.nameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val telemovel = binding.phoneInput.text.toString().trim()

            // Validação simples para garantir que nenhum campo está vazio
            if (nome.isEmpty() || email.isEmpty() || telemovel.isEmpty()) {
                Toast.makeText(context, "Todos os campos são obrigatórios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Para a execução da função se a validação falhar
            }

            // Se a validação passar, chama a função do ViewModel para adicionar o utilizador
            viewModel.adicionarUtilizador(nome, email, telemovel)
        }
    }

    /**
     * Configura o observador para o resultado da operação de registo
     * A UI reage ao que acontece no ViewModel
     */
    private fun observeViewModel() {
        viewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            // O resultado é um objeto Result que pode ser de sucesso ou falha
            result.onSuccess { registrationResponse ->
                // Se o registo for bem-sucedido, mostra uma mensagem e prepara os dados para o próximo ecrã
                Toast.makeText(context, "Código enviado para o seu telemóvel", Toast.LENGTH_LONG).show()
                // Cria um Bundle para passar os dados necessários para o VerificacaoFragment
                val bundle = Bundle().apply {
                    putString("email", registrationResponse.user.email)
                    putString("name", registrationResponse.user.nome) // Passa também o nome
                    putString("verificationCode", registrationResponse.verificationCode) // Passa o código (para depuração)
                }
                // Navega para o VerificacaoFragment com os dados no Bundle
                findNavController().navigate(R.id.action_registarFragment_to_verificacaoFragment, bundle)
            }.onFailure { throwable ->
                // Se o registo falhar, mostra a mensagem de erro que vem da API
                Toast.makeText(context, "Erro no registo: ${throwable.message}", Toast.LENGTH_LONG).show()
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
