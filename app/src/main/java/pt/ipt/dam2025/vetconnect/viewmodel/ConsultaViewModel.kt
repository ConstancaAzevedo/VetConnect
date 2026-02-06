package pt.ipt.dam2025.vetconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.model.Clinica
import pt.ipt.dam2025.vetconnect.model.Consulta
import pt.ipt.dam2025.vetconnect.model.NovaConsulta
import pt.ipt.dam2025.vetconnect.model.Veterinario
import pt.ipt.dam2025.vetconnect.repository.ConsultaRepository

/**
 * ViewModel para gerir os dados e a lógica de negócio relacionados com as Consultas
 */
class ConsultaViewModel(private val repository: ConsultaRepository) : ViewModel() {

    // LiveData para expor o resultado de operações (cancelar, marcar)
    private val _operationStatus = MutableLiveData<Result<Any>>()
    val operationStatus: LiveData<Result<Any>> = _operationStatus

    // --- Funções para Marcar Consulta ---

    val clinicas: LiveData<List<Clinica>> = repository.getClinicas().asLiveData()

    fun getVeterinariosPorClinica(clinicaId: Int): LiveData<List<Veterinario>> {
        return repository.getVeterinariosPorClinica(clinicaId).asLiveData()
    }

    fun marcarConsulta(token: String, novaConsulta: NovaConsulta) {
        viewModelScope.launch {
            val result = repository.marcarConsulta(token, novaConsulta)
            _operationStatus.postValue(result)
        }
    }

    // --- Funções para Gerir Consultas Existentes ---

    fun getConsultas(token: String, userId: Int): LiveData<List<Consulta>> {
        return repository.getConsultas(token, userId).asLiveData()
    }

    fun cancelarConsulta(token: String, consultaId: Int) {
        viewModelScope.launch {
            val result = repository.cancelarConsulta(token, consultaId)
            _operationStatus.postValue(result)
        }
    }
}
