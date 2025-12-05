package pt.ipt.dam2025.trabalho.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ipt.dam2025.trabalho.data.AppDatabase
import pt.ipt.dam2025.trabalho.model.Animal
import pt.ipt.dam2025.trabalho.repository.AnimalRepository

class AnimalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AnimalRepository
    private val animalDao = AppDatabase.getDatabase(application).animalDao()

    private val _animal = MutableLiveData<Animal?>()
    val animal: LiveData<Animal?> = _animal

    init {
        repository = AnimalRepository(animalDao)
        loadAnimalForCurrentUser()
    }

    fun loadAnimalForCurrentUser() {
        viewModelScope.launch {
            val sharedPrefs = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = sharedPrefs.getInt("LOGGED_IN_USER_ID", -1)
            if (userId != -1) {
                val animalData = withContext(Dispatchers.IO) {
                    animalDao.getAnimalByTutorId(userId)
                }
                _animal.postValue(animalData)
            }
        }
    }

    fun updateAnimal(animal: Animal, userId: Int) = viewModelScope.launch {
        animal.tutorId = userId
        val existingAnimal = withContext(Dispatchers.IO) {
            animalDao.getAnimalByTutorId(userId)
        }

        val animalToSave = existingAnimal?.copy(
            nome = animal.nome,
            especie = animal.especie,
            raca = animal.raca,
            dataNascimento = animal.dataNascimento,
            fotoUri = animal.fotoUri ?: existingAnimal.fotoUri
        ) ?: animal

        repository.insert(animalToSave)
        _animal.postValue(animalToSave)
    }
}