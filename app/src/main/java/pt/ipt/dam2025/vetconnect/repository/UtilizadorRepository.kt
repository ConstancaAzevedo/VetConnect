package pt.ipt.dam2025.vetconnect.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.vetconnect.api.ApiService
import pt.ipt.dam2025.vetconnect.data.UserDao
import pt.ipt.dam2025.vetconnect.model.*
import java.io.IOException

/**
 * Repositório para gerir os dados dos utilizadores
 * Este ficheiro é a única fonte de verdade para os dados dos utilizadores
 * coordenando a interação entre a API (remoto) e o DAO (base de dados local)
 */
class UtilizadorRepository(
    // A dependência para o serviço da API que faz as chamadas de rede
    private val apiService: ApiService,
    // A dependência para o DAO do utilizador que acede à base de dados local
    private val userDao: UserDao
) {

    /**
     * Envia um pedido à API para obter a lista de todos os utilizadores registados
     */
    suspend fun getUtilizadores(): Result<List<Utilizador>> {
        return try {
            val response = apiService.getUtilizadores()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(IOException("Erro na API ao obter utilizadores ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Envia um pedido à API para criar um utilizador
     * Retorna um objeto Result que contém a resposta da API ou um erro
     */
    suspend fun criarUtilizador(utilizador: NovoUtilizador): Result<RegistrationResponse> {
        // O bloco try-catch lida com exceções de rede (ex: falta de internet)
        return try {
            // Chama o endpoint da API para criar o utilizador
            val response = apiService.criarUtilizador(utilizador)
            // Verifica se a resposta foi bem-sucedida e se tem um corpo
            if (response.isSuccessful && response.body() != null) {
                // Retorna sucesso com os dados da resposta
                Result.success(response.body()!!)
            } else {
                // Retorna uma falha com uma mensagem de erro detalhada
                Result.failure(IOException("Erro na API ao criar utilizador ${response.code()} ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            // Retorna uma falha se ocorrer uma exceção de rede
            Result.failure(e)
        }
    }

    /**
     * Envia um pedido à API para verificar o código de um novo utilizador
     */
    suspend fun verificarCodigo(request: VerificationRequest): Result<VerificationResponse> {
        return try {
            val response = apiService.verificarCodigo(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(IOException("Erro na API ao verificar código ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Envia um pedido à API para criar o PIN inicial do utilizador
     */
    suspend fun criarPin(request: CreatePinRequest): Result<CreatePinResponse> {
        return try {
            val response = apiService.criarPin(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(IOException("Erro da API ao criar PIN: ${response.code()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Tenta autenticar um utilizador na API
     * Se o login for bem-sucedido, guarda os dados do utilizador (incluindo o token) na base de dados local
     */
    suspend fun login(email: String, pin: String): Result<LoginResponse> {
        return try {
            // Cria o objeto de pedido para o login
            val request = LoginRequest(email, pin)
            // Chama o endpoint de login da API
            val response = apiService.login(request)
            if (response.isSuccessful && response.body() != null) {
                // O login foi bem-sucedido
                // Copia o token recebido para dentro do objeto Utilizador antes de o guardar
                val userToSave = response.body()!!.user.copy(token = response.body()!!.token)
                // Usa insertOrUpdate, que é mais seguro do que apagar tudo. Ele substitui o utilizador se já existir
                userDao.insert(userToSave)
                // Retorna sucesso com a resposta completa do login
                Result.success(response.body()!!)
            } else {
                // Retorna falha se a autenticação falhar (ex: PIN errado)
                Result.failure(IOException("Erro de autenticação: ${response.code()}"))
            }
        } catch (e: Exception) {
            // Retorna falha em caso de erro de rede
            Result.failure(e)
        }
    }

    /**
     * Envia um pedido à API para alterar o PIN de um utilizador já autenticado
     */
    suspend fun alterarPin(token: String, request: AlterarPinRequest): Result<Unit> {
        return try {
            val response = apiService.alterarPin("Bearer $token", request)
            if (response.isSuccessful) {
                // Retorna sucesso se a API confirmar a alteração
                Result.success(Unit)
            } else {
                // Retorna uma falha com uma mensagem de erro detalhada da API
                val errorMsg = response.errorBody()?.string() ?: "Erro desconhecido"
                Result.failure(IOException("Erro da API ao alterar PIN: ${response.code()} - $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Envia um pedido à API para terminar a sessão do utilizador (invalidar o token)
     */
    suspend fun logout(token: String): Result<Unit> {
        return try {
            val response = apiService.logout("Bearer $token")
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(IOException("Erro da API ao fazer logout: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    /**
     * Obtém os dados de um utilizador da base de dados local
     * Retorna um Flow que a UI irá receber atualizações automáticas
     */
    fun getUser(userId: Int): Flow<Utilizador?> = userDao.getUserById(userId)

    /**
     * Envia um pedido à API para atualizar os dados de um utilizador
     * Se for bem-sucedido, chama o refreshUser para sincronizar os novos dados com a base de dados local
     */
    suspend fun updateUser(token: String, userId: Int, request: UpdateUserRequest): Result<Unit> {
        return try {
            val response = apiService.updateUtilizador("Bearer $token", userId, request)
            if (response.isSuccessful) {
                // A API confirmou a atualização, agora vamos buscar os dados frescos
                refreshUser(token, userId)
                Result.success(Unit)
            } else {
                Result.failure(IOException("Falha ao atualizar dados no servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sincroniza os dados de um utilizador a partir da API e guarda-os na base de dados local
     * Esta função garante que os dados locais estão sempre atualizados com os do servidor
     */
    suspend fun refreshUser(token: String, userId: Int): Result<Unit> {
        return try {
            val response = apiService.getUtilizador("Bearer $token", userId)
            if (response.isSuccessful && response.body() != null) {
                val utilizadorDaApi = response.body()!!
                // O endpoint getUtilizador da API não retorna o token, por isso temos de o preservar
                val localUser = userDao.getUserByIdOnce(userId)
                val userToSave = utilizadorDaApi.copy(token = localUser?.token ?: token)
                // Insere/atualiza o utilizador na base de dados local
                userDao.insert(userToSave)
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("UtilizadorRepository", "Erro ao obter dados do utilizador: ${response.code()} - $errorBody")
                Result.failure(IOException("Erro ao obter dados do utilizador: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("UtilizadorRepository", "Falha na chamada para refrescar utilizador", e)
            Result.failure(e)
        }
    }
}
