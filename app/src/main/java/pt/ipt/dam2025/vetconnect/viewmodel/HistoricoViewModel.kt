package pt.ipt.dam2025.vetconnect.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.model.CreateExameRequest
import pt.ipt.dam2025.vetconnect.model.Exame
import pt.ipt.dam2025.vetconnect.model.UpdateExameRequest
import pt.ipt.dam2025.vetconnect.repository.HistoricoRepository

/**
 * ViewModel para gerir o histórico e a adição de exames de um animal
 */
class HistoricoViewModel(private val repository: HistoricoRepository) : ViewModel() {

    private val _operationStatus = MutableLiveData<Result<Unit>>()
    val operationStatus: LiveData<Result<Unit>> = _operationStatus

    /*
     * expõe a lista de exames para a UI
     * o .asLiveData() converte o Flow do repositório em LiveData
     */
    fun getExames(token: String, animalId: Int): LiveData<List<Exame>> {
        return repository.getExames(token, animalId).asLiveData()
    }

    /*
     * criação de um novo exame e o upload da sua foto (se existir)
     */
    fun adicionarExameEFoto(
        token: String,
        animalId: Int,
        tipoExameId: Int,
        dataExame: String,
        clinicaId: Int,
        veterinarioId: Int,
        resultado: String?,
        observacoes: String?,
        imageUri: Uri?,
        context: Context
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
            // Primeiro, cria o exame
            val result = repository.createExame(token, request)
            result.onSuccess {
                // Se o exame foi criado e há uma imagem, faz o upload
                if (imageUri != null) {
                    val exameId = it.exame.id
                    // Descomentado para ativar o upload da foto
                    val uploadResult = repository.addFotoToExame(token, exameId, animalId, imageUri, context)
                    _operationStatus.postValue(uploadResult.map { })
                } else {
                    // Se não há imagem, a operação foi um sucesso
                    _operationStatus.postValue(Result.success(Unit))
                }
            }.onFailure {
                // Se a criação do exame falhar, a operação falha
                _operationStatus.postValue(Result.failure(it))
            }
        }
    }

    /*
     * Orquestra a atualização de um exame e, opcionalmente, o upload de uma nova foto.
     */
    fun atualizarExameEFoto(
        token: String,
        exameId: Int,
        animalId: Int,
        tipoExameId: Int?,
        dataExame: String?,
        clinicaId: Int?,
        veterinarioId: Int?,
        resultado: String?,
        observacoes: String?,
        novaImagemUri: Uri?,
        context: Context
    ) {
        viewModelScope.launch {
            val updateRequest = UpdateExameRequest(
                tipoExameId = tipoExameId,
                dataExame = dataExame,
                clinicaId = clinicaId,
                veterinarioId = veterinarioId,
                resultado = resultado,
                observacoes = observacoes
            )
            // Primeiro, atualiza os dados do exame
            val result = repository.updateExame(token, exameId, updateRequest)
            result.onSuccess {
                // Se a atualização dos dados foi bem-sucedida e há uma nova imagem, faz o upload
                if (novaImagemUri != null) {
                    val uploadResult = repository.addFotoToExame(token, exameId, animalId, novaImagemUri, context)
                    _operationStatus.postValue(uploadResult.map { })
                } else {
                    // Se não há nova imagem, a operação foi um sucesso
                    _operationStatus.postValue(Result.success(Unit))
                }
            }.onFailure {
                // Se a atualização falhar, a operação falha
                _operationStatus.postValue(Result.failure(it))
            }
        }
    }

    /*
     * pede ao repositório para apagar um exame específico
     */
    fun deleteExame(token: String, animalId: Int, exameId: Long) {
        viewModelScope.launch {
            val result = repository.deleteExame(token, animalId, exameId)
            _operationStatus.postValue(result)
        }
    }
}