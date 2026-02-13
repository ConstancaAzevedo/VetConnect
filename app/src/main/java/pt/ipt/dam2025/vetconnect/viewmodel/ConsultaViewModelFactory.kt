package pt.ipt.dam2025.vetconnect.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.data.AppDatabase
import pt.ipt.dam2025.vetconnect.repository.ConsultaRepository

/**
 * Factory para criar instâncias de ConsultaViewModel
 * Fornece o ConsultaRepository (com as suas dependências) ao ViewModel
 */
class ConsultaViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConsultaViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val repository = ConsultaRepository(
                apiService = ApiClient.apiService,
                consultaDao = database.consultaDao(),
                clinicaDao = database.clinicaDao(),
                veterinarioDao = database.veterinarioDao(),
                animalDao = database.animalDao()
            )
            @Suppress("UNCHECKED_CAST")
            return ConsultaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
