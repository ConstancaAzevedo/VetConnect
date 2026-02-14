package pt.ipt.dam2025.vetconnect.api

import okhttp3.MultipartBody // Importa para construir pedidos de upload de ficheiros
import pt.ipt.dam2025.vetconnect.model.* // Importa todos os modelos de dados
import retrofit2.Response // Importa a classe Response do Retrofit
import retrofit2.http.* // Importa todas as anotações do Retrofit

/**
 * Interface que define todos os endpoints da API para o Retrofit
 * O Retrofit usa esta interface para gerar o código de comunicação com a rede
 */
interface ApiService {

    // --- AUTENTICAÇÃO E UTILIZADORES ---

    /**
     * Cria um novo utilizador
     */
    @POST("utilizadores")
    suspend fun criarUtilizador(@Body utilizador: NovoUtilizador): Response<RegistrationResponse>

    /**
     * Envia o código de verificação para validar um novo utilizador
     */
    @POST("utilizadores/verificar")
    suspend fun verificarCodigo(@Body request: VerificationRequest): Response<VerificationResponse>

    /**
     * Cria o PIN inicial para um utilizador
     */
    @POST("utilizadores/criar-pin")
    suspend fun criarPin(@Body request: CreatePinRequest): Response<CreatePinResponse>

    /**
     * Efetua o login de um utilizador
     */
    @POST("utilizadores/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>


    /**
     * Altera o PIN de um utilizador autenticado
     */
    @POST("utilizadores/alterar-pin")
    suspend fun alterarPin(
        @Header("Authorization") token: String,
        @Body request: AlterarPinRequest
    ): Response<ChangePinResponse>

    /**
     * Termina a sessão do utilizador (invalida o token no servidor)
     */
    @POST("utilizadores/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<LogoutResponse>

    /**
     * Obtém a lista de todos os utilizadores
     */
    @GET("utilizadores")
    suspend fun getUtilizadores(): Response<List<Utilizador>>

    /**
     * Obtém os dados de um utilizador específico pelo seu ID
     */
    @GET("utilizadores/{id}")
    suspend fun getUtilizador(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Utilizador>

    /**
     * Atualiza os dados de um utilizador
     */
    @PUT("utilizadores/{id}")
    suspend fun updateUtilizador(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body utilizador: UpdateUserRequest
    ): Response<GenericMessageResponse>


    // --- ANIMAIS ---

    /**
     * Cria um novo animal para o utilizador autenticado
     */
    @POST("animais")
    suspend fun createAnimal(
        @Header("Authorization") token: String,
        @Body animal: CreateAnimalRequest
    ): Response<AnimalResponse>

    /**
     * Atualiza os dados de um animal existente
     */
    @PUT("animais/{id}")
    suspend fun updateAnimal(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body animal: CreateAnimalRequest
    ): Response<UpdateAnimalResponse>

    /**
     * Obtém a lista de animais de um tutor específico
     */
    @GET("utilizadores/{userId}/animais")
    suspend fun getAnimaisDoTutor(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<List<AnimalResponse>>

    /**
     * Obtém os detalhes de um animal específico
     */
    @GET("animais/{animalId}")
    suspend fun getAnimal(
        @Header("Authorization") token: String,
        @Path("animalId") animalId: Int
    ): Response<AnimalResponse>

    /**
     * Faz o upload da foto de um animal
     */
    @Multipart
    @POST("animais/{animalId}/foto")
    suspend fun uploadFoto(
        @Header("Authorization") token: String,
        @Path("animalId") animalId: Int,
        @Part foto: MultipartBody.Part
    ): Response<UploadResponse>

    // --- HISTÓRICO (EXAMES) ---

    /**
     * Obtém a lista de todos os tipos de exame disponíveis
     */
    @GET("exames/tipos")
    suspend fun getTiposExame(): Response<TiposExameResponse>

    /**
     * Cria um novo registo de exame
     */
    @POST("exames")
    suspend fun createExame(
        @Header("Authorization") token: String,
        @Body payload: CreateExameRequest
    ): Response<CreateExameResponse>

    /**
     * Adiciona uma foto a um exame existente
     */
    @Multipart
    @POST("exames/{id}/foto")
    suspend fun addFotoToExame(
        @Header("Authorization") token: String,
        @Path("id") exameId: Int,
        @Part foto: MultipartBody.Part
    ): Response<AddExameFotoResponse>

    /**
     * Obtém todos os exames de um animal específico
     */
    @GET("animais/{animalId}/exames")
    suspend fun getExamesDoAnimal(
        @Header("Authorization") token: String,
        @Path("animalId") animalId: Int
    ): Response<ExamesResponse>

    /**
     * Atualiza um exame existente
     */
    @PUT("exames/{id}")
    suspend fun updateExame(
        @Header("Authorization") token: String,
        @Path("id") exameId: Int,
        @Body payload: UpdateExameRequest
    ): Response<CreateExameResponse>

    /**
     * Apaga um exame
     */
    @DELETE("exames/{id}")
    suspend fun deleteExame(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<GenericSuccessResponse>

    // --- CONSULTAS ---

    /**
     * Obtém a lista de todas as clínicas
     */
    @GET("clinicas")
    suspend fun getClinicas(): Response<List<Clinica>>

    /**
     * Obtém os veterinários de uma clínica específica
     */
    @GET("clinicas/{clinicaId}/veterinarios")
    suspend fun getVeterinariosPorClinica(@Path("clinicaId") clinicaId: Int): Response<List<Veterinario>>

    /**
     * Obtém a lista de todos os veterinários
     */
    @GET("veterinarios")
    suspend fun getVeterinarios(): Response<List<Veterinario>>

    /**
     * Marca uma nova consulta
     */
    @POST("consultas")
    suspend fun marcarConsulta(
        @Header("Authorization") token: String,
        @Body novaConsulta: NovaConsulta
    ): Response<Consulta>

    /**
     * Obtém as consultas de um utilizador específico
     */
    @GET("consultas/user/{userId}")
    suspend fun getConsultasDoUser(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<List<Consulta>>

    /**
     * Cancela uma consulta
     */
    @DELETE("consultas/{id}")
    suspend fun cancelarConsulta(
        @Header("Authorization") token: String,
        @Path("id") consultaId: Int
    ): Response<CancelConsultaResponse>

    /**
     * Atualiza uma consulta
     */
    @PUT("consultas/{id}")
    suspend fun updateConsulta(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body body: UpdateConsultaRequest
    ): Response<Consulta>

    // --- VACINAS ---

    /**
     * Obtém a lista de todos os tipos de vacina disponíveis
     */
    @GET("vacinas/tipos")
    suspend fun getTiposVacina(): Response<TiposVacinaResponse>

    /**
     * Agenda uma nova vacina
     */
    @POST("vacinas/agendar")
    suspend fun agendarVacina(
        @Header("Authorization") token: String,
        @Body request: AgendarVacinaRequest
    ): Response<AgendarVacinaResponse>

    /**
     * Obtém as vacinas agendadas para um animal específico
     */
    @GET("animais/{animalId}/vacinas/agendadas")
    suspend fun getVacinasAgendadas(
        @Header("Authorization") token: String,
        @Path("animalId") animalId: Int
    ): Response<VacinasAgendadasResponse>

    /**
     * Atualiza uma vacina
     */
    @PUT("vacinas/{id}")
    suspend fun updateVacina(
        @Header("Authorization") token: String,
        @Path("id") vacinaId: Int,
        @Body request: UpdateVacinaRequest
    ): Response<UpdateVacinaResponse>

    /**
     * Cancela/apaga uma vacina
     */
    @DELETE("vacinas/{id}")
    suspend fun cancelarVacina(
        @Header("Authorization") token: String,
        @Path("id") vacinaId: Int
    ): Response<CancelVacinaResponse>

    /**
     * Obtém as vacinas com data de agendamento próxima
     */
    @GET("vacinas/proximas")
    suspend fun getVacinasProximas(
        @Header("Authorization") token: String
    ): Response<VacinasProximasResponse>

    /**
     * Marca uma vacina como 'realizada'
     */
    @POST("vacinas/{id}/realizada")
    suspend fun marcarVacinaRealizada(
        @Header("Authorization") token: String,
        @Path("id") vacinaId: Int,
        @Body request: MarcarVacinaRealizadaRequest
    ): Response<MarkVacinaRealizadaResponse>
}
