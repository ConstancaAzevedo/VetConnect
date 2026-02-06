package pt.ipt.dam2025.vetconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.model.Consulta
import pt.ipt.dam2025.vetconnect.repository.ConsultaRepository

/**
 * ViewModel para gerir os dados e a lógica de negócio relacionados com as Consultas
 */
class ConsultaViewModel(private val repository: ConsultaRepository) : ViewModel() {

    // LiveData para expor o resultado da operação de cancelamento
    private val _operationStatus = MutableLiveData<Result<Unit>>()
    val operationStatus: LiveData<Result<Unit>> = _operationStatus

    /**
     * Obtém a lista de consultas de um utilizador a partir do repositório
     * O .asLiveData() converte o Flow<List<Consulta>> em LiveData<List<Consulta>>
     */
    fun getConsultas(token: String, userId: Int): LiveData<List<Consulta>> {
        return repository.getConsultas(token, userId).asLiveData()
    }

    /**
     * Pede ao repositório para cancelar uma consulta específica
     */
    fun cancelarConsulta(token: String, consultaId: Int) {
        viewModelScope.launch {
            val result = repository.cancelarConsulta(token, consultaId)
            _operationStatus.postValue(result)
        }
    }
}
