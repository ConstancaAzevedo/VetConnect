package pt.ipt.dam2025.vetconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.model.AlterarPinRequest
import pt.ipt.dam2025.vetconnect.model.CreatePinRequest
import pt.ipt.dam2025.vetconnect.model.CreatePinResponse
import pt.ipt.dam2025.vetconnect.model.LoginResponse
import pt.ipt.dam2025.vetconnect.model.NovoUsuario
import pt.ipt.dam2025.vetconnect.model.RegistrationResponse
import pt.ipt.dam2025.vetconnect.model.UpdateUserRequest
import pt.ipt.dam2025.vetconnect.model.Usuario
import pt.ipt.dam2025.vetconnect.repository.UsuarioRepository

/**
 * ViewModel para gerir os dados e a lógica de negócio do Utilizador
 */
class UsuarioViewModel(private val repository: UsuarioRepository) : ViewModel() {

    // LiveData para o resultado do registo
    private val _registrationResult = MutableLiveData<Result<RegistrationResponse>>()
    val registrationResult: LiveData<Result<RegistrationResponse>> = _registrationResult

    // LiveData para o resultado da criação de PIN
    private val _createPinResult = MutableLiveData<Result<CreatePinResponse>>()
    val createPinResult: LiveData<Result<CreatePinResponse>> = _createPinResult

    // LiveData para o resultado do login
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    // LiveData para o resultado da atualização de um utilizador
    private val _updateResult = MutableLiveData<Result<Unit>>()
    val updateResult: LiveData<Result<Unit>> = _updateResult

    // LiveData para o resultado da alteração de PIN
    private val _pinChangeResult = MutableLiveData<Result<Unit>>()
    val pinChangeResult: LiveData<Result<Unit>> = _pinChangeResult

    // LiveData para o resultado do logout
    private val _logoutResult = MutableLiveData<Result<Unit>>()
    val logoutResult: LiveData<Result<Unit>> = _logoutResult

    /**
     * Pede ao repositório para criar um novo utilizador
     */
    fun adicionarUsuario(nome: String, email: String, telemovel: String) {
        viewModelScope.launch {
            val novoUsuario = NovoUsuario(nome, email, telemovel, "tutor")
            val result = repository.criarUsuario(novoUsuario)
            _registrationResult.postValue(result)
        }
    }

    /**
     * Pede ao repositório para criar o PIN do utilizador
     */
    fun criarPin(email: String, pin: String) {
        viewModelScope.launch {
            val request = CreatePinRequest(email, pin)
            val result = repository.criarPin(request)
            _createPinResult.postValue(result)
        }
    }

    /**
     * Pede ao repositório para autenticar um utilizador
     */
    fun login(email: String, pin: String) {
        viewModelScope.launch {
            val result = repository.login(email, pin)
            _loginResult.postValue(result)
        }
    }

    /**
     * Obtém os dados de um utilizador específico a partir do repositório
     */
    fun getUser(userId: Int): LiveData<Usuario?> {
        return repository.getUser(userId).asLiveData()
    }

    /**
     * Pede ao repositório para atualizar os dados de um utilizador
     */
    fun updateUser(token: String, userId: Int, request: UpdateUserRequest) {
        viewModelScope.launch {
            val result = repository.updateUser(token, userId, request)
            _updateResult.postValue(result)
        }
    }

    /**
     * Pede ao repositório para forçar a atualização dos dados de um utilizador a partir da API
     */
    fun refreshUser(token: String, userId: Int) {
        viewModelScope.launch {
            repository.refreshUser(token, userId)
        }
    }

    /**
     * Pede ao repositório para alterar o PIN do utilizador
     */
    fun alterarPin(token: String, pinAtual: String, novoPin: String) {
        viewModelScope.launch {
            val request = AlterarPinRequest(pinAtual, novoPin)
            val result = repository.alterarPin(token, request)
            _pinChangeResult.postValue(result)
        }
    }

    /**
     * Pede ao repositório para terminar a sessão do utilizador
     */
    fun logout(token: String) {
        viewModelScope.launch {
            val result = repository.logout(token)
            _logoutResult.postValue(result)
        }
    }
}
