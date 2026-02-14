package pt.ipt.dam2025.vetconnect.repository

import android.util.Log // Importa a classe Log para registar mensagens de erro
import kotlinx.coroutines.CoroutineScope // Importa para criar um escopo de coroutines
import kotlinx.coroutines.Dispatchers // Importa os dispatchers para definir a thread (ex: IO para rede/disco)
import kotlinx.coroutines.flow.Flow // Importa a classe Flow para streams de dados assíncronos
import kotlinx.coroutines.launch // Importa a função para iniciar uma coroutine
import pt.ipt.dam2025.vetconnect.api.ApiService
import pt.ipt.dam2025.vetconnect.data.*
import pt.ipt.dam2025.vetconnect.model.*
import java.io.IOException // Importa a exceção para erros de Input/Output

/**
 * Repositório para gerir os dados das Consultas
 * É a única fonte de verdade para os dados das consultas coordenando a API e a base de dados local
 */
class ConsultaRepository(
    private val apiService: ApiService,
    private val consultaDao: ConsultaDao,
    private val clinicaDao: ClinicaDao,
    private val veterinarioDao: VeterinarioDao,
) {

    /**
     * Obtém a lista de consultas de um utilizador
     * Lança uma tarefa em background para ir buscar os dados mais recentes à API
     * e retorna imediatamente um Flow da base de dados local para a UI observar
     */
    fun getConsultas(token: String, userId: Int): Flow<List<Consulta>> {
        // Inicia uma coroutine numa thread de I/O para não bloquear a UI
        CoroutineScope(Dispatchers.IO).launch {
            // Chama a função que vai buscar os dados à API e os guarda na BD
            refreshConsultas(token, userId)
        }
        // Retorna o Flow do DAO A UI irá receber os dados da BD imediatamente
        // e depois receberá uma nova lista quando o refresh da API terminar
        return consultaDao.getConsultasByUser(userId)
    }

    /**
     * Função privada que força a atualização da lista de consultas a partir da API
     */
    private suspend fun refreshConsultas(token: String, userId: Int) {
        try {
            // Chama o endpoint da API para obter a lista de consultas
            val response = apiService.getConsultasDoUser("Bearer $token", userId)
            // Se a resposta for bem-sucedida
            if (response.isSuccessful) {
                // E se o corpo da resposta não for nulo
                response.body()?.let {
                    // Apaga as consultas antigas e insere as novas numa única transação
                    consultaDao.clearAndInsert(userId, it)
                }
            } else {
                // Regista um erro se a chamada à API falhar
                Log.e("ConsultaRepository", "Erro ao refrescar consultas: ${response.code()}")
            }
        } catch (e: Exception) {
            // Regista uma exceção de rede (ex: sem internet)
            Log.e("ConsultaRepository", "Falha ao refrescar consultas", e)
        }
    }

    /**
     * Marca uma nova consulta através da API e se for bem-sucedido guarda-a localmente
     */
    suspend fun marcarConsulta(token: String, novaConsulta: NovaConsulta): Result<Consulta> {
        return try {
            val response = apiService.marcarConsulta("Bearer $token", novaConsulta)
            if (response.isSuccessful && response.body() != null) {
                val consultaCriada = response.body()!!
                // Insere a nova consulta na base de dados local para a UI atualizar
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
     * Cancela uma consulta na API e se for bem-sucedido apaga-a da base de dados local
     */
    suspend fun cancelarConsulta(token: String, consultaId: Int): Result<Unit> {
        return try {
            val response = apiService.cancelarConsulta("Bearer $token", consultaId)
            if (response.isSuccessful) {
                // Apaga a consulta da base de dados local
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
     * Atualiza uma consulta existente através da API
     * e em caso de sucesso atualiza-a na base de dados local
     */
    suspend fun updateConsulta(
        token: String,
        id: Int,
        request: UpdateConsultaRequest
    ): Result<Consulta> {
        return try {
            val response = apiService.updateConsulta("Bearer $token", id, request)
            if (response.isSuccessful && response.body() != null) {
                val consultaAtualizada = response.body()!!
                // O insert com OnConflictStrategy.REPLACE funciona como um update
                consultaDao.insert(consultaAtualizada)
                Result.success(consultaAtualizada)
            } else {
                Result.failure(IOException("Erro ao atualizar consulta: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    /**
     * Obtém a lista de todas as clínicas
     * A UI recebe os dados da BD local e o repositório atualiza-os em background
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
                response.body()?.let { clinicaDao.insertAll(it) }
            }
        } catch (e: Exception) {
            Log.e("ConsultaRepository", "Falha ao obter clínicas", e)
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
                response.body()?.let { veterinarioDao.insertAll(it) }
            }
        } catch (e: Exception) {
            Log.e("ConsultaRepository", "Falha ao obter veterinários da clínica", e)
        }
    }
}
