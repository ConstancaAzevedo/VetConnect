package pt.ipt.dam2025.vetconnect.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.data.AppDatabase
import pt.ipt.dam2025.vetconnect.repository.HistoricoRepository

/**
 * Factory para criar instâncias de AdicionarExameViewModel
 * Fornece o HistoricoRepository ao ViewModel
 */
class AdicionarExameViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdicionarExameViewModel::class.java)) {
            val exameDao = AppDatabase.getDatabase(application).exameDao()
            val repository = HistoricoRepository(ApiClient.apiService, exameDao)
            @Suppress("UNCHECKED_CAST")
            return AdicionarExameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
