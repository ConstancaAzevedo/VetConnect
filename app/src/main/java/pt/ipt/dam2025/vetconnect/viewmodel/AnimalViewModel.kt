package pt.ipt.dam2025.vetconnect.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.model.AnimalResponse
import pt.ipt.dam2025.vetconnect.model.CreateAnimalRequest
import pt.ipt.dam2025.vetconnect.repository.AnimalRepository

/**
 * ViewModel para gerir os dados e a lógica de negócio relacionados com os Animais
 */
class AnimalViewModel(private val repository: AnimalRepository) : ViewModel() {

    // LiveData para expor os dados de um animal específico para a UI
    private val _animal = MutableLiveData<AnimalResponse?>()
    val animal: LiveData<AnimalResponse?> = _animal

    // LiveData para comunicar o estado de operações (sucesso/falha) à UI
    private val _operationStatus = MutableLiveData<Result<Unit>>()
    val operationStatus: LiveData<Result<Unit>> = _operationStatus

    // LiveData para expor a URL da foto de um animal após o upload
    private val _fotoUrl = MutableLiveData<String?>()
    val fotoUrl: LiveData<String?> = _fotoUrl

    /*
     * obtém os dados de um animal específico a partir do repositório
     */
    fun getAnimal(token: String, animalId: Int) {
        viewModelScope.launch {
            repository.getAnimal(token, animalId).collect {
                _animal.postValue(it)
            }
        }
    }

    /*
     * guarda um novo animal, enviando os dados para o repositório
     */
    fun saveAnimal(token: String, nome: String, especie: String, raca: String?, dataNascimento: String?, numeroChip: String?) {
        viewModelScope.launch {
            val request = CreateAnimalRequest(
                nome = nome,
                especie = especie,
                raca = raca,
                dataNascimento = dataNascimento,
                numeroChip = numeroChip
            )
            val result = repository.createAnimal(token, request)
            _operationStatus.postValue(result.map { })
        }
    }

    /*
     * faz o upload da foto de um animal
     */
    fun uploadPhoto(token: String, animalId: Int, photoUri: Uri) {
        viewModelScope.launch {
            val result = repository.uploadFotoAnimal(token, animalId, photoUri)
            result.onSuccess { response ->
                _fotoUrl.postValue(response.fotoUrl)
            }
            _operationStatus.postValue(result.map { })
        }
    }
}
