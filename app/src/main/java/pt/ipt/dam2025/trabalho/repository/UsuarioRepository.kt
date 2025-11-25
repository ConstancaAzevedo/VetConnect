package pt.ipt.dam2025.trabalho.repository

import pt.ipt.dam2025.trabalho.api.ApiClient
import pt.ipt.dam2025.trabalho.model.NovoUsuario
import pt.ipt.dam2025.trabalho.model.Usuario

class UsuarioRepository {
    // Obtém a instância do nosso serviço de API
    private val apiService = ApiClient.apiService

    /**
     * Obtém a lista de todos os usuários da API.
     * Em caso de erro, retorna uma lista vazia para evitar crashes.
     */
    suspend fun getUsuarios(): List<Usuario> {
        return try {
            apiService.getUsuarios()
        } catch (e: Exception) {
            // Logar o erro seria uma boa prática aqui
            emptyList()
        }
    }

    /**
     * Cria um novo usuário na API.
     * Retorna um objeto Result, que encapsula o sucesso ou a falha da operação.
     */
    suspend fun criarUsuario(usuario: NovoUsuario): Result<Usuario> {
        return try {
            val response = apiService.criarUsuario(usuario)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtém um usuário específico pelo seu ID.
     * Retorna nulo se o usuário não for encontrado ou se ocorrer um erro.
     */
    suspend fun getUsuarioPorId(id: Int): Usuario? {
        return try {
            apiService.getUsuario(id)
        } catch (e: Exception) {
            null
        }
    }
}
