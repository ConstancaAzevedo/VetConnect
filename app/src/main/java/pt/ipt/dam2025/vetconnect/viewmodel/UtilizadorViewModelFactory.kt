package pt.ipt.dam2025.vetconnect.viewmodel

import android.app.Application // Importa a classe Application para aceder ao contexto global
import androidx.lifecycle.ViewModel // Importa a classe base ViewModel
import androidx.lifecycle.ViewModelProvider // Importa a interface ViewModelProviderFactory
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.data.AppDatabase
import pt.ipt.dam2025.vetconnect.repository.UtilizadorRepository

/**
 * Factory para criar instâncias do UtilizadorViewModel
 * Fornece o UtilizadorRepository ao ViewModel
 */
class UtilizadorViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    /**
     * Este métdo é chamado pelo sistema quando é preciso criar um ViewModel
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica se a classe do ViewModel a ser criada é ou herda de UtilizadorViewModel
        if (modelClass.isAssignableFrom(UtilizadorViewModel::class.java)) {
            // Obtém o DAO do utilizador a partir da base de dados
            val userDao = AppDatabase.getDatabase(application).userDao()
            // Cria uma instância do UtilizadorRepository com as suas dependências
            val repository = UtilizadorRepository(ApiClient.apiService, userDao)
            @Suppress("UNCHECKED_CAST")
            // Cria e retorna uma nova instância do UtilizadorViewModel passando o repositório
            return UtilizadorViewModel(repository) as T
        }
        // Se a classe do ViewModel for desconhecida lança uma exceção
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
