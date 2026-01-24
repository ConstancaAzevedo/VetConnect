package pt.ipt.dam2025.trabalho.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.data.AppDatabase
import pt.ipt.dam2025.trabalho.model.Animal
import pt.ipt.dam2025.trabalho.model.AnimalResponse
import pt.ipt.dam2025.trabalho.model.CreateAnimalRequest
import pt.ipt.dam2025.trabalho.repository.AnimalRepository

// ViewModel para Animal
class AnimalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AnimalRepository

    private val _animal = MutableLiveData<Animal?>()
    val animal: LiveData<Animal?> = _animal

    private val _operationStatus = MutableLiveData<Result<Unit>>()
    val operationStatus: LiveData<Result<Unit>> = _operationStatus

    private val _fotoUrl = MutableLiveData<String?>()
    val fotoUrl: LiveData<String?> = _fotoUrl

    init {
        val animalDao = AppDatabase.getDatabase(application).animalDao()
        repository = AnimalRepository(ApiClient.apiService, animalDao, getApplication())
    }

    fun getAnimal(token: String, animalId: Int) {
        viewModelScope.launch {
            repository.getAnimal(token, animalId).collect {
                _animal.postValue(it?.toAnimal())
            }
        }
    }

    fun saveAnimal(token: String, animal: Animal) {
        viewModelScope.launch {
            val request = CreateAnimalRequest(
                nome = animal.nome,
                dataNascimento = animal.dataNascimento,
                raca = animal.raca,
                especie = animal.especie,
                numeroChip = animal.numeroChip
            )
            val result = repository.createAnimal(token, request)
            _operationStatus.postValue(result.map {})
        }
    }

    fun deleteAnimal(token: String, animalId: Int) {
        viewModelScope.launch {
            val result = repository.deleteAnimal(token, animalId)
            _operationStatus.postValue(result)
        }
    }

    fun uploadPhoto(token: String, animalId: Int, photoUri: Uri) {
        viewModelScope.launch {
            val result = repository.uploadFotoAnimal(token, animalId, photoUri)
            result.onSuccess { response ->
                _fotoUrl.postValue(response.fotoUrl)
            }
            _operationStatus.postValue(result.map { })
        }
    }

    private fun AnimalResponse.toAnimal(): Animal {
        return Animal(
            id = this.id,
            nome = this.nome,
            dataNascimento = this.dataNascimento,
            raca = this.raca,
            especie = this.especie,
            tutorId = this.tutorId,
            fotoUrl = this.fotoUrl,
            numeroChip = this.numeroChip,
            codigoUnico = this.codigoUnico,
            dataRegisto = this.dataRegisto,
            tutorNome = this.tutorNome,
            tutorEmail = this.tutorEmail
        )
    }
}