package pt.ipt.dam2025.trabalho.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.data.AppDatabase
import pt.ipt.dam2025.trabalho.repository.AnimalRepository
import pt.ipt.dam2025.trabalho.repository.ConsultaRepository
import pt.ipt.dam2025.trabalho.repository.HistoricoRepository
import pt.ipt.dam2025.trabalho.repository.UsuarioRepository

/**
 * Factory genérica para criar instâncias de ViewModels com as suas dependências.
 * Isto evita que os ViewModels precisem de conhecer a lógica de criação dos repositórios.
 */
class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UsuarioViewModel::class.java) -> {
                val userDao = AppDatabase.getDatabase(context).userDao()
                val repository = UsuarioRepository(ApiClient.apiService, userDao)
                UsuarioViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AnimalViewModel::class.java) -> {
                AnimalViewModel(context.applicationContext as Application) as T
            }
            modelClass.isAssignableFrom(ConsultaViewModel::class.java) -> {
                ConsultaViewModel(context.applicationContext as Application) as T
            }
            modelClass.isAssignableFrom(HistoricoViewModel::class.java) -> {
                val db = AppDatabase.getDatabase(context)
                val repository = HistoricoRepository(
                    ApiClient.apiService,
                    db.receitaDao(),
                    db.vacinaDao(),
                    db.exameDao(),
                    Gson()
                )
                HistoricoViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Classe de ViewModel desconhecida: ${modelClass.name}")
        }
    }
}
