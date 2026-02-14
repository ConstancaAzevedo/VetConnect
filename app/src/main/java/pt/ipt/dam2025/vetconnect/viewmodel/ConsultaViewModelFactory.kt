package pt.ipt.dam2025.vetconnect.viewmodel

import android.app.Application // Importa a classe Application para aceder ao contexto global
import androidx.lifecycle.ViewModel // Importa a classe base ViewModel
import androidx.lifecycle.ViewModelProvider // Importa a interface ViewModelProviderFactory
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.data.AppDatabase
import pt.ipt.dam2025.vetconnect.repository.ConsultaRepository

/**
 * Factory para criar instâncias de ConsultaViewModel
 * Fornece o ConsultaRepository ao ViewModel
 */
class ConsultaViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    /**
     * Este métdo é chamado pelo sistema Android quando é preciso criar um ViewModel
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica se a classe do ViewModel a ser criada é ou herda de ConsultaViewModel
        if (modelClass.isAssignableFrom(ConsultaViewModel::class.java)) {
            // Obtém a instância da base de dados Room
            val database = AppDatabase.getDatabase(application)

            // Cria uma instância do ConsultaRepository com as suas dependências
            val consultaRepository = ConsultaRepository(
                apiService = ApiClient.apiService,
                consultaDao = database.consultaDao(),
                clinicaDao = database.clinicaDao(),
                veterinarioDao = database.veterinarioDao()
            )

            @Suppress("UNCHECKED_CAST")
            // Cria e retorna uma nova instância do ConsultaViewModel passando o repositório
            return ConsultaViewModel(consultaRepository) as T
        }
        // Se a classe do ViewModel for desconhecida lança uma exceção
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
