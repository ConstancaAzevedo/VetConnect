package pt.ipt.dam2025.trabalho.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.data.AppDatabase
import pt.ipt.dam2025.trabalho.model.Animal
import pt.ipt.dam2025.trabalho.repository.AnimalRepository

class AnimalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AnimalRepository

    init {
        val animalDao = AppDatabase.getDatabase(application).animalDao()
        repository = AnimalRepository(animalDao)
    }

    fun insert(animal: Animal) = viewModelScope.launch {
        repository.insert(animal)
    }
}