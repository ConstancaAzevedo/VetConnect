package pt.ipt.dam2025.trabalho.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.data.UserDao
import pt.ipt.dam2025.trabalho.model.NovoUsuario
import pt.ipt.dam2025.trabalho.model.User
import pt.ipt.dam2025.trabalho.model.Usuario

class UsuarioRepository(private val userDao: UserDao) {

    // --- Funções para a UserListActivity ---

    suspend fun getUsuarios(): List<Usuario> {
        return ApiClient.apiService.getUsuarios()
    }

    suspend fun criarUsuario(usuario: NovoUsuario): Result<Usuario> {
        return try {
            val response = ApiClient.apiService.criarUsuario(usuario)
            Result.success(response.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletarUsuario(id: Int) {
        ApiClient.apiService.deletarUsuario(id)
    }

    // --- Funções para o Perfil do Tutor ---

    fun getUser(userId: Int): Flow<User?> = userDao.getUserById(userId)

    suspend fun updateUser(user: User) {
        try {
            val updatedUserApi = NovoUsuario(
                nome = user.nome,
                email = user.email,
                tipo = "tutor" // Assumindo tipo, pode necessitar de ajuste
            )
            ApiClient.apiService.atualizarUsuario(user.id, updatedUserApi)
            userDao.insertOrUpdate(user)
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Erro ao atualizar o utilizador", e)
            throw e
        }
    }

    suspend fun refreshUser(userId: Int) {
        try {
            val userFromApi = ApiClient.apiService.getUsuario(userId)
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
}
