package pt.ipt.dam2025.trabalho.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.ipt.dam2025.trabalho.data.HistoricoDao

// Factory: "ensina" o Android a criar o nosso ViewModel, passando o DAO como parâmetro.
class HistoricoViewModelFactory(private val dao: HistoricoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoricoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoricoViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
