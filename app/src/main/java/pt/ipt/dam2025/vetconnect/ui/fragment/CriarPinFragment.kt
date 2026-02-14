package pt.ipt.dam2025.vetconnect.ui.fragment

import android.content.Context // Importa o contexto da aplicação
import android.os.Bundle // Importa a classe Bundle para passar dados entre fragments
import android.view.LayoutInflater // Importa o LayoutInflater para inflar layouts XML
import android.view.View // Importa a classe base de todas as Views
import android.view.ViewGroup // Importa a classe base para layouts de Views
import android.widget.Button // Importa a classe Button
import android.widget.ImageView // Importa a classe ImageView
import android.widget.Toast // Importa para mostrar mensagens curtas ao utilizador
import androidx.core.content.edit // Importa a função de extensão KTX para SharedPreferences
import androidx.fragment.app.Fragment // Importa a classe base para todos os Fragments
import androidx.lifecycle.ViewModelProvider // Importa para criar e gerir ViewModels
import androidx.navigation.fragment.findNavController // Importa para gerir a navegação entre fragments
import pt.ipt.dam2025.vetconnect.R // Importa a classe R para aceder aos recursos da aplicação
import pt.ipt.dam2025.vetconnect.databinding.FragmentCriarPinBinding // Importa a classe de ViewBinding gerada
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModelFactory

/**
 * Fragmetn para a página de criar um pin
 */
class CriarPinFragment : Fragment() {

    // Variável para o ViewBinding que permite aceder às views do layout de forma segura
    private var _binding: FragmentCriarPinBinding? = null
    private val binding get() = _binding!!

    // O ViewModel que contém a lógica de negócio
    private lateinit var viewModel: UtilizadorViewModel
    // StringBuilder para construir a string do PIN à medida que o utilizador digita
    private val pin = StringBuilder()
    // Lista das 6 ImageViews que representam os pontos do PIN
    private lateinit var pinDots: List<ImageView>
    // O email do utilizador que está a criar o PIN (recebido do fragment anterior)
    private lateinit var userEmail: String
    // O nome do utilizador (também recebido do fragment anterior)
    private lateinit var userName: String

    /**
     * Chamado para inflar o layout do fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCriarPinBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Chamado depois da view ter sido criada
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

        // Inicializa a lista de ImageViews dos pontos do PIN
        pinDots = listOf(
            binding.pin1, binding.pin2, binding.pin3,
            binding.pin4, binding.pin5, binding.pin6
        )
        // Configura os listeners dos botões
        setupListeners()
        // Começa a observar os resultados do ViewModel
        observeViewModel()
    }

    /**
     * Configura os listeners de clique para o teclado numérico
     */
    private fun setupListeners() {
        // Cria um listener de clique único para todos os botões numéricos
        val numberButtonClickListener = View.OnClickListener { view ->
            // Só adiciona um dígito se o PIN ainda não tiver 6 caracteres
            if (pin.length < 6) {
                pin.append((view as Button).text)
                updatePinDots() // Atualiza a UI dos pontos do PIN
                // Se o PIN atingiu 6 dígitos, chama o ViewModel para o criar na API
                if (pin.length == 6) {
                    viewModel.criarPin(userEmail, pin.toString())
                }
            }
        }

        // Lista de todos os botões numéricos
        val buttons = listOf(
            binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6,
            binding.btn7, binding.btn8, binding.btn9, binding.btn0
        )
        // Associa o listener de clique a cada botão
        buttons.forEach { it.setOnClickListener(numberButtonClickListener) }

        // Configura o listener para o botão de apagar
        binding.btnDelete.setOnClickListener {
            if (pin.isNotEmpty()) {
                pin.deleteCharAt(pin.length - 1) // Remove o último dígito
                updatePinDots() // Atualiza a UI
            }
        }
    }

    /**
     * Observa o resultado da operação de criar o PIN
     */
    private fun observeViewModel() {
        viewModel.createPinResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "PIN criado com sucesso", Toast.LENGTH_SHORT).show()
                // Guarda a nova conta localmente para aparecer no spinner de login
                saveAccountLocally()
                // Navega para a página principal da aplicação
                findNavController().navigate(R.id.action_criarPinFragment_to_homeFragment)
            }.onFailure { throwable ->
                Toast.makeText(context, "Erro ao criar PIN: ${throwable.message}", Toast.LENGTH_LONG).show()
                pin.clear() // Limpa o PIN em caso de erro
                updatePinDots() // Atualiza a UI
            }
        }
    }

    /**
     * Guarda os dados da nova conta (nome e email) nas SharedPreferences
     * para que apareça no spinner da página de login da próxima vez
     */
    private fun saveAccountLocally() {
        if (userName.isNotBlank() && userEmail.isNotBlank()) {
            val sharedPrefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            // Obtém o conjunto de contas já existentes
            val accounts = sharedPrefs.getStringSet("REGISTERED_ACCOUNTS", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
            // Adiciona a nova conta no formato "Nome:::email@exemplo.com"
            accounts.add("$userName:::$userEmail")
            // Guarda o novo conjunto de contas de volta nas SharedPreferences
            sharedPrefs.edit { putStringSet("REGISTERED_ACCOUNTS", accounts) }
        }
    }

    /**
     * Atualiza a aparência dos pontos do PIN (preenchido ou vazio)
     * com base no número de dígitos inseridos
     */
    private fun updatePinDots() {
        for (i in pinDots.indices) {
            pinDots[i].setImageResource(
                if (i < pin.length) R.drawable.ic_pin_dot_depois
                else R.drawable.ic_pin_dot_antes
            )
        }
    }

    /**
     * Chamado quando a view do Fragment está a ser destruída
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpa a referência ao binding para evitar memory leaks
    }
}
