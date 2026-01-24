package pt.ipt.dam2025.trabalho.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.data.AppDatabase
import pt.ipt.dam2025.trabalho.repository.HistoricoRepository

// Classe de fábrica para o ViewModel
class AdicionarDocumentoViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdicionarDocumentoViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val repository = HistoricoRepository(
                apiService = ApiClient.apiService,
                receitaDao = database.receitaDao(),
                vacinaDao = database.vacinaDao(),
                exameDao = database.exameDao(),
                gson = Gson()
            )
            @Suppress("UNCHECKED_CAST")
            return AdicionarDocumentoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}