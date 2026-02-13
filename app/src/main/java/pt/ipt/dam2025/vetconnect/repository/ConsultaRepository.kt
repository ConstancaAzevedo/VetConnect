package pt.ipt.dam2025.vetconnect.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pt.ipt.dam2025.vetconnect.api.ApiService
import pt.ipt.dam2025.vetconnect.data.AnimalDao
import pt.ipt.dam2025.vetconnect.data.ClinicaDao
import pt.ipt.dam2025.vetconnect.data.ConsultaDao
import pt.ipt.dam2025.vetconnect.data.VeterinarioDao
import pt.ipt.dam2025.vetconnect.model.AnimalResponse
import pt.ipt.dam2025.vetconnect.model.Clinica
import pt.ipt.dam2025.vetconnect.model.Consulta
import pt.ipt.dam2025.vetconnect.model.NovaConsulta
import pt.ipt.dam2025.vetconnect.model.Veterinario
import java.io.IOException

/**
 * Classe de repositório para consultas
 */
class ConsultaRepository(
    // Injeção de dependência do ApiService
    private val apiService: ApiService,
    private val consultaDao: ConsultaDao,
    private val clinicaDao: ClinicaDao,
    private val veterinarioDao: VeterinarioDao,
    private val animalDao: AnimalDao // DAO adicionado
) {

    /**
     * Obtém as consultas de um utilizador
     */
    fun getConsultas(token: String, userId: Int): Flow<List<Consulta>> {
        CoroutineScope(Dispatchers.IO).launch {
            refreshConsultas(token, userId)
        }
        return consultaDao.getConsultasByUser(userId)
    }

    private suspend fun refreshConsultas(token: String, userId: Int) {
        try {
            val response = apiService.getConsultasDoUser("Bearer $token", userId)
            if (response.isSuccessful) {
                response.body()?.let {
                    consultaDao.clearAndInsert(userId, it)
                }
            } else {
                Log.e("ConsultaRepository", "Erro ao refrescar consultas: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("ConsultaRepository", "Falha ao refrescar consultas", e)
        }
    }

    /**
     * Marca uma nova consulta através da API e guarda-a localmente.
     */
    suspend fun marcarConsulta(token: String, novaConsulta: NovaConsulta): Result<Consulta> {
        return try {
            val response = apiService.marcarConsulta("Bearer $token", novaConsulta)
            if (response.isSuccessful && response.body() != null) {
                val consultaCriada = response.body()!!
                consultaDao.insert(consultaCriada)
                Result.success(consultaCriada)
            } else {
                Result.failure(IOException("Erro ao marcar consulta: ${response.code()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cancela uma consulta na API e na base de dados local.
     */
    suspend fun cancelarConsulta(token: String, consultaId: Int): Result<Unit> {
        return try {
            val response = apiService.cancelarConsulta("Bearer $token", consultaId)
            if (response.isSuccessful) {
                consultaDao.deleteById(consultaId)
                Result.success(Unit)
            } else {
                Result.failure(IOException("Erro ao cancelar consulta: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Funções para obter listas para os Spinners ---

    fun getAnimaisDoTutor(token: String, userId: Int): Flow<List<AnimalResponse>> {
        CoroutineScope(Dispatchers.IO).launch { refreshAnimaisDoTutor(token, userId) }
        return animalDao.getAnimalsByTutorId(userId)
    }

    private suspend fun refreshAnimaisDoTutor(token: String, userId: Int) {
        try {
            val response = apiService.getAnimaisDoTutor("Bearer $token", userId)
            if (response.isSuccessful) {
                response.body()?.let { animalDao.insertAll(it) }
            }
        } catch (e: Exception) {
            Log.e("ConsultaRepository", "Falha ao obter animais do tutor", e)
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
                response.body()?.let { clinicaDao.insertAll(it) }
            }
        } catch (e: Exception) {
            Log.e("ConsultaRepository", "Falha ao obter clínicas", e)
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
                response.body()?.let { veterinarioDao.insertAll(it) }
            }
        } catch (e: Exception) {
            Log.e("ConsultaRepository", "Falha ao obter veterinários da clínica", e)
        }
    }
}
