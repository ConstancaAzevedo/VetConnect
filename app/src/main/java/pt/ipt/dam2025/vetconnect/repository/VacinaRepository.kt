package pt.ipt.dam2025.vetconnect.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.api.ApiService
import pt.ipt.dam2025.vetconnect.data.TipoVacinaDao
import pt.ipt.dam2025.vetconnect.data.VacinaDao
import pt.ipt.dam2025.vetconnect.model.AgendarVacinaRequest
import pt.ipt.dam2025.vetconnect.model.TipoVacina
import pt.ipt.dam2025.vetconnect.model.UpdateVacinaRequest
import pt.ipt.dam2025.vetconnect.model.Vacina
import java.io.IOException

class VacinaRepository(
    private val apiService: ApiService,
    private val vacinaDao: VacinaDao,
    private val tipoVacinaDao: TipoVacinaDao
) {

    fun getVacinas(token: String, animalId: Int): Flow<List<Vacina>> {
        CoroutineScope(Dispatchers.IO).launch {
            refreshVacinas(token, animalId)
        }
        return vacinaDao.getVacinasByAnimal(animalId)
    }

    private suspend fun refreshVacinas(token: String, animalId: Int) {
        try {
            val response = apiService.getVacinasAgendadas("Bearer $token", animalId)
            if (response.isSuccessful) {
                response.body()?.vacinas?.let { vacinasDaApi ->
                    vacinaDao.deleteByAnimal(animalId)
                    vacinaDao.insertAll(vacinasDaApi)
                }
            } else {
                Log.e("VacinaRepository", "Erro ao refrescar vacinas: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("VacinaRepository", "Falha de rede ao refrescar vacinas", e)
        }
    }

    fun getTiposVacina(): Flow<List<TipoVacina>> = flow {
        // Emite os dados locais primeiro
        val tiposLocais = tipoVacinaDao.getTiposVacina()
        emit(tiposLocais)

        // Depois vai à API buscar dados frescos
        try {
            val response = apiService.getTiposVacina()
            if (response.isSuccessful) {
                response.body()?.tipos?.let {
                    tipoVacinaDao.clearAll()
                    tipoVacinaDao.insertAll(it)
                    emit(it) // Emite os novos dados
                }
            }
        } catch (e: Exception) {
            Log.e("VacinaRepository", "Falha ao buscar tipos de vacina da API", e)
        }
    }

    suspend fun agendarVacina(token: String, request: AgendarVacinaRequest): Result<Unit> {
        return try {
            val response = apiService.agendarVacina("Bearer $token", request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(IOException("Erro da API ao agendar vacina: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateVacina(token: String, vacinaId: Int, request: UpdateVacinaRequest): Result<Unit> {
        return try {
            val response = apiService.updateVacina("Bearer $token", vacinaId, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(IOException("Erro da API ao atualizar vacina: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelarVacina(token: String, vacinaId: Int): Result<Unit> {
        return try {
            val response = apiService.cancelarVacina("Bearer $token", vacinaId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(IOException("Erro da API ao cancelar vacina: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
