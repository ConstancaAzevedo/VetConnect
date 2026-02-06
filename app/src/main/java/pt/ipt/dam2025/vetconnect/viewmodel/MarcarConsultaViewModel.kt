package pt.ipt.dam2025.vetconnect.viewmodel

import androidx.lifecycle.LiveData
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
 * ViewModel para gerir a marcação de uma nova consulta
 */
class MarcarConsultaViewModel(private val repository: ConsultaRepository) : ViewModel() {

    // Expõe a lista de clínicas a partir do repositório
    val clinicas: LiveData<List<Clinica>> = repository.getClinicas().asLiveData()

    /**
     * Obtém a lista de veterinários para uma clínica específica
     */
    fun getVeterinariosPorClinica(clinicaId: Int): LiveData<List<Veterinario>> {
        return repository.getVeterinariosPorClinica(clinicaId).asLiveData()
    }

    /**
     * Pede ao repositório para marcar uma nova consulta
     * Retorna o resultado da operação para a UI
     */
    fun marcarConsulta(token: String, novaConsulta: NovaConsulta): LiveData<Result<Consulta>> {
        val result = androidx.lifecycle.MutableLiveData<Result<Consulta>>()
        viewModelScope.launch {
            val response = repository.marcarConsulta(token, novaConsulta)
            result.postValue(response)
        }
        return result
    }
}
