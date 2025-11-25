package pt.ipt.dam2025.trabalho.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    /**
     * Envia os dados de um novo utilizador para a API.
     * @param novoUsuario O objeto com os dados do utilizador a ser criado.
     * @return A resposta da API, que pode ser o utilizador criado ou uma confirmação.
     */
    @POST("users") // Substitua "users" pelo endpoint real da sua API para criar utilizadores
    // enviar os dados de um NovoUsuario para o servidor
    suspend fun createUser(@Body novoUsuario: NovoUsuario): Response<Usuario>
}
