package pt.ipt.dam2025.vetconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.model.*
import pt.ipt.dam2025.vetconnect.repository.ConsultaRepository

/**
 * ViewModel para gerir os dados e a lógica de negócio relacionados com as Consultas
 */
class ConsultaViewModel(private val repository: ConsultaRepository) : ViewModel() {

    // LiveData para expor o resultado de operações
    private val _operationStatus = MutableLiveData<Result<Any>>()
    val operationStatus: LiveData<Result<Any>> = _operationStatus

    // --- Funções para obter as listas para os spinners ---

    fun getAnimaisDoTutor(token: String, userId: Int): LiveData<List<AnimalResponse>> {
        return repository.getAnimaisDoTutor(token, userId).asLiveData()
    }

    val clinicas: LiveData<List<Clinica>> = repository.getClinicas().asLiveData()

    private val _veterinarios = MutableLiveData<List<Veterinario>>()
    val veterinarios: LiveData<List<Veterinario>> = _veterinarios

    fun carregaVeterinarios(clinicaId: Int) {
        viewModelScope.launch {
            repository.getVeterinariosPorClinica(clinicaId).collect {
                _veterinarios.postValue(it)
            }
        }
    }

    fun marcarConsulta(token: String, novaConsulta: NovaConsulta) {
        viewModelScope.launch {
            val result = repository.marcarConsulta(token, novaConsulta)
            _operationStatus.postValue(result)
        }
    }

    // funções para gerir consultas existentes

    fun getConsultas(token: String, userId: Int): LiveData<List<Consulta>> {
        return repository.getConsultas(token, userId).asLiveData()
    }

    fun cancelarConsulta(token: String, consultaId: Int) {
        viewModelScope.launch {
            val result = repository.cancelarConsulta(token, consultaId)
            _operationStatus.postValue(result)
        }
    }

    fun updateConsulta(token: String, id: Int, request: UpdateConsultaRequest) {
        viewModelScope.launch {
            val result = repository.updateConsulta(token, id, request)
            _operationStatus.postValue(result)
        }
    }
}