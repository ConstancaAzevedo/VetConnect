package pt.ipt.dam2025.trabalho.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.model.CreateDocumentRequest
import pt.ipt.dam2025.trabalho.model.QrCodePayload
import pt.ipt.dam2025.trabalho.repository.HistoricoRepository

// ViewModel para adicionar documento
class AdicionarDocumentoViewModel(private val repository: HistoricoRepository) : ViewModel() {

    private val _documentoAdicionado = MutableLiveData<Boolean>()
    val documentoAdicionado: LiveData<Boolean> = _documentoAdicionado

    fun adicionarDocumento(token: String, payload: QrCodePayload) {
        viewModelScope.launch {
            val request = Gson().fromJson(payload.payload, CreateDocumentRequest::class.java)
            val result = repository.createDocument(token, request)
            _documentoAdicionado.postValue(result.isSuccess)
        }
    }
}