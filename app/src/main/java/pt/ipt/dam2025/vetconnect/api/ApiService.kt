package pt.ipt.dam2025.vetconnect.api

import okhttp3.MultipartBody
import pt.ipt.dam2025.vetconnect.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface que define os endpoints da API para o Retrofit.
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

    @POST("usuarios/recuperar-pin")
    suspend fun recuperarPin(@Body request: RecuperarPinRequest): Response<RecuperarPinResponse>

    @POST("usuarios/redefinir-pin")
    suspend fun redefinirPin(@Body request: RedefinirPinRequest): Response<RedefinirPinResponse>

    @POST("usuarios/alterar-pin")
    suspend fun alterarPin(
        @Header("Authorization") token: String,
        @Body request: AlterarPinRequest
    ): Response<ChangePinResponse>

    @POST("usuarios/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<LogoutResponse>

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
        @Body usuario: UpdateUserRequest
    ): Response<GenericMessageResponse>

    @DELETE("usuarios/{id}")
    suspend fun deleteUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<GenericMessageResponse>

    // animais==============================================
    @POST("animais")
    suspend fun createAnimal(
        @Header("Authorization") token: String,
        @Body animal: CreateAnimalRequest
    ): Response<AnimalResponse>

    @DELETE("animais/{animalId}")
    suspend fun deleteAnimal(
        @Header("Authorization") token: String,
        @Path("animalId") animalId: Int
    ): Response<GenericMessageResponse>

    @GET("usuarios/{userId}/animais")
    suspend fun getAnimaisDoTutor(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<List<AnimalResponse>>

    @GET("animais/{animalId}")
    suspend fun getAnimal(
        @Header("Authorization") token: String,
        @Path("animalId") animalId: Int
    ): Response<AnimalResponse>

    @Multipart
    @POST("animais/{animalId}/foto")
    suspend fun uploadFoto(
        @Header("Authorization") token: String,
        @Path("animalId") animalId: Int,
        @Part foto: MultipartBody.Part
    ): Response<UploadResponse>

    // documentos(histórico)==============================================
    @POST("documentos")
    suspend fun createDocumento(
        @Header("Authorization") token: String,
        @Body payload: CreateDocumentRequest
    ): Response<CreateDocumentResponse>

    @GET("animais/{animalId}/documentos")
    suspend fun getDocumentosDoAnimal(
        @Header("Authorization") token: String,
        @Path("animalId") animalId: Int
    ): Response<DocumentosResponse>

    @DELETE("documentos/{tipo}/{id}")
    suspend fun deleteDocumento(
        @Header("Authorization") token: String,
        @Path("tipo") tipo: String,
        @Path("id") id: Long
    ): Response<DeleteDocumentResponse>

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
    ): Response<CancelConsultaResponse>

    // Vacinas ==============================================
    @GET("vacinas/tipos")
    suspend fun getTiposVacina(): Response<TiposVacinaResponse>

    @POST("vacinas/agendar")
    suspend fun agendarVacina(
        @Header("Authorization") token: String,
        @Body request: AgendarVacinaRequest
    ): Response<AgendarVacinaResponse>

    @GET("animais/{animalId}/vacinas/agendadas")
    suspend fun getVacinasAgendadas(
        @Header("Authorization") token: String,
        @Path("animalId") animalId: Int
    ): Response<VacinasAgendadasResponse>

    @PUT("vacinas/{id}")
    suspend fun updateVacina(
        @Header("Authorization") token: String,
        @Path("id") vacinaId: Int,
        @Body request: UpdateVacinaRequest
    ): Response<UpdateVacinaResponse>

    @DELETE("vacinas/{id}")
    suspend fun cancelarVacina(
        @Header("Authorization") token: String,
        @Path("id") vacinaId: Int
    ): Response<CancelVacinaResponse>

    @GET("vacinas/proximas")
    suspend fun getVacinasProximas(
        @Header("Authorization") token: String
    ): Response<VacinasProximasResponse>

    @POST("vacinas/{id}/realizada")
    suspend fun marcarVacinaRealizada(
        @Header("Authorization") token: String,
        @Path("id") vacinaId: Int,
        @Body request: MarcarVacinaRealizadaRequest
    ): Response<MarkVacinaRealizadaResponse>
}