package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle // Importa a classe Bundle para passar dados entre fragments
import android.view.LayoutInflater // Importa o LayoutInflater para inflar layouts XML
import android.view.View // Importa a classe base de todas as Views
import android.view.ViewGroup // Importa a classe base para layouts de Views
import android.widget.Toast // Importa para mostrar mensagens curtas ao utilizador
import androidx.appcompat.app.AlertDialog // Importa o construtor de diálogos de alerta
import androidx.fragment.app.Fragment // Importa a classe base para todos os Fragments
import androidx.lifecycle.ViewModelProvider // Importa para criar e gerir ViewModels
import androidx.navigation.fragment.findNavController // Importa para gerir a navegação entre fragments
import pt.ipt.dam2025.vetconnect.R // Importa a classe R para aceder aos recursos da aplicação
import pt.ipt.dam2025.vetconnect.databinding.FragmentDefinicoesBinding // Importa a classe de ViewBinding gerada
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModelFactory

/**
 * Fragment para a página de Definições
 */
class DefinicoesFragment : Fragment() {

    // Variável para o ViewBinding que permite aceder às views do layout de forma segura
    private var _binding: FragmentDefinicoesBinding? = null
    private val binding get() = _binding!!

    // O ViewModel que contém a lógica de negócio
    private lateinit var viewModel: UtilizadorViewModel
    // O gestor de sessão para obter o token e limpar a sessão no logout
    private lateinit var sessionManager: SessionManager

    /**
     * Chamado para inflar o layout do fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDefinicoesBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Chamado depois da view ter sido criada
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa o SessionManager e o ViewModel
        sessionManager = SessionManager(requireContext())
        val factory = UtilizadorViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[UtilizadorViewModel::class.java]

        // Configura os listeners de clique e começa a observar os LiveData
        setupListeners()
        observeViewModel()
    }

    /**
     * Configura os listeners de clique para os botões de guardar e logout
     */
    private fun setupListeners() {
        binding.btnGuardar.setOnClickListener {
            alterarPin()
        }
        binding.btnLogout.setOnClickListener {
            confirmarLogout()
        }
    }

    /**
     * Valida os campos do formulário de alteração de PIN e chama o ViewModel
     */
    private fun alterarPin() {
        val pinAtual = binding.atualPin.text.toString()
        val novoPin = binding.novoPin.text.toString()
        val confirmarPin = binding.confirmarPin.text.toString()

        // Validação do lado do cliente para dar feedback rápido ao utilizador
        if (pinAtual.length != 6) {
            binding.atualPin.error = "O PIN atual deve ter 6 dígitos."
            return
        }
        if (novoPin.length != 6) {
            binding.novoPin.error = "O novo PIN deve ter 6 dígitos."
            return
        }
        if (novoPin != confirmarPin) {
            binding.confirmarPin.error = "Os novos PINs não coincidem."
            return
        }

        // Obtém o token para autenticar o pedido
        val token = sessionManager.getAuthToken()
        if (token == null) {
            Toast.makeText(context, "Sessão inválida. Não é possível alterar o PIN.", Toast.LENGTH_LONG).show()
            return
        }
        // Se a validação passar chama a função do ViewModel
        viewModel.alterarPin(token, pinAtual, novoPin)
    }

    /**
     * Mostra um diálogo de confirmação antes de fazer logout
     * Previne que o utilizador termine a sessão acidentalmente
     */
    private fun confirmarLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Tem a certeza que deseja terminar a sessão?")
            .setPositiveButton("Sim") { _, _ ->
                // Só faz logout se o utilizador clicar em "Sim"
                fazerLogout()
            }
            .setNegativeButton("Não", null) // Não faz nada se o utilizador clicar em "Não"
            .show()
    }

    /**
     * Chama o ViewModel para executar a operação de logout na API
     */
    private fun fazerLogout(){
        val token = sessionManager.getAuthToken()
        if (token == null) {
            Toast.makeText(context, "Sessão inválida. Não é possível fazer logout.", Toast.LENGTH_LONG).show()
            return
        }
        viewModel.logout(token)
    }

    /**
     * Configura os observadores para os LiveData do ViewModel
     * A UI reage aos resultados das operações de alterar PIN e logout
     */
    private fun observeViewModel() {
        // Observa o resultado da operação de alterar o PIN
        viewModel.pinChangeResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "PIN alterado com sucesso!", Toast.LENGTH_SHORT).show()
                // Limpa os campos após o sucesso
                binding.atualPin.text.clear()
                binding.novoPin.text.clear()
                binding.confirmarPin.text.clear()
            }.onFailure { throwable ->
                Toast.makeText(context, "Erro ao alterar PIN: ${throwable.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Observa o resultado da operação de logout
        viewModel.logoutResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                // A API confirmou o logout
                // Agora limpamos os dados da sessão local (token e IDs)
                sessionManager.clearAuth()
                Toast.makeText(context, "Sessão terminada", Toast.LENGTH_SHORT).show()
                // Navega para o ecrã de login
                findNavController().navigate(R.id.action_definicoesFragment_to_loginFragment)
            }.onFailure { throwable ->
                Toast.makeText(context, "Erro ao fazer logout: ${throwable.message}", Toast.LENGTH_LONG).show()
            }
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
