package pt.ipt.dam2025.vetconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.model.CreateExameRequest
import pt.ipt.dam2025.vetconnect.model.Exame
import pt.ipt.dam2025.vetconnect.repository.HistoricoRepository

/**
 * ViewModel para gerir o histórico e a adição de exames de um animal
 */
class HistoricoViewModel(private val repository: HistoricoRepository) : ViewModel() {

    private val _operationStatus = MutableLiveData<Result<Unit>>()
    val operationStatus: LiveData<Result<Unit>> = _operationStatus

    /*
     * expõe a lista de exames para a UI
     * o .asLiveData() converte o Flow do repositório em LiveData
     */
    fun getExames(token: String, animalId: Int): LiveData<List<Exame>> {
        return repository.getExames(token, animalId).asLiveData()
    }

    /*
     * pede ao repositório para criar um novo exame
     */
    fun adicionarExame(
        token: String,
        animalId: Int,
        tipoExameId: Int,
        dataExame: String,
        clinicaId: Int,
        veterinarioId: Int,
        resultado: String?,
        observacoes: String?
    ) {
        viewModelScope.launch {
            val request = CreateExameRequest(
                animalId = animalId,
                tipoExameId = tipoExameId,
                dataExame = dataExame,
                clinicaId = clinicaId,
                veterinarioId = veterinarioId,
                resultado = resultado,
                observacoes = observacoes
            )
            val result = repository.createExame(token, request)
            _operationStatus.postValue(result.map { })
        }
    }

    /*
     * pede ao repositório para apagar um exame específico
     */
    fun deleteExame(token: String, animalId: Int, exameId: Long) {
        viewModelScope.launch {
            val result = repository.deleteExame(token, animalId, exameId)
            _operationStatus.postValue(result)
        }
    }
}
