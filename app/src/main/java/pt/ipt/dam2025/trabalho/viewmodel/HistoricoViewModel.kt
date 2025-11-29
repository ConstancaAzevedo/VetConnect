package pt.ipt.dam2025.trabalho.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.model.HistoricoItem
import pt.ipt.dam2025.trabalho.repository.HistoricoRepository

/**
 * ViewModel para o ecrã de Histórico.
 * Gere a lógica de negócio e a comunicação com o repositório.
 */
class HistoricoViewModel(private val repository: HistoricoRepository) : ViewModel() {

    // Expõe a lista de itens do repositório. A UI observa este Flow para atualizações automáticas.
    val allHistoricoItems: Flow<List<HistoricoItem>> = repository.allHistoricoItems

    init {
        // Sempre que o ViewModel é criado, vai buscar os dados mais recentes à API.
        refreshHistorico()
    }

    /**
     * Pede ao repositório para ir buscar os dados mais recentes à API e atualizar a base de dados local.
     */
    fun refreshHistorico() = viewModelScope.launch {
        repository.refreshHistorico()
    }

    /**
     * Pede ao repositório para inserir um novo item na base de dados.
     */
    fun insert(item: HistoricoItem) = viewModelScope.launch {
        repository.insert(item)
    }

    fun delete(item: HistoricoItem) = viewModelScope.launch {
        // O mesmo para o delete.
    }
}
