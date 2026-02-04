package pt.ipt.dam2025.vetconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.model.Vacina
import pt.ipt.dam2025.vetconnect.model.VacinasAgendadasResponse

// ViewModel para Vacina
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
                    // a resposta contém o objeto VacinasAgendadasResponse, que tem a lista de vacinas
                    _vacinas.postValue(response.body()?.vacinas ?: emptyList())
                    _errorMessage.postValue(null) // limpa erros anteriores em caso de sucesso
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

     // limpa a mensagem de erro
     // deve ser chamado depois de o erro ser tratado na UI
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}