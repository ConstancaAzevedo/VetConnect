package pt.ipt.dam2025.vetconnect.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.data.AppDatabase
import pt.ipt.dam2025.vetconnect.repository.VacinaRepository

class VacinaViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VacinaViewModel::class.java)) {
            val db = AppDatabase.getDatabase(application)
            val repository = VacinaRepository(
                apiService = ApiClient.apiService,
                vacinaDao = db.vacinaDao(),
                tipoVacinaDao = db.tipoVacinaDao(),
                clinicaDao = db.clinicaDao(),
                veterinarioDao = db.veterinarioDao()
            )
            @Suppress("UNCHECKED_CAST")
            return VacinaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
