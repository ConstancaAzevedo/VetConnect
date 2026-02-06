package pt.ipt.dam2025.vetconnect.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.data.AppDatabase
import pt.ipt.dam2025.vetconnect.repository.AnimalRepository

/**
 * Factory para criar instâncias de AnimalViewModel
 * Fornece o AnimalRepository (com as suas dependências) ao ViewModel
 */
class AnimalViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnimalViewModel::class.java)) {
            val animalDao = AppDatabase.getDatabase(application).animalDao()
            val repository = AnimalRepository(ApiClient.apiService, animalDao, application)
            @Suppress("UNCHECKED_CAST")
            return AnimalViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
