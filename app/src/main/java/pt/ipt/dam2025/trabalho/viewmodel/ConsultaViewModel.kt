package pt.ipt.dam2025.trabalho.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.data.AppDatabase
import pt.ipt.dam2025.trabalho.model.Consulta
import pt.ipt.dam2025.trabalho.repository.ConsultaRepository

// ViewModel para Consulta
class ConsultaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ConsultaRepository

    private val _consultas = MutableLiveData<List<Consulta>>()
    val consultas: LiveData<List<Consulta>> = _consultas

    private val _operationStatus = MutableLiveData<Boolean>()
    val operationStatus: LiveData<Boolean> = _operationStatus

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ConsultaRepository(ApiClient.apiService, db.consultaDao(), db.clinicaDao(), db.veterinarioDao())
    }

    fun fetchConsultas(token: String, userId: Int) {
        viewModelScope.launch {
            repository.getConsultas(token, userId).collect {
                _consultas.postValue(it)
            }
        }
    }

    fun cancelarConsulta(token: String, consultaId: Int) {
        viewModelScope.launch {
            try {
                repository.cancelarConsulta(token, consultaId)
                _operationStatus.postValue(true)
            } catch (e: Exception) {
                _operationStatus.postValue(false)
            }
        }
    }
}