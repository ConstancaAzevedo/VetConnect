package pt.ipt.dam2025.trabalho.api

import okhttp3.MultipartBody
import pt.ipt.dam2025.trabalho.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface que define os endpoints da API
 */
interface ApiService {

    // autenticação e utilziadores==============================================
    @POST("usuarios")
    suspend fun criarUsuario(@Body usuario: NovoUsuario): Response<RegistrationResponse>

    @POST("usuarios/verificar")
    suspend fun verificarCodigo(@Body request: VerificationRequest): Response<VerificationResponse>

    @POST("usuarios/criar-pin")
    suspend fun criarPin(@Body request: CreatePinRequest): Response<CreatePinResponse>

    @POST("usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("usuarios")
    suspend fun getUsuarios(): Response<List<Usuario>>

    @GET("usuarios/{id}")
    suspend fun getUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Usuario>

    @PUT("usuarios/{id}")
    suspend fun updateUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body usuario: NovoUsuario
    ): Response<Unit>

    @DELETE("usuarios/{id}")
    suspend fun deleteUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    // animais==============================================
    @POST("animais")
    suspend fun createAnimal(
        @Header("Authorization") token: String,
        @Body animal: Animal
    ): Response<Animal>

    @GET("usuarios/{userId}/animais")
    suspend fun getAnimaisDoTutor(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<List<Animal>>

    @GET("animais/{animalId}")
    suspend fun getAnimal(
        @Header("Authorization") token: String,
        @Path("animalId") animalId: Int
    ): Response<Animal>

    @PUT("animais/{id}")
    suspend fun updateAnimal(
        @Header("Authorization") token: String,
        @Path("id") animalId: Int,
        @Body animal: Animal
    ): Response<Animal>

    @DELETE("animais/{id}")
    suspend fun deleteAnimal(
        @Header("Authorization") token: String,
        @Path("id") animalId: Int
    ): Response<Unit>

    @Multipart
    @POST("animais/{animalId}/foto")
    suspend fun uploadFoto(
        @Header("Authorization") token: String,
        @Path("animalId") animalId: Int,
        @Part("foto") foto: MultipartBody.Part
    ): Response<UploadFotoResponse>

    // documentos(histórico)==============================================
    @POST("documentos")
    suspend fun createDocumento(
        @Header("Authorization") token: String,
        @Body payload: QrCodePayload
    ): Response<Unit>

    @GET("animais/{animalId}/documentos")
    suspend fun getDocumentosDoAnimal(
        @Header("Authorization") token: String,
        @Path("animalId") animalId: Int
    ): Response<DocumentosResponse>

    // consultas==============================================
    @GET("clinicas")
    suspend fun getClinicas(): Response<List<Clinica>>

    @GET("clinicas/{clinicaId}/veterinarios")
    suspend fun getVeterinariosPorClinica(@Path("clinicaId") clinicaId: Int): Response<List<Veterinario>>

    @GET("veterinarios")
    suspend fun getVeterinarios(): Response<List<Veterinario>>

    @POST("consultas")
    suspend fun marcarConsulta(
        @Header("Authorization") token: String,
        @Body novaConsulta: NovaConsulta
    ): Response<Consulta>

    @GET("consultas/user/{userId}")
    suspend fun getConsultasDoUser(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<List<Consulta>>

    @DELETE("consultas/{id}")
    suspend fun cancelarConsulta(
        @Header("Authorization") token: String,
        @Path("id") consultaId: Int
    ): Response<Unit>
}