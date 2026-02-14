package pt.ipt.dam2025.vetconnect.viewmodel

import android.content.Context // Importa a classe Context
import android.net.Uri // Importa a classe Uri para lidar com caminhos de ficheiros
import androidx.lifecycle.LiveData // Importa a classe LiveData para dados observáveis
import androidx.lifecycle.MutableLiveData // Importa a versão mutável do LiveData
import androidx.lifecycle.ViewModel // Importa a classe base ViewModel
import androidx.lifecycle.asLiveData // Importa a extensão para converter Flow em LiveData
import androidx.lifecycle.viewModelScope // Importa o contexto de coroutines para o ViewModel
import kotlinx.coroutines.launch // Importa a função para iniciar uma coroutine
import pt.ipt.dam2025.vetconnect.model.*
import pt.ipt.dam2025.vetconnect.repository.HistoricoRepository

/**
 * ViewModel para gerir o histórico e a adição de exames de um animal
 */
class HistoricoViewModel(private val repository: HistoricoRepository) : ViewModel() {

    // LiveData privado e mutável para o resultado de operações
    private val _operationStatus = MutableLiveData<Result<Unit>>()
    // LiveData público e imutável exposto à UI
    val operationStatus: LiveData<Result<Unit>> = _operationStatus

    // LiveData para as listas dos spinners que vêm diretamente do repositório
    val tiposExame: LiveData<List<TipoExame>> = repository.getTiposExame().asLiveData()
    val clinicas: LiveData<List<Clinica>> = repository.getClinicas().asLiveData()

    // LiveData privado e mutável para a lista de veterinários
    private val _veterinarios = MutableLiveData<List<Veterinario>>()
    // LiveData público exposto à UI
    val veterinarios: LiveData<List<Veterinario>> = _veterinarios

    /**
     * Obtém a lista de exames de um animal específico
     * Retorna um LiveData que a UI pode observar
     */
    fun getExames(token: String, animalId: Int): LiveData<List<Exame>> {
        // Converte o Flow do repositório diretamente em LiveData
        return repository.getExames(token, animalId).asLiveData()
    }

    /**
     * Pede ao repositório para carregar os veterinários de uma clínica específica
     * e atualiza o LiveData _veterinarios
     */
    fun carregaVeterinarios(clinicaId: Int) {
        viewModelScope.launch {
            // Coleta os dados do Flow retornado pelo repositório
            repository.getVeterinariosPorClinica(clinicaId).collect {
                _veterinarios.postValue(it) // Atualiza o LiveData com a nova lista
            }
        }
    }

    /**
     * Orquestra a criação de um novo exame e o upload da sua foto (se existir)
     */
    fun adicionarExameEFoto(
        token: String,
        animalId: Int,
        tipoExameId: Int,
        dataExame: String,
        clinicaId: Int,
        veterinarioId: Int,
        resultado: String?, // opcional
        observacoes: String?, // opcional
        imageUri: Uri?, // opcional
        context: Context // Contexto necessário para o upload da imagem
    ) {
        viewModelScope.launch { // Inicia uma coroutine
            // Cria o objeto de pedido com os dados do exame
            val request = CreateExameRequest(
                animalId = animalId,
                tipoExameId = tipoExameId,
                dataExame = dataExame,
                clinicaId = clinicaId,
                veterinarioId = veterinarioId,
                resultado = resultado,
                observacoes = observacoes
            )
            // Primeiro cria o exame na API
            val result = repository.createExame(token, request)
            // Se a criação do exame for bem-sucedida
            result.onSuccess {
                // E se houver uma imagem para fazer upload
                if (imageUri != null) {
                    val exameId = it.exame.id // Obtém o ID do exame
                    // Faz o upload da foto para o exame
                    val uploadResult = repository.addFotoToExame(token, exameId, animalId, imageUri, context)
                    // Publica o resultado da operação de upload
                    _operationStatus.postValue(uploadResult.map { })
                } else {
                    // Se não houver imagem a operação termina com sucesso
                    _operationStatus.postValue(Result.success(Unit))
                }
            }.onFailure {
                // Se a criação do exame falhar publica a falha
                _operationStatus.postValue(Result.failure(it))
            }
        }
    }

    /**
     * Orquestra a atualização de um exame e opcionalmente o upload de uma nova foto
     */
    fun atualizarExameEFoto(
        token: String,
        exameId: Int,
        animalId: Int,
        tipoExameId: Int?, // opcional
        dataExame: String?, // opcional
        clinicaId: Int?, // opcional
        veterinarioId: Int?, // opcional
        resultado: String?, // opcional
        observacoes: String?, // opcional
        novaImagemUri: Uri?, // opcional
        context: Context // Contexto necessário para o upload
    ) {
        viewModelScope.launch { // Inicia uma coroutine
            // Cria o objeto de pedido com os dados a serem atualizados
            val updateRequest = UpdateExameRequest(
                tipoExameId = tipoExameId,
                dataExame = dataExame,
                clinicaId = clinicaId,
                veterinarioId = veterinarioId,
                resultado = resultado,
                observacoes = observacoes
            )
            // Primeiro atualiza os dados do exame
            val result = repository.updateExame(token, exameId, updateRequest)
            // Se a atualização for bem-sucedida
            result.onSuccess {
                // E se houver uma nova imagem para fazer upload
                if (novaImagemUri != null) {
                    // Faz o upload da nova foto para o exame
                    val uploadResult = repository.addFotoToExame(token, exameId, animalId, novaImagemUri, context)
                    // Publica o resultado da operação de upload
                    _operationStatus.postValue(uploadResult.map { })
                } else {
                    // Se não houver nova imagem a operação termina com sucesso
                    _operationStatus.postValue(Result.success(Unit))
                }
            }.onFailure {
                // Se a atualização falhar publica a falha
                _operationStatus.postValue(Result.failure(it))
            }
        }
    }

    /**
     * Pede ao repositório para apagar um exame específico
     */
    fun deleteExame(token: String, animalId: Int, exameId: Long) {
        viewModelScope.launch {
            // Chama o repositório para apagar o exame
            val result = repository.deleteExame(token, animalId, exameId)
            // Publica o resultado da operação
            _operationStatus.postValue(result)
        }
    }
}
