package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle // Importa a classe Bundle para guardar e recuperar o estado do Fragment
import android.view.LayoutInflater // Importa o LayoutInflater para inflar (criar) as Views a partir de um layout XML
import android.view.View // Importa a classe base de todas as Views
import android.view.ViewGroup // Importa a classe base para layouts de Views
import android.widget.AdapterView // Importa a interface para listeners de seleção em widgets como Spinners
import android.widget.ArrayAdapter // Importa o ArrayAdapter para popular Spinners e AutoCompleteTextViews
import android.widget.Button // Importa a classe Button
import android.widget.ImageView // Importa a classe ImageView para mostrar imagens (os pontos do PIN)
import android.widget.Toast // Importa para mostrar mensagens curtas ao utilizador
import androidx.fragment.app.Fragment // Importa a classe base para todos os Fragments
import androidx.lifecycle.ViewModelProvider // Importa para criar e gerir a instância do ViewModel
import androidx.navigation.fragment.findNavController // Importa para gerir a navegação entre os ecrãs (fragments)
import com.google.android.material.snackbar.Snackbar // Importa para mostrar mensagens mais informativas
import pt.ipt.dam2025.vetconnect.R // Importa a classe R para aceder aos recursos da aplicação
import pt.ipt.dam2025.vetconnect.databinding.FragmentLoginBinding // Importa a classe de ViewBinding gerada para o nosso layout
import pt.ipt.dam2025.vetconnect.model.Utilizador
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModelFactory

/**
 * Fragment para a página de login
 */
class LoginFragment : Fragment() {

    // Variável para o ViewBinding que garante acesso seguro às views do layout
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // O ViewModel que contém a lógica de negócio
    private lateinit var viewModel: UtilizadorViewModel
    // O gestor de sessão para guardar o token e o ID do utilizador após o login
    private lateinit var sessionManager: SessionManager

    // StringBuilder para construir a string do PIN à medida que o utilizador digita
    private val pin = StringBuilder()
    // Lista das 6 ImageViews que representam os pontos do PIN na UI
    private lateinit var pinDots: List<ImageView>
    // Lista local para guardar os utilizadores recebidos da API
    private var utilizadoresList: List<Utilizador> = emptyList()
    // O email do utilizador selecionado no spinner
    private var selectedEmail: String? = null

    /**
     * Chamado pelo sistema para criar a hierarquia de Views do Fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout usando ViewBinding e guarda a referência
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Chamado logo após a view ter sido criada
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa o SessionManager e o ViewModel
        sessionManager = SessionManager(requireContext())
        val factory = UtilizadorViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[UtilizadorViewModel::class.java]

        // Inicializa a lista de ImageViews para os pontos do PIN
        pinDots = listOf(binding.pin1, binding.pin2, binding.pin3, binding.pin4, binding.pin5, binding.pin6)

        // Configura todos os listeners de clique e começa a observar os LiveData
        setupListeners()
        observeViewModel()

        // Pede ao ViewModel para ir buscar a lista de todos os utilizadores à API
        viewModel.getUtilizadores()
    }

    /* Lógica antiga que usava SharedPreferences para guardar as contas localmente
    private fun loadRegisteredAccounts() {
        val sharedPrefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val accountsSet = sharedPrefs.getStringSet("REGISTERED_ACCOUNTS", setOf()) ?: setOf()
        accountsSet.forEach {
            val parts = it.split(":::")
            if (parts.size == 2) registeredAccounts[parts[1]] = parts[0] // email -> nome
        }

        if (registeredAccounts.isEmpty()) {
            binding.contaSpinner.visibility = View.GONE
            binding.welcomeText.setText(R.string.bem_vindo)
        } else {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, registeredAccounts.values.toList())
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.contaSpinner.adapter = adapter
        }
    }
    */

    /**
     * Configura os listeners de clique para o spinner e para o teclado numérico
     */
    private fun setupListeners() {
        // Listener para quando um item é selecionado no spinner de contas
        binding.contaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Guarda o email do utilizador selecionado
                selectedEmail = utilizadoresList.getOrNull(position)?.email
                // Limpa o PIN inserido anteriormente e atualiza a UI dos pontos
                pin.clear()
                updatePinDots()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { selectedEmail = null }
        }

        // Listener de clique único para todos os botões numéricos
        val numberButtonClickListener = View.OnClickListener { view ->
            if (pin.length < 6) {
                pin.append((view as Button).text)
                updatePinDots()
                // Quando o PIN atinge 6 dígitos, tenta fazer o login automaticamente
                if (pin.length == 6) attemptLogin()
            }
        }

        // Associa o listener a todos os botões de 0 a 9
        listOf(binding.btn1, binding.btn2, binding.btn3, binding.btn4, binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9, binding.btn0)
            .forEach { it.setOnClickListener(numberButtonClickListener) }

        // Listener para o botão de apagar
        binding.btnApagar.setOnClickListener {
            if (pin.isNotEmpty()) {
                pin.deleteCharAt(pin.length - 1)
                updatePinDots()
            }
        }

        // botão de esquecer pin -> mostra uma mensagem de aviso
        binding.btnEsqueci.setOnClickListener { v ->
            Snackbar.make(v, "Funcionalidade em desenvolvimento", Snackbar.LENGTH_LONG).show()
        }
        // TODO: criar uma página de recuperar pin ._.
    }

    /**
     * Configura os observadores para os LiveData do ViewModel
     * A UI reage aos resultados das chamadas à API
     */
    private fun observeViewModel() {
        // Observa o resultado do pedido da lista de utilizadores
        viewModel.utilizadores.observe(viewLifecycleOwner) { result ->
            result.onSuccess { utilizadores ->
                this.utilizadoresList = utilizadores
                if (utilizadores.isEmpty()) {
                    // Se não houver utilizadores, esconde o spinner
                    binding.contaSpinner.visibility = View.GONE
                    binding.welcomeText.text = getString(R.string.bem_vindo)
                } else {
                    // Se houver utilizadores, mostra o spinner e preenche-o
                    binding.contaSpinner.visibility = View.VISIBLE
                    // Cria um adapter com os nomes dos utilizadores
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, utilizadores.map { it.nome })
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.contaSpinner.adapter = adapter
                }
            }.onFailure {
                // Em caso de falha ao carregar as contas, mostra um erro
                Toast.makeText(context, "Erro ao carregar contas", Toast.LENGTH_SHORT).show()
                binding.contaSpinner.visibility = View.GONE
                binding.welcomeText.text = getString(R.string.bem_vindo)
            }
        }

        // Observa o resultado da tentativa de login
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { loginResponse ->
                // Se o login for bem-sucedido, mostra uma mensagem, guarda a sessão e navega para a Home
                Toast.makeText(context, loginResponse.message, Toast.LENGTH_SHORT).show()
                sessionManager.saveAuthToken(loginResponse.token)
                sessionManager.saveUserId(loginResponse.user.id)
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }.onFailure {
                // Se o login falhar (ex: PIN incorreto), mostra um erro e limpa o PIN
                Toast.makeText(context, "PIN incorreto", Toast.LENGTH_SHORT).show()
                pin.clear()
                updatePinDots()
            }
        }
    }

    /**
     * Tenta fazer o login com o email selecionado e o PIN inserido
     */
    private fun attemptLogin() {
        if (selectedEmail != null) {
            viewModel.login(selectedEmail!!, pin.toString())
        } else {
            Toast.makeText(context, "Selecione uma conta", Toast.LENGTH_SHORT).show()
            pin.clear()
            updatePinDots()
        }
    }

    /**
     * Atualiza a aparência dos pontos do PIN (preenchido ou vazio)
     */
    private fun updatePinDots() {
        for (i in pinDots.indices) {
            pinDots[i].setImageResource(if (i < pin.length) R.drawable.ic_pin_dot_depois else R.drawable.ic_pin_dot_antes)
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
