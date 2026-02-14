package pt.ipt.dam2025.vetconnect.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import pt.ipt.dam2025.vetconnect.api.ApiService
import pt.ipt.dam2025.vetconnect.data.ClinicaDao
import pt.ipt.dam2025.vetconnect.data.ExameDao
import pt.ipt.dam2025.vetconnect.data.TipoExameDao
import pt.ipt.dam2025.vetconnect.data.VeterinarioDao
import pt.ipt.dam2025.vetconnect.model.*
import java.io.IOException

/**
 * Repositório para gerir o histórico de exames de um animal
 * É a única fonte de verdade para os dados dos exames, coordenando a API e a base de dados local
 */
class HistoricoRepository(
    private val apiService: ApiService,
    private val exameDao: ExameDao,
    private val tipoExameDao: TipoExameDao,
    private val clinicaDao: ClinicaDao,
    private val veterinarioDao: VeterinarioDao
) {

    /**
     * Obtém a lista de exames de um animal
     * Lança uma tarefa para atualizar os dados em background e retorna um Flow da BD local
     */
    fun getExames(token: String, animalId: Int): Flow<List<Exame>> {
        CoroutineScope(Dispatchers.IO).launch {
            refreshExames(token, animalId)
        }
        return exameDao.getExamesByAnimal(animalId)
    }

    /**
     * Força a atualização da lista de exames a partir da API
     * Apaga os exames antigos do animal e insere os novos
     */
    private suspend fun refreshExames(token: String, animalId: Int) {
        try {
            val response = apiService.getExamesDoAnimal("Bearer $token", animalId)
            if (response.isSuccessful) {
                response.body()?.let {
                    // limpa os dados antigos e insere os novos
                    exameDao.deleteByAnimal(animalId)
                    exameDao.insertAll(it.exames)
                }
            }
        } catch (e: Exception) {
            Log.e("HistoricoRepository", "Falha ao atualizar o histórico de exames", e)
        }
    }


    /**
     * Obtém a lista de todos os tipos de exame disponíveis
     */
    fun getTiposExame(): Flow<List<TipoExame>> {
        CoroutineScope(Dispatchers.IO).launch { refreshTiposExame() }
        return tipoExameDao.getAll()
    }

    /**
     * Atualiza a lista de tipos de exame a partir da API
     */
    private suspend fun refreshTiposExame() {
        try {
            val response = apiService.getTiposExame()
            if (response.isSuccessful) {
                response.body()?.tipos?.let {
                    tipoExameDao.clearAll()
                    tipoExameDao.insertAll(it)
                }
            }
        } catch (e: Exception) {
            Log.e("HistoricoRepository", "Falha ao obter tipos de exame", e)
        }
    }

    /**
     * Obtém a lista de todas as clínicas
     */
    fun getClinicas(): Flow<List<Clinica>> {
        CoroutineScope(Dispatchers.IO).launch { refreshClinicas() }
        return clinicaDao.getAllClinicas()
    }

    /**
     * Atualiza a lista de clínicas a partir da API
     */
    private suspend fun refreshClinicas() {
        try {
            val response = apiService.getClinicas()
            if (response.isSuccessful) {
                response.body()?.let {
                    clinicaDao.clearAll()
                    clinicaDao.insertAll(it)
                }
            }
        } catch (e: Exception) {
            Log.e("HistoricoRepository", "Falha ao obter clinicas", e)
        }
    }

    /**
     * Obtém a lista de veterinários de uma clínica específica
     */
    fun getVeterinariosPorClinica(clinicaId: Int): Flow<List<Veterinario>> {
        CoroutineScope(Dispatchers.IO).launch { refreshVeterinariosPorClinica(clinicaId) }
        return veterinarioDao.getVeterinariosByClinica(clinicaId)
    }

    /**
     * Atualiza a lista de veterinários de uma clínica a partir da API
     */
    private suspend fun refreshVeterinariosPorClinica(clinicaId: Int) {
        try {
            val response = apiService.getVeterinariosPorClinica(clinicaId)
            if (response.isSuccessful) {
                response.body()?.let {
                    veterinarioDao.deleteByClinica(clinicaId)
                    veterinarioDao.insertAll(it)
                }
            }
        } catch (e: Exception) {
            Log.e("HistoricoRepository", "Falha ao obter veterinarios", e)
        }
    }

    /**
     * Cria um novo exame através da API e atualiza a BD local
     */
    suspend fun createExame(token: String, request: CreateExameRequest): Result<CreateExameResponse> {
        return try {
            val response = apiService.createExame("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                // Após criar na API, força a atualização da lista local
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
     * Atualiza um exame existente através da API e atualiza a BD local
     */
    suspend fun updateExame(token: String, exameId: Int, request: UpdateExameRequest): Result<CreateExameResponse> {
        return try {
            val response = apiService.updateExame("Bearer $token", exameId, request)
            if (response.isSuccessful && response.body() != null) {
                val animalId = response.body()!!.exame.animalId
                // Após atualizar na API, força a atualização da lista local
                refreshExames(token, animalId)
                Result.success(response.body()!!)
            } else {
                Result.failure(IOException("Erro da API ao atualizar exame: ${response.code()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Envia uma foto para um exame existente na API e atualiza a BD local
     */
    suspend fun addFotoToExame(token: String, exameId: Int, animalId: Int, imageUri: Uri, context: Context): Result<AddExameFotoResponse> {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val requestBody = inputStream?.readBytes()?.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val part = requestBody?.let { MultipartBody.Part.createFormData("foto", "exame.jpg", it) }

            if (part != null) {
                val response = apiService.addFotoToExame("Bearer $token", exameId, part)
                if (response.isSuccessful && response.body() != null) {
                    // Após o upload, força a atualização da lista local para obter a nova URL
                    refreshExames(token, animalId)
                    Result.success(response.body()!!)
                } else {
                    Result.failure(IOException("Erro da API ao adicionar foto: ${response.code()} - ${response.errorBody()?.string()}"))
                }
            } else {
                Result.failure(IOException("Não foi possível ler a imagem"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Apaga um exame através da API e atualiza a BD local
     */
    suspend fun deleteExame(token: String, animalId: Int, exameId: Long): Result<Unit> {
        return try {
            val response = apiService.deleteExame("Bearer $token", exameId)
            if (response.isSuccessful) {
                // Após apagar na API, força a atualização da lista local
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
