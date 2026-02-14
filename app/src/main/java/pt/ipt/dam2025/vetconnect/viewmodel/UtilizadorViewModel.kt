package pt.ipt.dam2025.vetconnect.viewmodel

import androidx.lifecycle.LiveData // Importa a classe LiveData para dados observáveis
import androidx.lifecycle.MutableLiveData // Importa a versão mutável do LiveData
import androidx.lifecycle.ViewModel // Importa a classe base ViewModel
import androidx.lifecycle.asLiveData // Importa a extensão para converter Flow em LiveData
import androidx.lifecycle.viewModelScope // Importa o contexto de coroutines para o ViewModel
import kotlinx.coroutines.launch // Importa a função para iniciar uma coroutine
import pt.ipt.dam2025.vetconnect.model.*
import pt.ipt.dam2025.vetconnect.repository.UtilizadorRepository

/**
 * ViewModel para gerir os dados e a lógica de negócio do Utilizador
 * Atua como intermediário entre a UI e o UtilizadorRepository
 */
class UtilizadorViewModel(private val repository: UtilizadorRepository) : ViewModel() {

    // LiveData para a lista de utilizadores para o spinner de login
    private val _utilizadores = MutableLiveData<Result<List<Utilizador>>>()
    val utilizadores: LiveData<Result<List<Utilizador>>> = _utilizadores

    // LiveData privado para o resultado do registo
    private val _registrationResult = MutableLiveData<Result<RegistrationResponse>>()
    // LiveData público que a UI observa para saber se o registo teve sucesso
    val registrationResult: LiveData<Result<RegistrationResponse>> = _registrationResult

    private val _verificationResult = MutableLiveData<Result<VerificationResponse>>()
    val verificationResult: LiveData<Result<VerificationResponse>> = _verificationResult

    private val _createPinResult = MutableLiveData<Result<CreatePinResponse>>()
    // LiveData público que a UI observa para saber se o PIN foi criado
    val createPinResult: LiveData<Result<CreatePinResponse>> = _createPinResult

    // LiveData privado para o resultado do login
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    // LiveData público que a UI observa para saber se o login foi bem sucedido
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    // LiveData privado para o resultado da atualização de um utilizador
    private val _updateResult = MutableLiveData<Result<Unit>>()
    // LiveData público que a UI observa para saber se os dados foram atualizados
    val updateResult: LiveData<Result<Unit>> = _updateResult

    // LiveData privado para o resultado da alteração de PIN
    private val _pinChangeResult = MutableLiveData<Result<Unit>>()
    // LiveData público que a UI observa para saber se o PIN foi alterado
    val pinChangeResult: LiveData<Result<Unit>> = _pinChangeResult

    // LiveData privado para o resultado do logout
    private val _logoutResult = MutableLiveData<Result<Unit>>()
    // LiveData público que a UI observa para saber se o logout teve sucesso
    val logoutResult: LiveData<Result<Unit>> = _logoutResult

    /**
     * Pede ao repositório para obter a lista de todos os utilizadores
     */
    fun getUtilizadores() {
        viewModelScope.launch {
            val result = repository.getUtilizadores()
            _utilizadores.postValue(result)
        }
    }

    /**
     * Pede ao repositório para criar um utilizador
     * Esta função é chamada a partir do RegistarFragment
     */
    fun adicionarUtilizador(nome: String, email: String, telemovel: String) {
        // Inicia uma coroutine no contexto do ViewModel que é cancelada automaticamente
        viewModelScope.launch {
            // Cria o objeto de pedido com os dados do novo utilizador
            val novoUtilizador = NovoUtilizador(nome, email, telemovel, "tutor")
            // Delega a chamada de rede ao repositório
            val result = repository.criarUtilizador(novoUtilizador)
            // Publica o resultado (sucesso ou falha) no LiveData para a UI reagir
            _registrationResult.postValue(result)
        }
    }

    fun verificarCodigo(email: String, codigo: String) {
        viewModelScope.launch {
            val request = VerificationRequest(email, codigo)
            val result = repository.verificarCodigo(request)
            _verificationResult.postValue(result)
        }
    }

    fun criarPin(email: String, pin: String) {
        viewModelScope.launch {
            // Cria o objeto de pedido com o email e o pin
            val request = CreatePinRequest(email, pin)
            // Delega a chamada de rede ao repositório
            val result = repository.criarPin(request)
            // Publica o resultado no LiveData correspondente
            _createPinResult.postValue(result)
        }
    }

    /**
     * Pede ao repositório para autenticar um utilizador com email e PIN
     * Chamada a partir do LoginFragment
     */
    fun login(email: String, pin: String) {
        viewModelScope.launch {
            // Delega a chamada de rede ao repositório
            val result = repository.login(email, pin)
            // Publica o resultado que contém o token e os dados do utilizador
            _loginResult.postValue(result)
        }
    }

    /**
     * Obtém os dados de um utilizador específico a partir do repositório
     * O repositório irá primeiro devolver dados da base de dados local e depois da rede
     * Retorna um LiveData que a UI (PerfilFragment) pode observar
     */
    fun getUser(userId: Int): LiveData<Utilizador?> {
        // Converte o Flow da base de dados local diretamente em LiveData
        // A UI irá receber atualizações automaticamente sempre que os dados do utilizador mudarem na BD
        return repository.getUser(userId).asLiveData()
    }

    /**
     * Pede ao repositório para atualizar os dados de um utilizador na API
     * Chamada a partir do PerfilFragment
     */
    fun updateUser(token: String, userId: Int, request: UpdateUserRequest) {
        viewModelScope.launch {
            // Delega a chamada de rede ao repositório
            val result = repository.updateUser(token, userId, request)
            // Publica o resultado (sucesso ou falha)
            _updateResult.postValue(result)
        }
    }

    /**
     * Pede ao repositório para forçar a atualização dos dados de um utilizador a partir da API
     * Útil para obter os dados mais recentes do servidor
     */
    fun refreshUser(token: String, userId: Int) {
        viewModelScope.launch {
            // Esta função não precisa de retornar um resultado
            // porque ao atualizar a BD local o LiveData do getUser será notificado automaticamente
            repository.refreshUser(token, userId)
        }
    }

    /**
     * Pede ao repositório para alterar o PIN do utilizador autenticado
     * Chamada a partir do DefinicoesFragment
     */
    fun alterarPin(token: String, pinAtual: String, novoPin: String) {
        viewModelScope.launch {
            // Cria o objeto de pedido com o pin atual e o novo pin
            val request = AlterarPinRequest(pinAtual, novoPin)
            // Delega a chamada de rede ao repositório
            val result = repository.alterarPin(token, request)
            // Publica o resultado para a UI mostrar uma mensagem de sucesso ou erro
            _pinChangeResult.postValue(result)
        }
    }

    /**
     * Pede ao repositório para terminar a sessão do utilizador (invalidar o token na API)
     * Chamada a partir do DefinicoesFragment
     */
    fun logout(token: String) {
        viewModelScope.launch {
            // Delega a chamada de rede ao repositório
            val result = repository.logout(token)
            // Publica o resultado para que a UI possa limpar a sessão e navegar para o login
            _logoutResult.postValue(result)
        }
    }
}
