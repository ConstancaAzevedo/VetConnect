package pt.ipt.dam2025.vetconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.model.*
import pt.ipt.dam2025.vetconnect.repository.VacinaRepository

class VacinaViewModel(private val repository: VacinaRepository) : ViewModel() {

    private val _operationStatus = MutableLiveData<Result<Unit>>()
    val operationStatus: LiveData<Result<Unit>> = _operationStatus

    // LiveData para as listas dos spinners
    val tiposVacina: LiveData<List<TipoVacina>> = repository.getTiposVacina().asLiveData()
    val clinicas: LiveData<List<Clinica>> = repository.getClinicas().asLiveData()

    private val _veterinarios = MutableLiveData<List<Veterinario>>()
    val veterinarios: LiveData<List<Veterinario>> = _veterinarios

    fun getVacinas(token: String, animalId: Int): LiveData<List<Vacina>> {
        return repository.getVacinas(token, animalId).asLiveData()
    }

    fun carregaVeterinarios(clinicaId: Int) {
        viewModelScope.launch {
            repository.getVeterinariosPorClinica(clinicaId).collect {
                _veterinarios.postValue(it)
            }
        }
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