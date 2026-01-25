package pt.ipt.dam2025.trabalho.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import pt.ipt.dam2025.trabalho.api.ApiService
import pt.ipt.dam2025.trabalho.data.UserDao
import pt.ipt.dam2025.trabalho.model.*
import java.io.IOException

// Classe de repositório para utilizadores
class UsuarioRepository(
    private val apiService: ApiService,
    private val userDao: UserDao
) {

    /**
     * Obtém a lista de todos os utilizadores da API.
     */
    suspend fun getUsuarios(token: String): List<Usuario> {
        return try {
            val response = apiService.getUsuarios()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                Log.e("UsuarioRepository", "Erro ao obter utilizadores: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Falha na chamada para obter utilizadores", e)
            emptyList()
        }
    }

    /**
     * Cria um novo utilizador na API.
     */
    suspend fun criarUsuario(usuario: NovoUsuario): Result<RegistrationResponse> {
        return try {
            val response = apiService.criarUsuario(usuario)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(IOException("Erro na API ao criar utilizador: ${response.code()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Apaga um utilizador através da API e depois localmente.
     */
    suspend fun deletarUsuario(token: String, id: Int): Result<Unit> {
        return try {
            val response = apiService.deleteUsuario("Bearer $token", id)
            if (response.isSuccessful) {
                userDao.deleteById(id)
                Result.success(Unit)
            } else {
                Result.failure(IOException("Erro da API ao apagar utilizador: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Funções para o Perfil do Tutor (Base de Dados Local + API) ---

    /**
     * Obtém os dados de um utilizador da base de dados local.
     */
    fun getUser(userId: Int): Flow<Usuario?> = userDao.getUserById(userId)

    /**
     * Atualiza os dados de um utilizador na API e depois na base de dados local.
     */
    suspend fun updateUser(token: String, userId: Int, request: UpdateUserRequest): Result<Unit> {
        return try {
            val response = apiService.updateUsuario("Bearer $token", userId, request)

            if (response.isSuccessful) {
                // Se a API atualizou com sucesso, atualiza também a base de dados local.
                refreshUser(token, userId) // Força a atualização para obter todos os dados
                Result.success(Unit)
            } else {
                Result.failure(IOException("Falha ao atualizar dados no servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

   /**
     * Sincroniza os dados de um utilizador a partir da API e guarda-os na base de dados local.
     */
    suspend fun refreshUser(token: String, userId: Int): Result<Unit> {
        return try {
            val response = apiService.getUsuario("Bearer $token", userId)
            if (response.isSuccessful) {
                response.body()?.let { usuarioDaApi ->
                    // O token não vem da API, por isso precisamos de o preservar
                    val localUser = userDao.getUserByIdOnce(userId)
                    val userToSave = usuarioDaApi.copy(token = localUser?.token ?: token)
                    userDao.insertOrUpdate(userToSave)
                    Result.success(Unit)
                } ?: Result.failure(IOException("O corpo da resposta da API ao refrescar o utilizador está vazio."))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("UsuarioRepository", "Erro ao obter dados do utilizador: ${response.code()} - $errorBody")
                Result.failure(IOException("Erro ao obter dados do utilizador: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Falha na chamada para refrescar utilizador", e)
            Result.failure(e)
        }
    }
}
