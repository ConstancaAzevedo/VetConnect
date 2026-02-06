package pt.ipt.dam2025.vetconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.model.UpdateVacinaRequest
import pt.ipt.dam2025.vetconnect.model.Vacina

/**
 * Classe que define o ViewModel para Vacina
 */
class VacinaViewModel : ViewModel() {

    private val _vacinas = MutableLiveData<List<Vacina>>()
    val vacinas: LiveData<List<Vacina>> = _vacinas

    private val _operationStatus = MutableLiveData<Boolean>()
    val operationStatus: LiveData<Boolean> = _operationStatus

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchVacinasAgendadas(token: String, animalId: Int) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getVacinasAgendadas("Bearer $token", animalId)
                if (response.isSuccessful) {
                    _vacinas.postValue(response.body()?.vacinas ?: emptyList())
                    _errorMessage.postValue(null)
                } else {
                    _vacinas.postValue(emptyList())
                    _errorMessage.postValue("Falha ao carregar vacinas: ${response.message()}")
                }
            } catch (e: Exception) {
                _vacinas.postValue(emptyList())
                _errorMessage.postValue("Ocorreu um erro: ${e.message}")
            }
        }
    }

    fun cancelarVacina(token: String, vacinaId: Int) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.cancelarVacina("Bearer $token", vacinaId)
                if (response.isSuccessful) {
                    _operationStatus.postValue(true)
                    _errorMessage.postValue(null)
                } else {
                    _operationStatus.postValue(false)
                    _errorMessage.postValue("Falha ao apagar vacina: ${response.message()}")
                }
            } catch (e: Exception) {
                _operationStatus.postValue(false)
                _errorMessage.postValue("Ocorreu um erro: ${e.message}")
            }
        }
    }

    fun updateVacina(token: String, vacinaId: Int, request: UpdateVacinaRequest) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.updateVacina("Bearer $token", vacinaId, request)
                if (response.isSuccessful) {
                    _operationStatus.postValue(true)
                    _errorMessage.postValue(null)
                } else {
                    _operationStatus.postValue(false)
                    _errorMessage.postValue("Falha ao atualizar vacina: ${response.message()}")
                }
            } catch (e: Exception) {
                _operationStatus.postValue(false)
                _errorMessage.postValue("Ocorreu um erro: ${e.message}")
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}