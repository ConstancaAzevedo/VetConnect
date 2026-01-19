package pt.ipt.dam2025.trabalho.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.api.ApiService
import pt.ipt.dam2025.trabalho.data.HistoricoRepository
import pt.ipt.dam2025.trabalho.model.Exame
import pt.ipt.dam2025.trabalho.model.QrCodePayload
import pt.ipt.dam2025.trabalho.model.Receita
import pt.ipt.dam2025.trabalho.model.Vacina

class HistoricoViewModel(
    private val repository: HistoricoRepository,
    private val apiService: ApiService
) : ViewModel() {

    val receitas: LiveData<List<Receita>> = repository.todasReceitas.asLiveData()
    val exames: LiveData<List<Exame>> = repository.todosExames.asLiveData()
    val vacinas: LiveData<List<Vacina>> = repository.todasVacinas.asLiveData()

    /**
     * Processa o JSON lido de um QR Code, envia para o servidor e insere na base de dados local.
     */
    fun processQrCode(json: String, authToken: String) {
        val gson = Gson()
        try {
            // O servidor espera um objeto com "tipo", "animalId" e "dados".
            // O QR code contém estes campos.
            val payload = gson.fromJson(json, QrCodePayload::class.java)

            viewModelScope.launch {
                try {
                    // 1. Enviar para o servidor
                    val response = apiService.createDocumento("Bearer $authToken", payload)

                    if (response.isSuccessful) {
                        Log.d("HistoricoViewModel", "Documento enviado para o servidor com sucesso.")
                        // 2. Inserir na base de dados local se o servidor confirmar
                        when (payload.tipo) {
                            "receita" -> {
                                val dados = gson.toJson(payload.dados) // Precisamos do objeto de dados
                                val novaReceita = gson.fromJson(dados, Receita::class.java)
                                repository.insertReceita(novaReceita)
                            }
                            "exame" -> {
                                val dados = gson.toJson(payload.dados)
                                val novoExame = gson.fromJson(dados, Exame::class.java)
                                repository.insertExame(novoExame)
                            }
                            "vacina" -> {
                                val dados = gson.toJson(payload.dados)
                                val novaVacina = gson.fromJson(dados, Vacina::class.java)
                                repository.insertVacina(novaVacina)
                            }
                        }
                    } else {
                        Log.e("HistoricoViewModel", "Erro ao enviar documento para o servidor: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("HistoricoViewModel", "Falha na chamada à API", e)
                }
            }
        } catch (e: Exception) {
            Log.e("HistoricoViewModel", "Erro ao processar JSON do QR Code", e)
        }
    }
}
