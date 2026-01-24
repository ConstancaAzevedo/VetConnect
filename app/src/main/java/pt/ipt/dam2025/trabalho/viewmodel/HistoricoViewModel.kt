package pt.ipt.dam2025.trabalho.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.model.Exame
import pt.ipt.dam2025.trabalho.model.Receita
import pt.ipt.dam2025.trabalho.model.Vacina
import pt.ipt.dam2025.trabalho.repository.HistoricoRepository

// ViewModel para histórico
class HistoricoViewModel(private val repository: HistoricoRepository) : ViewModel() {

    val allReceitas: LiveData<List<Receita>> = repository.todasReceitas.asLiveData()
    val allVacinas: LiveData<List<Vacina>> = repository.todasVacinas.asLiveData()
    val allExames: LiveData<List<Exame>> = repository.todosExames.asLiveData()

    private val _operationStatus = MutableLiveData<Result<Unit>>()
    val operationStatus: LiveData<Result<Unit>> = _operationStatus

    fun refreshHistorico(token: String, animalId: Int) {
        viewModelScope.launch {
            try {
                repository.refreshHistorico(token, animalId)
            } catch (e: Exception) {
                // O erro já é tratado e logado no repositório.
            }
        }
    }

    fun deleteReceita(token: String, receita: Receita) {
        viewModelScope.launch {
            val result = repository.deleteDocument(token, receita.animalId, "receita", receita.id.toLong())
            _operationStatus.postValue(result.map { })
        }
    }

    fun deleteVacina(token: String, vacina: Vacina) {
        viewModelScope.launch {
            val result = repository.deleteDocument(token, vacina.animalId, "vacina", vacina.id.toLong())
            _operationStatus.postValue(result.map { })
        }
    }

    fun deleteExame(token: String, exame: Exame) {
        viewModelScope.launch {
            val result = repository.deleteDocument(token, exame.animalId, "exame", exame.id.toLong())
            _operationStatus.postValue(result.map { })
        }
    }
}