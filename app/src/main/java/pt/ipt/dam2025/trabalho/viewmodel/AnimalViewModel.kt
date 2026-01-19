package pt.ipt.dam2025.trabalho.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import pt.ipt.dam2025.trabalho.api.ApiService
import pt.ipt.dam2025.trabalho.model.Animal

class AnimalViewModel(private val application: Application, private val apiService: ApiService) : ViewModel() {

    private val _animal = MutableLiveData<Animal>()
    val animal: LiveData<Animal> = _animal

    private val _operationStatus = MutableLiveData<Result<String>>()
    val operationStatus: LiveData<Result<String>> = _operationStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchAnimal(token: String, animalId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getAnimal("Bearer $token", animalId)
                if (response.isSuccessful) {
                    _animal.postValue(response.body())
                } else {
                    _errorMessage.postValue("Erro ao carregar dados do animal")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Falha na ligação: ${e.message}")
            }
        }
    }

    fun saveAnimal(token: String, animal: Animal) {
        viewModelScope.launch {
            try {
                val response = if (animal.id == 0) {
                    apiService.createAnimal("Bearer $token", animal)
                } else {
                    apiService.updateAnimal("Bearer $token", animal.id, animal)
                }

                if (response.isSuccessful) {
                    _operationStatus.postValue(Result.success("Animal guardado com sucesso"))
                } else {
                    _operationStatus.postValue(Result.failure(Exception("Erro ao guardar animal")))
                }
            } catch (e: Exception) {
                _operationStatus.postValue(Result.failure(e))
            }
        }
    }

    fun deleteAnimal(token: String, animalId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.deleteAnimal("Bearer $token", animalId)
                if (response.isSuccessful) {
                    _operationStatus.postValue(Result.success("Animal eliminado com sucesso"))
                } else {
                    _operationStatus.postValue(Result.failure(Exception("Erro ao eliminar animal")))
                }
            } catch (e: Exception) {
                _operationStatus.postValue(Result.failure(e))
            }
        }
    }

    fun uploadFotoAnimal(token: String, animalId: Int, fotoUri: Uri) {
        viewModelScope.launch {
            try {
                val inputStream = application.contentResolver.openInputStream(fotoUri)
                val fileBytes = inputStream?.readBytes()
                inputStream?.close()

                if (fileBytes != null) {
                    val requestFile = fileBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("foto", "foto.jpg", requestFile)

                    val response = apiService.uploadFoto("Bearer $token", animalId, body)
                    if (response.isSuccessful) {
                        _operationStatus.postValue(Result.success("Foto atualizada com sucesso."))
                        // Pode ser útil recarregar os dados do animal para obter a nova URL da foto
                        fetchAnimal(token, animalId)
                    } else {
                        _operationStatus.postValue(Result.failure(Exception("Erro ao enviar foto.")))
                    }
                }
            } catch (e: Exception) {
                _operationStatus.postValue(Result.failure(e))
            }
        }
    }
}
