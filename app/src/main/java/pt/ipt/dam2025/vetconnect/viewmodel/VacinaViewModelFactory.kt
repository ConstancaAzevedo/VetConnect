package pt.ipt.dam2025.vetconnect.viewmodel

import android.app.Application // Importa a classe Application para aceder ao contexto global
import androidx.lifecycle.ViewModel // Importa a classe base ViewModel
import androidx.lifecycle.ViewModelProvider // Importa a interface ViewModelProviderFactory
import pt.ipt.dam2025.vetconnect.api.ApiClient // Importa o nosso cliente de API singleton
import pt.ipt.dam2025.vetconnect.data.AppDatabase // Importa a nossa classe de base de dados Room
import pt.ipt.dam2025.vetconnect.repository.VacinaRepository // Importa o repositório de vacinas

/**
 * Factory para criar instâncias do UtilizadorViewModel
 *  Fornece o VacinaRepository ao ViewModel
 */
class VacinaViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    /**
     * Este métdo é chamado pelo sistema quando é necessário criar um ViewModel
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica se a classe do ViewModel a ser criada é ou herda de VacinaViewModel
        if (modelClass.isAssignableFrom(VacinaViewModel::class.java)) {
            // Obtém a instância da base de dados Room
            val db = AppDatabase.getDatabase(application)
            // Cria uma instância do VacinaRepository com todas as dependências
            val repository = VacinaRepository(
                apiService = ApiClient.apiService,
                vacinaDao = db.vacinaDao(),
                tipoVacinaDao = db.tipoVacinaDao(),
                clinicaDao = db.clinicaDao(),
                veterinarioDao = db.veterinarioDao()
            )
            @Suppress("UNCHECKED_CAST")
            // Cria e retorna uma instância do VacinaViewModel com o repositório
            return VacinaViewModel(repository) as T
        }
        // Se a classe do ViewModel for desconhecida lança uma exceção
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
