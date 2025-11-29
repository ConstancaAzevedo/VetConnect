package pt.ipt.dam2025.trabalho.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.data.UserDao
import pt.ipt.dam2025.trabalho.model.NovoUsuario
import pt.ipt.dam2025.trabalho.model.User
import pt.ipt.dam2025.trabalho.model.Usuario

class UsuarioRepository(private val userDao: UserDao) {

    // Para a lista de utilizadores (UserListActivity)
    suspend fun getUsuarios(): List<Usuario> {
        return ApiClient.apiService.getUsuarios()
    }

    // Para o utilizador individual (PerfilTutorActivity)
    fun getUser(userId: Int): Flow<User?> = userDao.getUserById(userId)

    suspend fun updateUser(user: User) {
        try {
            // 1. Atualiza na API - Adapta o User local para o modelo da API (NovoUsuario)
            val updatedUserApi = NovoUsuario(
                nome = user.nome,
                email = user.email,
                telemovel = user.telemovel,
                tipo = "tutor" // Assumindo tipo
            )
            ApiClient.apiService.atualizarUsuario(user.id, updatedUserApi)

            // 2. Se a API não deu erro, atualiza na base de dados local
            userDao.insertOrUpdate(user)

        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Erro ao atualizar o utilizador", e)
            throw e
        }
    }

    suspend fun refreshUser(userId: Int) {
        try {
            val userFromApi = ApiClient.apiService.getUsuario(userId)
            // Mapeamento de Usuario (API) para User (Local DB)
            val userLocal = User(
                id = userFromApi.id,
                nome = userFromApi.nome,
                email = userFromApi.email,
                telemovel = userFromApi.telemovel
            )
            userDao.insertOrUpdate(userLocal)
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Erro ao refrescar os dados do utilizador", e)
        }
    }

    suspend fun criarUsuario(usuario: NovoUsuario): Result<Usuario> {
        return try {
            val response = ApiClient.apiService.criarUsuario(usuario)
            Result.success(response.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
