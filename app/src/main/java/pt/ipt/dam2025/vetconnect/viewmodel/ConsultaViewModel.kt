package pt.ipt.dam2025.vetconnect.viewmodel

import androidx.lifecycle.LiveData // Importa a classe LiveData para dados observáveis
import androidx.lifecycle.MutableLiveData // Importa a versão mutável do LiveData
import androidx.lifecycle.ViewModel // Importa a classe base ViewModel
import androidx.lifecycle.asLiveData // Importa a extensão para converter Flow em LiveData
import androidx.lifecycle.viewModelScope // Importa o contexto de coroutines para o ViewModel
import kotlinx.coroutines.launch // Importa a função para iniciar uma coroutine
import pt.ipt.dam2025.vetconnect.model.*
import pt.ipt.dam2025.vetconnect.repository.ConsultaRepository

/**
 * ViewModel para gerir os dados e a lógica de negócio relacionados com as Consultas
 * Atua como intermediário entre a UI e o ConsultaRepository
 */
class ConsultaViewModel(
    private val consultaRepository: ConsultaRepository
) : ViewModel() {

    // LiveData privado e mutável para o resultado de operações como marcar ou cancelar
    private val _operationStatus = MutableLiveData<Result<Any>>()
    // LiveData público e imutável exposto à UI para observar o estado das operações
    val operationStatus: LiveData<Result<Any>> = _operationStatus

    // LiveData para a lista de todas as clínicas
    val clinicas: LiveData<List<Clinica>> = consultaRepository.getClinicas().asLiveData()

    // LiveData para a lista de todos os veterinários
    val todosVeterinarios: LiveData<List<Veterinario>> = consultaRepository.getVeterinarios().asLiveData()

    // LiveData privado e mutável para a lista de veterinários de uma clínica específica
    private val _veterinarios = MutableLiveData<List<Veterinario>>()
    // LiveData público exposto à UI
    val veterinarios: LiveData<List<Veterinario>> = _veterinarios

    /**
     * Pede ao repositório para carregar os veterinários de uma clínica específica
     * e atualiza o LiveData _veterinarios
     */
    fun carregaVeterinarios(clinicaId: Int) {
        // Inicia uma coroutine no contexto do ViewModel
        viewModelScope.launch {
            // Coleta os dados do Flow retornado pelo repositório
            consultaRepository.getVeterinariosPorClinica(clinicaId).collect {
                // Atualiza o LiveData com a nova lista de veterinários
                _veterinarios.postValue(it)
            }
        }
    }

    /**
     * Pede ao repositório para marcar uma nova consulta
     * O resultado é publicado no _operationStatus
     */
    fun marcarConsulta(token: String, novaConsulta: NovaConsulta) {
        viewModelScope.launch {
            val result = consultaRepository.marcarConsulta(token, novaConsulta)
            _operationStatus.postValue(result)
        }
    }

    /**
     * Obtém a lista de todas as consultas de um utilizador
     * Retorna um LiveData que a UI pode observar
     */
    fun getConsultas(token: String, userId: Int): LiveData<List<Consulta>> {
        return consultaRepository.getConsultas(token, userId).asLiveData()
    }

    /**
     * Pede ao repositório para cancelar uma consulta
     * O resultado é publicado no _operationStatus
     */
    fun cancelarConsulta(token: String, consultaId: Int) {
        viewModelScope.launch {
            val result = consultaRepository.cancelarConsulta(token, consultaId)
            _operationStatus.postValue(result)
        }
    }

    /**
     * Pede ao repositório para atualizar os dados de uma consulta
     * O resultado é publicado no _operationStatus
     */
    fun updateConsulta(token: String, id: Int, request: UpdateConsultaRequest) {
        viewModelScope.launch {
            val result = consultaRepository.updateConsulta(token, id, request)
            _operationStatus.postValue(result)
        }
    }
}
