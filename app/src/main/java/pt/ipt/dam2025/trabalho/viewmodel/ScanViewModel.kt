package pt.ipt.dam2025.trabalho.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.data.AppDatabase
import pt.ipt.dam2025.trabalho.model.CreateDocumentRequest
import pt.ipt.dam2025.trabalho.repository.HistoricoRepository

// ViewModel para o QR Code
class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoricoRepository

    private val _uploadStatus = MutableLiveData<Result<Unit>>()
    val uploadStatus: LiveData<Result<Unit>> = _uploadStatus

    init {
        val db = AppDatabase.getDatabase(application)
        repository = HistoricoRepository(
            apiService = ApiClient.apiService,
            receitaDao = db.receitaDao(),
            vacinaDao = db.vacinaDao(),
            exameDao = db.exameDao(),
            gson = Gson()
        )
    }

    fun sendQrCode(token: String, qrCodeData: String) {
        viewModelScope.launch {
            try {
                val request = Gson().fromJson(qrCodeData, CreateDocumentRequest::class.java)
                val result = repository.createDocument(token, request)
                _uploadStatus.postValue(result.map { })
            } catch (e: Exception) {
                _uploadStatus.postValue(Result.failure(e))
            }
        }
    }
}