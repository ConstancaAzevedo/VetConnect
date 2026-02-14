package pt.ipt.dam2025.vetconnect.ui.fragment

import android.os.Bundle // Importa a classe Bundle para guardar e recuperar o estado do Fragment
import android.view.LayoutInflater // Importa o LayoutInflater para inflar (criar) as Views a partir de um layout XML
import android.view.View // Importa a classe base de todas as Views
import android.view.ViewGroup // Importa a classe base para layouts de Views
import android.widget.Toast // Importa para mostrar mensagens curtas ao utilizador
import androidx.fragment.app.Fragment // Importa a classe base para todos os Fragments
import androidx.lifecycle.ViewModelProvider // Importa para criar e gerir a instância do ViewModel
import pt.ipt.dam2025.vetconnect.R // Importa a classe R para aceder aos recursos da aplicação (strings, etc)
import pt.ipt.dam2025.vetconnect.databinding.FragmentPerfilBinding // Importa a classe de ViewBinding gerada
import pt.ipt.dam2025.vetconnect.model.UpdateUserRequest
import pt.ipt.dam2025.vetconnect.model.Utilizador
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModel
import pt.ipt.dam2025.vetconnect.viewmodel.UtilizadorViewModelFactory

/**
 * Fragment para a página de perfil do utilizador
 */
class PerfilFragment : Fragment() {

    // Variável para o ViewBinding que garante acesso seguro às views do layout
    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    // O ViewModel que contém a lógica de negócio e os dados para este ecrã
    private lateinit var viewModel: UtilizadorViewModel
    // O gestor de sessão para obter o token e o ID do utilizador
    private lateinit var sessionManager: SessionManager
    // Variável para controlar se o ecrã está em modo de edição ou visualização
    private var isEditMode = false
    // Guarda uma cópia local do utilizador para referência
    private var currentUser: Utilizador? = null

    /**
     * Chamado pelo sistema para criar a hierarquia de Views do Fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout usando ViewBinding e guarda a referência
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
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

        // Configura o estado inicial da UI e os listeners
        setupUI()
        // Começa a observar os dados do ViewModel
        observeViewModel()

        // Obtém os dados da sessão
        val token = sessionManager.getAuthToken()
        val userId = sessionManager.getUserId()

        // Se a sessão for válida, pede ao ViewModel para forçar a atualização dos dados do utilizador a partir da API
        // Isto garante que estamos a ver a informação mais recente
        if (token != null && userId != -1) {
            viewModel.refreshUser(token, userId)
        } else {
            Toast.makeText(context, "Sessão inválida. Por favor reinicie a aplicação", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Configura o estado inicial da UI, nomeadamente o botão de editar/guardar e os campos de texto
     */
    private fun setupUI() {
        binding.btnEditarGuardar.setOnClickListener { toggleEditMode() }
        // Começa em modo de visualização (campos não editáveis)
        setFieldsEditable(false)
    }

    /**
     * Configura os observadores para os LiveData do ViewModel
     */
    private fun observeViewModel() {
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            // Observa o Flow de dados do utilizador vindo da base de dados local
            // A UI irá atualizar-se automaticamente sempre que os dados do utilizador mudarem na BD
            viewModel.getUser(userId).observe(viewLifecycleOwner) { user ->
                user?.let {
                    currentUser = it
                    populateUI(it)
                }
            }
        }

        // Observa o resultado da operação de atualização do perfil
        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Perfil atualizado com sucesso", Toast.LENGTH_SHORT).show()
                // Após guardar com sucesso, volta para o modo de visualização
                setFieldsEditable(false)
                binding.btnEditarGuardar.setText(R.string.editar) // Volta a mostrar "Editar"
                isEditMode = false
            }.onFailure { throwable ->
                Toast.makeText(context, "Erro ao atualizar o perfil: ${throwable.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Preenche todos os campos da UI com os dados do objeto Utilizador
     */
    private fun populateUI(user: Utilizador) {
        binding.nomeDado.setText(user.nome)
        binding.emailDado.setText(user.email)
        binding.telemovelDado.setText(user.telemovel)
        binding.nacionalidadeDado.setText(user.nacionalidade)
        binding.sexoDado.setText(user.sexo)
        binding.ccDado.setText(user.cc)
        binding.dataDado.setText(user.dataNascimento)
        binding.moradaDado.setText(user.morada)
    }

    /**
     * Alterna entre o modo de edição e o modo de visualização
     * Esta função é chamada sempre que o botão "Editar/Guardar" é clicado
     */
    private fun toggleEditMode() {
        // Inverte o valor do booleano de modo de edição
        isEditMode = !isEditMode
        // Ativa ou desativa a edição dos campos de texto
        setFieldsEditable(isEditMode)
        if (isEditMode) {
            // Se entrámos em modo de edição, o botão deve mostrar "Guardar"
            binding.btnEditarGuardar.setText(R.string.guardar)
        } else {
            // Se saímos do modo de edição (clicámos em "Guardar"), chama a função para guardar as alterações
            saveProfileChanges()
        }
    }

    /**
     * Ativa ou desativa a capacidade de editar todos os campos de perfil
     */
    private fun setFieldsEditable(isEditable: Boolean) {
        val fields = listOf(binding.nomeDado, binding.emailDado, binding.telemovelDado, binding.nacionalidadeDado, binding.sexoDado, binding.ccDado, binding.dataDado, binding.moradaDado)
        fields.forEach { it.isEnabled = isEditable }
    }

    /**
     * Recolhe os dados dos campos, cria um objeto de pedido e chama o ViewModel para guardar as alterações
     */
    private fun saveProfileChanges() {
        val token = sessionManager.getAuthToken()
        val userId = sessionManager.getUserId()

        if (token == null || userId == -1) {
            Toast.makeText(context, "Sessão inválida. Não é possível guardar alterações", Toast.LENGTH_LONG).show()
            return
        }

        // Cria o objeto de pedido (Request) com os novos dados para enviar à API
        val request = UpdateUserRequest(
            nome = binding.nomeDado.text.toString(),
            email = binding.emailDado.text.toString(),
            tipo = currentUser?.tipo ?: "tutor"
        )

        // Chama a função do ViewModel para atualizar o utilizador
        viewModel.updateUser(token, userId, request)
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
