package pt.ipt.dam2025.vetconnect.repository

import android.util.Log // Importa a classe Log para registar mensagens de erro no Logcat
import kotlinx.coroutines.CoroutineScope // Importa para criar um escopo de coroutines que podem ser canceladas
import kotlinx.coroutines.Dispatchers // Importa os dispatchers para escolher a thread correta para o trabalho (IO para rede/disco)
import kotlinx.coroutines.flow.Flow // Importa a classe Flow para criar streams de dados que emitem valores de forma assíncrona
import kotlinx.coroutines.launch // Importa a função para iniciar uma nova coroutine sem bloquear a thread atual
import pt.ipt.dam2025.vetconnect.api.ApiService
import pt.ipt.dam2025.vetconnect.data.*
import pt.ipt.dam2025.vetconnect.model.*
import java.io.IOException // Importa a exceção para erros de Input/Output (ex: falhas de rede)

/**
 * Repositório para gerir os dados das Vacinas
 * Este ficheiro é a única fonte de verdade para os dados das vacinas na aplicação
 * Ele orquestra de onde os dados vêm (da API ou da base de dados local)
 * e como são guardados
 */
class VacinaRepository(
    private val apiService: ApiService,
    private val vacinaDao: VacinaDao,
    private val tipoVacinaDao: TipoVacinaDao,
    private val clinicaDao: ClinicaDao,
    private val veterinarioDao: VeterinarioDao
) {

    /**
     * Obtém a lista de vacinas de um animal específico
     * Usa uma estratégia de "cache offline" A UI recebe os dados da BD local instantaneamente
     * enquanto uma tarefa em background vai buscar os dados mais recentes à API
     */
    fun getVacinas(token: String, animalId: Int): Flow<List<Vacina>> {
        // Inicia uma coroutine numa thread de I/O (Input/Output) para não bloquear a UI
        CoroutineScope(Dispatchers.IO).launch {
            // Chama a função que vai buscar os dados frescos à API e os guarda na BD
            refreshVacinas(token, animalId)
        }
        // Retorna imediatamente o Flow do DAO A UI observa este Flow e atualiza-se sozinha
        return vacinaDao.getVacinasByAnimal(animalId)
    }

    /**
     * Função privada que força a atualização da lista de vacinas de um animal a partir da API
     * Esta função é suspensa o que significa que só pode ser chamada de dentro de uma coroutine
     */
    private suspend fun refreshVacinas(token: String, animalId: Int) {
        try {
            // Chama o endpoint da API para obter a lista de vacinas do animal
            val response = apiService.getVacinasAgendadas("Bearer $token", animalId)
            // Se a chamada de rede foi bem-sucedida (código 2xx)
            if (response.isSuccessful) {
                // E se o corpo da resposta não for nulo
                response.body()?.vacinas?.let {
                    // Primeiro apaga as vacinas antigas do animal na BD local
                    vacinaDao.deleteByAnimal(animalId)
                    // Depois insere a nova lista de vacinas recebida da API
                    vacinaDao.insert(it)
                }
            }
        } catch (e: Exception) {
            // Se ocorrer uma exceção (ex: sem internet) regista um erro no Logcat
            Log.e("VacinaRepository", "Falha de rede ao refrescar vacinas do animal", e)
        }
    }


    /**
     * Obtém a lista de todos os tipos de vacina disponíveis
     * Segue a mesma estratégia de cache offline
     */
    fun getTiposVacina(): Flow<List<TipoVacina>> {
        CoroutineScope(Dispatchers.IO).launch { refreshTiposVacina() }
        return tipoVacinaDao.getTiposVacina()
    }

    /**
     * Atualiza a lista de tipos de vacina a partir da API
     */
    private suspend fun refreshTiposVacina() {
        try {
            val response = apiService.getTiposVacina()
            if (response.isSuccessful) {
                response.body()?.tipos?.let {
                    tipoVacinaDao.clearAll() // Apaga todos os tipos de vacina antigos
                    tipoVacinaDao.insertAll(it) // Insere os novos
                }
            }
        } catch (e: Exception) {
            Log.e("VacinaRepository", "Falha ao buscar tipos de vacina da API", e)
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
            Log.e("VacinaRepository", "Falha ao buscar clinicas da API", e)
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
            Log.e("VacinaRepository", "Falha ao buscar veterinarios da API", e)
        }
    }

    suspend fun getVacinasProximas(token: String): Result<VacinasProximasResponse> {
        return try {
            val response = apiService.getVacinasProximas("Bearer $token")
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(IOException("Erro ao obter vacinas próximas: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Envia um pedido à API para agendar uma nova vacina
     * Retorna um Result para o ViewModel saber se a operação teve sucesso ou falhou
     */
    suspend fun agendarVacina(token: String, request: AgendarVacinaRequest): Result<Unit> {
        return try {
            val response = apiService.agendarVacina("Bearer $token", request)
            if (response.isSuccessful) {
                Result.success(Unit) // Retorna sucesso se a API confirmar
            } else {
                // Retorna falha se a API devolver um erro
                Result.failure(IOException("Erro da API ao agendar vacina: ${response.code()}"))
            }
        } catch (e: Exception) {
            // Retorna falha se houver um erro de rede
            Result.failure(e)
        }
    }

    /**
     * Marca uma vacina como realizada através da API
     */
    suspend fun marcarVacinaRealizada(token: String, vacinaId: Int, request: MarcarVacinaRealizadaRequest): Result<Unit> {
        return try {
            val response = apiService.marcarVacinaRealizada("Bearer $token", vacinaId, request)
            if (response.isSuccessful) {
                // Opcional: podemos forçar um refresh da lista de vacinas aqui
                Result.success(Unit)
            } else {
                Result.failure(IOException("Erro da API ao marcar como realizada: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Envia um pedido à API para atualizar uma vacina existente
     */
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

    /**
     * Envia um pedido à API para cancelar uma vacina existente
     */
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
