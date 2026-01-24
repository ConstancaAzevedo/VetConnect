package pt.ipt.dam2025.trabalho.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pt.ipt.dam2025.trabalho.api.ApiService
import pt.ipt.dam2025.trabalho.data.ClinicaDao
import pt.ipt.dam2025.trabalho.data.ConsultaDao
import pt.ipt.dam2025.trabalho.data.VeterinarioDao
import pt.ipt.dam2025.trabalho.model.Clinica
import pt.ipt.dam2025.trabalho.model.Consulta
import pt.ipt.dam2025.trabalho.model.NovaConsulta
import pt.ipt.dam2025.trabalho.model.Veterinario
import java.io.IOException

// Classe de repositório para consultas
class ConsultaRepository(
    // Injeção de dependência do ApiService
    private val apiService: ApiService,
    private val consultaDao: ConsultaDao,
    private val clinicaDao: ClinicaDao,
    private val veterinarioDao: VeterinarioDao
) {

    /**
     * Obtém as consultas de um utilizador
     * Retorna um Flow da base de dados local e inicia uma atualização em background
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
     * Cancela uma consulta na API e, em caso de sucesso, na base de dados local.
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

    /**
     * Obtém a lista de todas as clínicas, com cache.
     */
    fun getClinicas(): Flow<List<Clinica>> {
        CoroutineScope(Dispatchers.IO).launch { refreshClinicas() }
        return clinicaDao.getAll()
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

    /**
     * Obtém a lista de todos os veterinários, com cache.
     */
    fun getVeterinarios(): Flow<List<Veterinario>> {
        CoroutineScope(Dispatchers.IO).launch { refreshVeterinarios() }
        return veterinarioDao.getAll()
    }

    private suspend fun refreshVeterinarios() {
        try {
            val response = apiService.getVeterinarios()
            if (response.isSuccessful) {
                response.body()?.let { veterinarioDao.insertAll(it) }
            }
        } catch (e: Exception) {
            Log.e("ConsultaRepository", "Falha ao obter veterinários", e)
        }
    }

    /**
     * Obtém os veterinários de uma clínica específica, com cache.
     */
    fun getVeterinariosPorClinica(clinicaId: Int): Flow<List<Veterinario>> {
        CoroutineScope(Dispatchers.IO).launch { refreshVeterinariosPorClinica(clinicaId) }
        return veterinarioDao.getByClinicaId(clinicaId)
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
