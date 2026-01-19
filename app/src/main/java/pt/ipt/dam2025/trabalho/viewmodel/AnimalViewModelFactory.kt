package pt.ipt.dam2025.trabalho.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.ipt.dam2025.trabalho.api.ApiService

class AnimalViewModelFactory(private val application: Application, private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnimalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnimalViewModel(application, apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
