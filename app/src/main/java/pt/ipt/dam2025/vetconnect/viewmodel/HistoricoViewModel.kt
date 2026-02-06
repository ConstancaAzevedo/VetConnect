package pt.ipt.dam2025.vetconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.model.Exame
import pt.ipt.dam2025.vetconnect.repository.HistoricoRepository

/**
 * ViewModel para gerir o histórico de exames de um animal
 */
class HistoricoViewModel(private val repository: HistoricoRepository) : ViewModel() {

    // expõe a lista de exames para a UI
    // O .asLiveData() converte o Flow do repositório em LiveData
    fun getExames(token: String, animalId: Int): LiveData<List<Exame>> {
        return repository.getExames(token, animalId).asLiveData()
    }

    /*
     * pede ao repositório para apagar um exame específico
     */
    fun deleteExame(token: String, animalId: Int, exameId: Long) {
        viewModelScope.launch {
            repository.deleteExame(token, animalId, exameId)
        }
    }
}
