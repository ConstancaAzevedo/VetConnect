package pt.ipt.dam2025.vetconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.model.CreateExameRequest
import pt.ipt.dam2025.vetconnect.repository.HistoricoRepository

/**
 * ViewModel para gerir a adição de um novo exame
 */
class AdicionarExameViewModel(private val repository: HistoricoRepository) : ViewModel() {

    // LiveData para comunicar o resultado da operação à UI
    private val _exameAdicionado = MutableLiveData<Result<Unit>>()
    val exameAdicionado: LiveData<Result<Unit>> = _exameAdicionado

    /**
     * Cria um novo exame enviando os dados para o repositório
     */
    fun adicionarExame(
        token: String,
        animalId: Int,
        tipoExameId: Int,
        dataExame: String,
        clinicaId: Int,
        veterinarioId: Int,
        resultado: String?,
        observacoes: String?
    ) {
        viewModelScope.launch {
            val request = CreateExameRequest(
                animalId = animalId,
                tipoExameId = tipoExameId,
                dataExame = dataExame,
                clinicaId = clinicaId,
                veterinarioId = veterinarioId,
                resultado = resultado,
                observacoes = observacoes
            )
            // Chama o repositório e atualiza o LiveData com o resultado
            val result = repository.createExame(token, request)
            _exameAdicionado.postValue(result.map { })
        }
    }
}
