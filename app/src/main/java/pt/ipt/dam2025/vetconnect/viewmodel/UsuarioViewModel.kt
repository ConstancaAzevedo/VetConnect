package pt.ipt.dam2025.vetconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.model.UpdateUserRequest
import pt.ipt.dam2025.vetconnect.model.Usuario
import pt.ipt.dam2025.vetconnect.repository.UsuarioRepository

/**
 * ViewModel para gerir os dados e a lógica de negócio do Perfil do Utilizador
 */
class UsuarioViewModel(private val repository: UsuarioRepository) : ViewModel() {

    // LiveData para o resultado da atualização de um utilizador
    private val _updateResult = MutableLiveData<Result<Unit>>()
    val updateResult: LiveData<Result<Unit>> = _updateResult

    /**
     * obtém os dados de um utilizador específico a partir do repositório
     * para serem exibidos no ecrã de perfil
     */
    fun getUser(userId: Int): LiveData<Usuario?> {
        return repository.getUser(userId).asLiveData()
    }

    /**
     * pede ao repositório para atualizar os dados de um utilizador
     */
    fun updateUser(token: String, userId: Int, request: UpdateUserRequest) {
        viewModelScope.launch {
            val result = repository.updateUser(token, userId, request)
            _updateResult.postValue(result)
        }
    }

    /**
     * pede ao repositório para forçar a atualização dos dados de um utilizador a partir da API
     */
    fun refreshUser(token: String, userId: Int) {
        viewModelScope.launch {
            repository.refreshUser(token, userId)
        }
    }
}
