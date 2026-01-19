package pt.ipt.dam2025.trabalho.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.data.UserDao
import pt.ipt.dam2025.trabalho.model.NovoUsuario
import pt.ipt.dam2025.trabalho.model.User
import pt.ipt.dam2025.trabalho.model.Usuario
import java.io.IOException

class UsuarioRepository(private val userDao: UserDao) {

    // --- Funções para a UserListActivity (Exemplo de CRUD) ---

    /**
     * Obtém a lista de todos os utilizadores da API.
     */
    suspend fun getUsuarios(token: String): List<Usuario> {
        return try {
            val response = ApiClient.apiService.getUsuarios("Bearer $token")
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
     * Cria um novo utilizador através da API.
     */
    suspend fun criarUsuario(usuario: NovoUsuario): Result<Usuario> {
        return try {
            val response = ApiClient.apiService.criarUsuario(usuario)
            if (response.isSuccessful) {
                response.body()?.user?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Resposta da API nula ou inválida no registo"))
            } else {
                Result.failure(Exception("Erro na API ao criar utilizador: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Apaga um utilizador através da API.
     */
    suspend fun deletarUsuario(token: String, id: Int) {
        try {
            val response = ApiClient.apiService.deleteUsuario("Bearer $token", id)
            if (!response.isSuccessful) {
                Log.e("UsuarioRepository", "Erro ao apagar utilizador: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Falha na chamada para apagar utilizador", e)
        }
    }

    // --- Funções para o Perfil do Tutor (Base de Dados Local + API) ---

    /**
     * Obtém os dados de um utilizador da base de dados local.
     */
    fun getUser(userId: Int): Flow<User?> = userDao.getUserById(userId)

    /**
     * Atualiza os dados de um utilizador na API e depois na base de dados local.
     */
    suspend fun updateUser(token: String, user: User) {
        try {
            val updatedUserApi = NovoUsuario(
                nome = user.nome,
                email = user.email,
                telemovel = user.telemovel ?: "",
                tipo = "tutor" // O tipo pode não ser editável, mas é necessário para o modelo
            )

            val response = ApiClient.apiService.updateUsuario("Bearer $token", user.id, updatedUserApi)

            if (response.isSuccessful) {
                // Se a API atualizou com sucesso, atualiza também a base de dados local.
                userDao.insertOrUpdate(user)
            } else {
                Log.e("UsuarioRepository", "Erro ao atualizar utilizador na API: ${response.code()}")
                throw IOException("Falha ao atualizar dados no servidor.")
            }
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Falha na chamada para atualizar utilizador", e)
            throw e
        }
    }

    /**
     * Sincroniza os dados de um utilizador da API para a base de dados local.
     */
    suspend fun refreshUser(token: String, userId: Int) {
        try {
            val response = ApiClient.apiService.getUsuario("Bearer $token", userId)
            if (response.isSuccessful) {
                response.body()?.let { usuarioDaApi ->
                    val userLocal = User(
                        id = usuarioDaApi.id,
                        nome = usuarioDaApi.nome,
                        email = usuarioDaApi.email,
                        telemovel = usuarioDaApi.telemovel
                    )
                    userDao.insertOrUpdate(userLocal)
                }
            } else {
                Log.e("UsuarioRepository", "Erro ao obter dados do utilizador: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Falha na chamada para refrescar utilizador", e)
        }
    }
}