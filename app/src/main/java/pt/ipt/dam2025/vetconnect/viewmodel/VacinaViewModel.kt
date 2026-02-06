package pt.ipt.dam2025.vetconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.model.AgendarVacinaRequest
import pt.ipt.dam2025.vetconnect.model.TipoVacina
import pt.ipt.dam2025.vetconnect.model.UpdateVacinaRequest
import pt.ipt.dam2025.vetconnect.model.Vacina
import pt.ipt.dam2025.vetconnect.repository.VacinaRepository

class VacinaViewModel(private val repository: VacinaRepository) : ViewModel() {

    private val _operationStatus = MutableLiveData<Result<Unit>>()
    val operationStatus: LiveData<Result<Unit>> = _operationStatus

    fun getVacinas(token: String, animalId: Int): LiveData<List<Vacina>> {
        return repository.getVacinas(token, animalId).asLiveData()
    }

    fun getTiposVacina(): LiveData<List<TipoVacina>> {
        return repository.getTiposVacina().asLiveData()
    }

    fun agendarVacina(token: String, request: AgendarVacinaRequest) {
        viewModelScope.launch {
            val result = repository.agendarVacina(token, request)
            _operationStatus.postValue(result)
        }
    }

    fun cancelarVacina(token: String, vacinaId: Int) {
        viewModelScope.launch {
            val result = repository.cancelarVacina(token, vacinaId)
            _operationStatus.postValue(result)
        }
    }

    fun updateVacina(token: String, vacinaId: Int, request: UpdateVacinaRequest) {
        viewModelScope.launch {
            val result = repository.updateVacina(token, vacinaId, request)
            _operationStatus.postValue(result)
        }
    }
}
