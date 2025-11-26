package pt.ipt.dam2025.trabalho.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.ipt.dam2025.trabalho.data.HistoricoDao
import pt.ipt.dam2025.trabalho.repository.HistoricoRepository

/**
 * Factory para o HistoricoViewModel.
 * "Ensina" o sistema a criar o ViewModel, construindo as dependências necessárias (DAO -> Repository -> ViewModel).
 */
class HistoricoViewModelFactory(private val dao: HistoricoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoricoViewModel::class.java)) {
            // 1. Cria o Repositório, passando o DAO
            val repository = HistoricoRepository(dao)
            // 2. Cria o ViewModel, passando o Repositório
            @Suppress("UNCHECKED_CAST")
            return HistoricoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
