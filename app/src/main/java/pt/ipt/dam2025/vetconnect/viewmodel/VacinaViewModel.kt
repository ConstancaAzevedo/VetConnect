package pt.ipt.dam2025.vetconnect.viewmodel

import androidx.lifecycle.LiveData // Importa a classe LiveData para dados observáveis
import androidx.lifecycle.MutableLiveData // Importa a versão mutável do LiveData
import androidx.lifecycle.ViewModel // Importa a classe base ViewModel
import androidx.lifecycle.asLiveData // Importa a extensão para converter Flow em LiveData
import androidx.lifecycle.viewModelScope // Importa o escopo de coroutines para o ViewModel
import kotlinx.coroutines.launch // Importa a função para iniciar uma coroutine
import pt.ipt.dam2025.vetconnect.model.* 
import pt.ipt.dam2025.vetconnect.repository.VacinaRepository

/**
 * ViewModel para gerir a lógica relacionada com as vacinas
 * Atua como intermediário entre a UI e o VacinaRepository
 */
class VacinaViewModel(private val repository: VacinaRepository) : ViewModel() {

    // LiveData privado e mutável para o estado das operações (agendar cancelar etc)
    private val _operationStatus = MutableLiveData<Result<Unit>>()
    // LiveData público e imutável exposto à UI
    val operationStatus: LiveData<Result<Unit>> = _operationStatus

    // LiveData para as listas dos spinners que vêm diretamente do repositório
    // Converte o Flow da base de dados em LiveData para a UI observar
    val tiposVacina: LiveData<List<TipoVacina>> = repository.getTiposVacina().asLiveData()
    val clinicas: LiveData<List<Clinica>> = repository.getClinicas().asLiveData()

    // LiveData privado para a lista de veterinários que depende da clínica selecionada
    private val _veterinarios = MutableLiveData<List<Veterinario>>()
    // LiveData público exposto à UI
    val veterinarios: LiveData<List<Veterinario>> = _veterinarios

    /**
     * Obtém a lista de vacinas de um animal específico
     * Retorna um LiveData que a UI pode observar
     */
    fun getVacinas(token: String, animalId: Int): LiveData<List<Vacina>> {
        return repository.getVacinas(token, animalId).asLiveData()
    }

    /**
     * Pede ao repositório para carregar os veterinários de uma clínica específica
     * e atualiza o LiveData _veterinarios
     */
    fun carregaVeterinarios(clinicaId: Int) {
        // Inicia uma coroutine no contexto do ViewModel
        viewModelScope.launch {
            // Coleta os dados do Flow retornado pelo repositório
            repository.getVeterinariosPorClinica(clinicaId).collect { veterinarios ->
                // Atualiza o LiveData com a nova lista de veterinários
                _veterinarios.postValue(veterinarios)
            }
        }
    }

    /**
     * Pede ao repositório para agendar uma nova vacina
     * O resultado da operação é publicado no _operationStatus
     */
    fun agendarVacina(token: String, request: AgendarVacinaRequest) {
        viewModelScope.launch {
            val result = repository.agendarVacina(token, request)
            _operationStatus.postValue(result)
        }
    }

    /**
     * Pede ao repositório para marcar uma vacina como realizada
     */
    fun marcarVacinaRealizada(token: String, vacinaId: Int, dataAplicacao: String?, lote: String?, veterinario: String?, observacoes: String?) {
        viewModelScope.launch {
            val request = MarcarVacinaRealizadaRequest(dataAplicacao, lote, veterinario, observacoes)
            val result = repository.marcarVacinaRealizada(token, vacinaId, request)
            _operationStatus.postValue(result)
        }
    }

    /**
     * Pede ao repositório para cancelar uma vacina existente
     */
    fun cancelarVacina(token: String, vacinaId: Int) {
        viewModelScope.launch {
            val result = repository.cancelarVacina(token, vacinaId)
            _operationStatus.postValue(result)
        }
    }

    /**
     * Pede ao repositório para atualizar os dados de uma vacina
     */
    fun updateVacina(token: String, vacinaId: Int, request: UpdateVacinaRequest) {
        viewModelScope.launch {
            val result = repository.updateVacina(token, vacinaId, request)
            _operationStatus.postValue(result)
        }
    }
}
