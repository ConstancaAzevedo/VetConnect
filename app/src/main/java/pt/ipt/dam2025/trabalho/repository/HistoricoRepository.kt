package pt.ipt.dam2025.trabalho.repository

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.api.ApiService
import pt.ipt.dam2025.trabalho.data.ExameDao
import pt.ipt.dam2025.trabalho.data.ReceitaDao
import pt.ipt.dam2025.trabalho.data.VacinaDao
import pt.ipt.dam2025.trabalho.model.CreateDocumentRequest
import pt.ipt.dam2025.trabalho.model.CreateDocumentResponse
import pt.ipt.dam2025.trabalho.model.DeleteDocumentResponse
import pt.ipt.dam2025.trabalho.model.Exame
import pt.ipt.dam2025.trabalho.model.Receita
import pt.ipt.dam2025.trabalho.model.Vacina
import java.io.IOException

// Classe de repositório para histórico
class HistoricoRepository(
    private val apiService: ApiService,
    private val receitaDao: ReceitaDao,
    private val vacinaDao: VacinaDao,
    private val exameDao: ExameDao,
    private val gson: Gson
) {

    val todasReceitas: Flow<List<Receita>> = receitaDao.getAllReceitas()
    val todasVacinas: Flow<List<Vacina>> = vacinaDao.getAllVacinas()
    val todosExames: Flow<List<Exame>> = exameDao.getAllExames()

    fun getReceitas(animalId: Int): Flow<List<Receita>> {
        return receitaDao.getReceitasByAnimal(animalId)
    }

    fun getVacinas(animalId: Int): Flow<List<Vacina>> {
        return vacinaDao.getVacinasByAnimal(animalId)
    }

    fun getExames(animalId: Int): Flow<List<Exame>> {
        return exameDao.getExamesByAnimal(animalId)
    }

    /**
     * Dispara a atualização do histórico de um animal a partir da API.
     */
    fun refreshHistorico(token: String, animalId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getDocumentosDoAnimal("Bearer $token", animalId)

                if (response.isSuccessful) {
                    response.body()?.let { documentos ->
                        // Limpa os dados antigos para este animal
                        receitaDao.deleteByAnimal(animalId)
                        vacinaDao.deleteByAnimal(animalId)
                        exameDao.deleteByAnimal(animalId)

                        // Insere os novos dados
                        receitaDao.insertAll(documentos.receitas)
                        vacinaDao.insertAll(documentos.vacinas)
                        exameDao.insertAll(documentos.exames)

                        Log.d("HistoricoRepository", "Histórico do animal $animalId atualizado.")
                    }
                } else {
                    Log.e("HistoricoRepository", "Erro na API ao atualizar histórico: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HistoricoRepository", "Falha ao atualizar o histórico a partir da API", e)
            }
        }
    }

    /**
     * Cria um novo documento (receita, vacina ou exame) na API.
     * Em caso de sucesso, o histórico do animal é atualizado.
     */
    suspend fun createDocument(token: String, request: CreateDocumentRequest): Result<CreateDocumentResponse> {
        return try {
            val response = apiService.createDocumento("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                // Atualiza o histórico para refletir a adição
                refreshHistorico(token, request.animalId)
                Result.success(response.body()!!)
            } else {
                Result.failure(IOException("Erro da API ao criar documento: ${response.code()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Apaga um documento (receita, vacina ou exame) da API.
     * Em caso de sucesso, o histórico do animal é atualizado.
     */
    suspend fun deleteDocument(token: String, animalId: Int, tipo: String, documentoId: Long): Result<DeleteDocumentResponse> {
        return try {
            val response = apiService.deleteDocumento("Bearer $token", tipo, documentoId)
            if (response.isSuccessful && response.body() != null) {
                // Atualiza o histórico para refletir a remoção
                refreshHistorico(token, animalId)
                Result.success(response.body()!!)
            } else {
                Result.failure(IOException("Erro da API ao apagar documento: ${response.code()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /*suspend fun deleteReceita(receita: Receita) {
        receitaDao.delete(receita)
    }

    suspend fun deleteVacina(vacina: Vacina) {
        vacinaDao.delete(vacina)
    }

    suspend fun deleteExame(exame: Exame) {
        exameDao.delete(exame)
    }*/
}