package pt.ipt.dam2025.trabalho.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.data.HistoricoDao
import pt.ipt.dam2025.trabalho.model.HistoricoItem

// ViewModel: sobrevive a mudanças de configuração e gere a lógica de negócio.
class HistoricoViewModel(private val dao: HistoricoDao) : ViewModel() {

    // expõe a lista de itens para a Activity poder observar
    val allHistoricoItems: Flow<List<HistoricoItem>> = dao.getAll()

    // as operações na base de dados devem ser feitas numa thread de segundo plano para não congelar a UI.
    // o viewModelScope faz isto automaticamente.
    fun insert(item: HistoricoItem) = viewModelScope.launch {
        dao.insert(item)
    }

    fun delete(item: HistoricoItem) = viewModelScope.launch {
        dao.delete(item)
    }
}
