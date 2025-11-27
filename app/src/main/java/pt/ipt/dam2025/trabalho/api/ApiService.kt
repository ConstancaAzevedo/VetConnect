package pt.ipt.dam2025.trabalho.api

import pt.ipt.dam2025.trabalho.model.CreatePinRequest
import pt.ipt.dam2025.trabalho.model.CreatePinResponse
import pt.ipt.dam2025.trabalho.model.HistoricoItem
import pt.ipt.dam2025.trabalho.model.LoginRequest
import pt.ipt.dam2025.trabalho.model.LoginResponse
import pt.ipt.dam2025.trabalho.model.NovoUsuario
import pt.ipt.dam2025.trabalho.model.RegistrationResponse
import pt.ipt.dam2025.trabalho.model.Usuario
import pt.ipt.dam2025.trabalho.model.VerificationRequest
import pt.ipt.dam2025.trabalho.model.VerificationResponse
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
    suspend fun criarUsuario(@Body usuario: NovoUsuario): RegistrationResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("usuarios/verificar")
    suspend fun verificarCodigo(@Body request: VerificationRequest): VerificationResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("usuarios/criar-pin")
    suspend fun criarPin(@Body request: CreatePinRequest): CreatePinResponse

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("usuarios/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

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
