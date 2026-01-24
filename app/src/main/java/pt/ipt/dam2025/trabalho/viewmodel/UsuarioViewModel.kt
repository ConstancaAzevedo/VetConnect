package pt.ipt.dam2025.trabalho.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.model.NovoUsuario
import pt.ipt.dam2025.trabalho.model.RegistrationResponse
import pt.ipt.dam2025.trabalho.model.UpdateUserRequest
import pt.ipt.dam2025.trabalho.model.Usuario
import pt.ipt.dam2025.trabalho.repository.UsuarioRepository

// Classe de fábrica para o ViewModel
class UsuarioViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _usuarios = MutableLiveData<List<Usuario>>()
    val usuarios: LiveData<List<Usuario>> = _usuarios

    private val _registrationResult = MutableLiveData<Result<RegistrationResponse>>()
    val registrationResult: LiveData<Result<RegistrationResponse>> = _registrationResult

    private val _updateResult = MutableLiveData<Result<Unit>>()
    val updateResult: LiveData<Result<Unit>> = _updateResult

    private val _deleteResult = MutableLiveData<Result<Unit>>()
    val deleteResult: LiveData<Result<Unit>> = _deleteResult

    private val _refreshResult = MutableLiveData<Result<Usuario>>()
    val refreshResult: LiveData<Result<Usuario>> = _refreshResult

    fun carregarUsuarios(token: String) {
        viewModelScope.launch {
            try {
                val listaUsuarios = repository.getUsuarios(token)
                _usuarios.postValue(listaUsuarios)
            } catch (e: Exception) {
                // Idealmente, tratar o erro com um LiveData de erro separado
            }
        }
    }

    fun adicionarUsuario(usuario: NovoUsuario) {
        viewModelScope.launch {
            val result = repository.criarUsuario(usuario)
            _registrationResult.postValue(result)
            // A atualização da lista de utilizadores deve ser observada pela UI e despoletada se o resultado for sucesso
        }
    }

    fun apagarUsuario(token: String, id: Int) {
        viewModelScope.launch {
            val result = repository.deletarUsuario(token, id)
            _deleteResult.postValue(result)
        }
    }

    fun getUser(userId: Int): LiveData<Usuario?> {
        return repository.getUser(userId).asLiveData()
    }

    fun updateUser(token: String, userId: Int, request: UpdateUserRequest) {
        viewModelScope.launch {
            val result = repository.updateUser(token, userId, request)
            result.onSuccess {
                _updateResult.postValue(Result.success(Unit))
            }.onFailure {
                _updateResult.postValue(Result.failure(it))
            }
        }
    }

    fun refreshUser(token: String, userId: Int) {
        viewModelScope.launch {
            val refreshResult = repository.refreshUser(token, userId)
            refreshResult.onSuccess {
                val user = repository.getUser(userId).firstOrNull()
                if (user != null) {
                    _refreshResult.postValue(Result.success(user))
                } else {
                    _refreshResult.postValue(Result.failure(Exception("Utilizador não encontrado após a atualização")))
                }
            }.onFailure {
                _refreshResult.postValue(Result.failure(it))
            }
        }
    }
}
