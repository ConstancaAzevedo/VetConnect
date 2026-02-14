package pt.ipt.dam2025.vetconnect.viewmodel

import android.app.Application // Importa a classe Application para aceder ao contexto global
import androidx.lifecycle.ViewModel // Importa a classe base ViewModel
import androidx.lifecycle.ViewModelProvider // Importa a interface ViewModelProviderFactory
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.data.AppDatabase
import pt.ipt.dam2025.vetconnect.repository.HistoricoRepository

/**
 * Factorypara criar instâncias do HistoricoViewModel
 * Fornece o HistoricoRepository ao ViewModel
 */
class HistoricoViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    /**
     * Este métdo é chamado pelo sistema Android quando é preciso criar um ViewModel
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica se a classe do ViewModel a ser criada é ou herda de HistoricoViewModel
        if (modelClass.isAssignableFrom(HistoricoViewModel::class.java)) {
            // Obtém a instância da base de dados Room
            val database = AppDatabase.getDatabase(application)
            // Cria uma instância do HistoricoRepository com todas as suas dependências
            val repository = HistoricoRepository(
                apiService = ApiClient.apiService,
                exameDao = database.exameDao(),
                tipoExameDao = database.tipoExameDao(),
                clinicaDao = database.clinicaDao(),
                veterinarioDao = database.veterinarioDao()
            )
            @Suppress("UNCHECKED_CAST")
            // Cria e retorna uma nova instância do HistoricoViewModel passando o repositório
            return HistoricoViewModel(repository) as T
        }
        // Se a classe do ViewModel for desconhecida lança uma exceção
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
