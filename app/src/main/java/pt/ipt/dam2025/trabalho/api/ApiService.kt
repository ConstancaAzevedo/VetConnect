package pt.ipt.dam2025.trabalho.api

import pt.ipt.dam2025.trabalho.model.HistoricoItem
import pt.ipt.dam2025.trabalho.model.NovoUsuario
import pt.ipt.dam2025.trabalho.model.Usuario
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- Rotas de Usuários ---
    @GET("usuarios")
    suspend fun getUsuarios(): List<Usuario>

    @GET("usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Int): Usuario

    @POST("usuarios")
    suspend fun criarUsuario(@Body usuario: NovoUsuario): Usuario

    @PUT("usuarios/{id}")
    suspend fun atualizarUsuario(
        @Path("id") id: Int,
        @Body usuario: NovoUsuario
    ): Usuario

    @DELETE("usuarios/{id}")
    suspend fun deletarUsuario(@Path("id") id: Int): Response<Unit>

    // --- Rotas de Histórico ---
    @GET("historico")
    suspend fun getHistorico(): List<HistoricoItem>
}
