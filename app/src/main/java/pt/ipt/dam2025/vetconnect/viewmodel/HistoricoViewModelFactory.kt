package pt.ipt.dam2025.vetconnect.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.data.AppDatabase
import pt.ipt.dam2025.vetconnect.repository.HistoricoRepository

/**
 * Factory para criar instâncias de HistoricoViewModel
 * Fornece o HistoricoRepository ao ViewModel
 */
class HistoricoViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoricoViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val repository = HistoricoRepository(
                apiService = ApiClient.apiService,
                exameDao = database.exameDao(),
                tipoExameDao = database.tipoExameDao(),
                clinicaDao = database.clinicaDao(),
                veterinarioDao = database.veterinarioDao()
            )
            @Suppress("UNCHECKED_CAST")
            return HistoricoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
