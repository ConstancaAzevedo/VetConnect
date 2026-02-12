package pt.ipt.dam2025.vetconnect.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.api.ApiService
import pt.ipt.dam2025.vetconnect.data.ClinicaDao
import pt.ipt.dam2025.vetconnect.data.TipoVacinaDao
import pt.ipt.dam2025.vetconnect.data.VacinaDao
import pt.ipt.dam2025.vetconnect.data.VeterinarioDao
import pt.ipt.dam2025.vetconnect.model.*
import java.io.IOException

class VacinaRepository(
    private val apiService: ApiService,
    private val vacinaDao: VacinaDao,
    private val tipoVacinaDao: TipoVacinaDao,
    private val clinicaDao: ClinicaDao,
    private val veterinarioDao: VeterinarioDao
) {

    // Função para obter vacinas de um animal específico
    fun getVacinas(token: String, animalId: Int): Flow<List<Vacina>> {
        CoroutineScope(Dispatchers.IO).launch {
            refreshVacinas(token, animalId)
        }
        return vacinaDao.getVacinasByAnimal(animalId)
    }

    // Função para obter TODAS as vacinas do utilizador
    fun getAllUserVacinas(token: String): Flow<List<Vacina>> {
        CoroutineScope(Dispatchers.IO).launch {
            refreshAllUserVacinas(token)
        }
        return vacinaDao.getAllVacinas()
    }

    // Refresh para as vacinas de um animal específico
    private suspend fun refreshVacinas(token: String, animalId: Int) {
        try {
            val response = apiService.getVacinasAgendadas("Bearer $token", animalId)
            if (response.isSuccessful) {
                response.body()?.vacinas?.let {
                    vacinaDao.deleteByAnimal(animalId)
                    vacinaDao.insertAll(it)
                }
            }
        } catch (e: Exception) {
            Log.e("VacinaRepository", "Falha de rede ao refrescar vacinas do animal", e)
        }
    }

    // Refresh para TODAS as vacinas do utilizador
    private suspend fun refreshAllUserVacinas(token: String) {
        try {
            val response = apiService.getVacinas("Bearer $token")
            if (response.isSuccessful) {
                response.body()?.vacinas?.let { vacinas ->
                    vacinaDao.clearAll()
                    vacinaDao.insertAll(vacinas)
                }
            } else {
                Log.e("VacinaRepository", "Erro ao refrescar todas as vacinas: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("VacinaRepository", "Falha de rede ao refrescar todas as vacinas", e)
        }
    }

    fun getTiposVacina(): Flow<List<TipoVacina>> {
        CoroutineScope(Dispatchers.IO).launch { refreshTiposVacina() }
        return tipoVacinaDao.getTiposVacina()
    }

    private suspend fun refreshTiposVacina() {
        try {
            val response = apiService.getTiposVacina()
            if (response.isSuccessful) {
                response.body()?.tipos?.let {
                    tipoVacinaDao.clearAll()
                    tipoVacinaDao.insertAll(it)
                }
            }
        } catch (e: Exception) {
            Log.e("VacinaRepository", "Falha ao buscar tipos de vacina da API", e)
        }
    }

    fun getClinicas(): Flow<List<Clinica>> {
        CoroutineScope(Dispatchers.IO).launch { refreshClinicas() }
        return clinicaDao.getAllClinicas()
    }

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
            Log.e("VacinaRepository", "Falha ao buscar clinicas da API", e)
        }
    }

    fun getVeterinariosPorClinica(clinicaId: Int): Flow<List<Veterinario>> {
        CoroutineScope(Dispatchers.IO).launch { refreshVeterinariosPorClinica(clinicaId) }
        return veterinarioDao.getVeterinariosByClinica(clinicaId)
    }

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
            Log.e("VacinaRepository", "Falha ao buscar veterinarios da API", e)
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