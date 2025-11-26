package pt.ipt.dam2025.trabalho.api

import pt.ipt.dam2025.trabalho.model.HistoricoItem
import pt.ipt.dam2025.trabalho.model.NovoUsuario
import pt.ipt.dam2025.trabalho.model.RegistrationResponse
import pt.ipt.dam2025.trabalho.model.Usuario
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- Rotas do Usuário ---
    @GET("usuarios")
    suspend fun getUsuarios(): List<Usuario>

    @GET("usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Int): Usuario

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("usuarios")
    suspend fun criarUsuario(@Body usuario: NovoUsuario): RegistrationResponse // <-- ALTERADO

    @PUT("usuarios/{id}")
    suspend fun atualizarUsuario(
        @Path("id") id: Int,
        @Body usuario: NovoUsuario
    ): Usuario

    @DELETE("usuarios/{id}")
    suspend fun deletarUsuario(@Path("id") id: Int): Response<Unit>

    // --- Rotas do Histórico ---
    @GET("historico")
    suspend fun getHistorico(): List<HistoricoItem>
}
