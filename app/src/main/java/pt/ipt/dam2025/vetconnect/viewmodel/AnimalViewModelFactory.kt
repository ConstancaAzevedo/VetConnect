package pt.ipt.dam2025.vetconnect.viewmodel

import android.app.Application // Importa a classe Application para aceder ao contexto global
import androidx.lifecycle.ViewModel // Importa a classe base ViewModel
import androidx.lifecycle.ViewModelProvider // Importa a interface ViewModelProviderFactory
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.data.AppDatabase
import pt.ipt.dam2025.vetconnect.repository.AnimalRepository

/**
 * Factory para criar instâncias de AnimalViewModel
 * Fornece o AnimalRepository (com as suas dependências) ao ViewModel
 */
class AnimalViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    /**
     * Este métdo é chamado pelo sistema Android quando é preciso criar um ViewModel
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica se a classe do ViewModel a ser criada é ou herda de AnimalViewModel
        if (modelClass.isAssignableFrom(AnimalViewModel::class.java)) {
            // Obtém o DAO (Data Access Object) do animal a partir da base de dados
            val animalDao = AppDatabase.getDatabase(application).animalDao()
            // Cria uma instância do AnimalRepository com as suas dependências
            val repository = AnimalRepository(ApiClient.apiService, animalDao, application)
            @Suppress("UNCHECKED_CAST")
            // Cria e retorna uma nova instância do AnimalViewModel passando o repositório
            return AnimalViewModel(repository) as T
        }
        // Se a classe do ViewModel for desconhecida lança uma exceção
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
