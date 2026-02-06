package pt.ipt.dam2025.vetconnect.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.data.AppDatabase
import pt.ipt.dam2025.vetconnect.repository.ConsultaRepository

/**
 * Factory para criar instâncias de MarcarConsultaViewModel
 * Fornece o ConsultaRepository (com as suas dependências) ao ViewModel
 */
class MarcarConsultaViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MarcarConsultaViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val repository = ConsultaRepository(
                apiService = ApiClient.apiService,
                consultaDao = database.consultaDao(),
                clinicaDao = database.clinicaDao(),
                veterinarioDao = database.veterinarioDao()
            )
            @Suppress("UNCHECKED_CAST")
            return MarcarConsultaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
