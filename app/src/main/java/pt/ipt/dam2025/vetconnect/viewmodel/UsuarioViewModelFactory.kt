package pt.ipt.dam2025.vetconnect.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.data.AppDatabase
import pt.ipt.dam2025.vetconnect.repository.UsuarioRepository

/**
 * Factory para criar instâncias de UsuarioViewModel
 * Fornece o UsuarioRepository (com as suas dependências) ao ViewModel
 */
class UsuarioViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
            val userDao = AppDatabase.getDatabase(application).userDao()
            val repository = UsuarioRepository(ApiClient.apiService, userDao)
            @Suppress("UNCHECKED_CAST")
            return UsuarioViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
