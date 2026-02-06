package pt.ipt.dam2025.vetconnect.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.api.ApiService
import pt.ipt.dam2025.vetconnect.data.ExameDao
import pt.ipt.dam2025.vetconnect.model.CreateExameRequest
import pt.ipt.dam2025.vetconnect.model.CreateExameResponse
import pt.ipt.dam2025.vetconnect.model.Exame
import java.io.IOException

/**
 * Repositório para gerir o histórico de exames de um animal.
 */
class HistoricoRepository(
    private val apiService: ApiService,
    private val exameDao: ExameDao
) {

    /**
     * Obtém os exames de um animal. Retorna um Flow da base de dados local
     * e inicia uma atualização em background a partir da API.
     */
    fun getExames(token: String, animalId: Int): Flow<List<Exame>> {
        CoroutineScope(Dispatchers.IO).launch {
            refreshExames(token, animalId)
        }
        return exameDao.getExamesByAnimal(animalId)
    }

    /**
     * Força a atualização da lista de exames de um animal a partir da API
     * e guarda-os na base de dados local.
     */
    suspend fun refreshExames(token: String, animalId: Int) {
        try {
            val response = apiService.getExamesDoAnimal("Bearer $token", animalId)
            if (response.isSuccessful) {
                response.body()?.let {
                    // Limpa os dados antigos e insere os novos
                    exameDao.deleteByAnimal(animalId)
                    exameDao.insertAll(it.exames)
                }
            } else {
                Log.e("HistoricoRepository", "Erro na API ao atualizar exames: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("HistoricoRepository", "Falha ao atualizar o histórico de exames", e)
        }
    }

    /**
     * Cria um novo exame através da API.
     * Em caso de sucesso, atualiza o histórico para refletir a adição.
     */
    suspend fun createExame(token: String, request: CreateExameRequest): Result<CreateExameResponse> {
        return try {
            val response = apiService.createExame("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                // Atualiza o histórico para refletir a adição
                refreshExames(token, request.animalId)
                Result.success(response.body()!!)
            } else {
                Result.failure(IOException("Erro da API ao criar exame: ${response.code()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Apaga um exame através da API.
     * Em caso de sucesso, atualiza o histórico para refletir a remoção.
     */
    suspend fun deleteExame(token: String, animalId: Int, exameId: Long): Result<Unit> {
        return try {
            val response = apiService.deleteExame("Bearer $token", exameId)
            if (response.isSuccessful) {
                // Atualiza o histórico para refletir a remoção
                refreshExames(token, animalId)
                Result.success(Unit)
            } else {
                Result.failure(IOException("Erro da API ao apagar exame: ${response.code()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
