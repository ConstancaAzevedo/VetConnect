package pt.ipt.dam2025.vetconnect.viewmodel

import android.net.Uri // Importa a classe Uri para lidar com caminhos de ficheiros
import androidx.lifecycle.LiveData // Importa a classe LiveData para dados observáveis
import androidx.lifecycle.MutableLiveData // Importa a versão mutável do LiveData
import androidx.lifecycle.ViewModel // Importa a classe base ViewModel
import androidx.lifecycle.viewModelScope // Importa o contexto de coroutines para o ViewModel
import kotlinx.coroutines.launch // Importa a função para iniciar uma coroutine
import pt.ipt.dam2025.vetconnect.model.AnimalResponse
import pt.ipt.dam2025.vetconnect.model.CreateAnimalRequest
import pt.ipt.dam2025.vetconnect.repository.AnimalRepository

/**
 * ViewModel para gerir a lógica de negócio relacionada com os Animais
 * Atua como intermediário entre a UI e o AnimalRepository
 */
class AnimalViewModel(private val repository: AnimalRepository) : ViewModel() {

    // LiveData privado e mutável para os dados de um animal específico
    private val _animal = MutableLiveData<AnimalResponse?>()
    // LiveData público e imutável exposto à UI para observar os dados do animal
    val animal: LiveData<AnimalResponse?> = _animal

    // LiveData privado e mutável para o estado das operações
    private val _operationStatus = MutableLiveData<Result<Unit>>()
    // LiveData público exposto à UI para reagir ao resultado das operações
    val operationStatus: LiveData<Result<Unit>> = _operationStatus

    // LiveData privado e mutável para a URL da foto após o upload
    private val _fotoUrl = MutableLiveData<String?>()
    // LiveData público exposto à UI para atualizar a imagem do animal
    val fotoUrl: LiveData<String?> = _fotoUrl

    /**
     * Pede ao repositório para obter os dados de um animal específico
     * A UI observa o LiveData 'animal' para receber as atualizações
     */
    fun getAnimal(token: String, animalId: Int) {
        // Inicia uma coroutine no contexto do ViewModel
        viewModelScope.launch {
            // Coleta os dados do Flow retornado pelo repositório
            repository.getAnimal(token, animalId).collect { animalData ->
                // Atualiza o LiveData com os novos dados do animal
                _animal.postValue(animalData)
            }
        }
    }

    /**
     * Pede ao repositório para criar um animal
     * O resultado da operação é publicado no _operationStatus
     */
    fun saveAnimal(token: String, nome: String, especie: String, raca: String?, dataNascimento: String?, numeroChip: String?) {
        // Inicia uma coroutine no contexto do ViewModel
        viewModelScope.launch {
            // Cria o objeto de pedido com os dados do animal
            val request = CreateAnimalRequest(
                nome = nome,
                especie = especie,
                raca = raca,
                dataNascimento = dataNascimento,
                numeroChip = numeroChip
            )
            // Chama o repositório para criar o animal
            val result = repository.createAnimal(token, request)
            // Publica o resultado
            _operationStatus.postValue(result.map { })
        }
    }

    /**
     * Pede ao repositório para atualizar os dados de um animal
     */
    fun updateAnimal(token: String, animalId: Int, nome: String, especie: String, raca: String?, dataNascimento: String?, numeroChip: String?) {
        viewModelScope.launch {
            val request = CreateAnimalRequest(
                nome = nome,
                especie = especie,
                raca = raca,
                dataNascimento = dataNascimento,
                numeroChip = numeroChip
            )
            val result = repository.updateAnimal(token, animalId, request)
            _operationStatus.postValue(result.map { })
        }
    }

    /**
     * Pede ao repositório para fazer o upload da foto de um animal
     * Publica a nova URL da foto e o estado da operação
     */
    fun uploadPhoto(token: String, animalId: Int, photoUri: Uri) {
        // Inicia uma coroutine no contexto do ViewModel
        viewModelScope.launch {
            // Chama o repositório para fazer o upload da foto
            val result = repository.uploadFotoAnimal(token, animalId, photoUri)
            // Se o upload for bem-sucedido
            result.onSuccess { response ->
                // Publica a nova URL da foto para a UI
                _fotoUrl.postValue(response.fotoUrl)
            }
            // Publica o resultado da operação
            _operationStatus.postValue(result.map { })
        }
    }
}
