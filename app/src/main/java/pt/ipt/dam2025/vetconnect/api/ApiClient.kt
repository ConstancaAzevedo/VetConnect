package pt.ipt.dam2025.vetconnect.api

import okhttp3.Interceptor // Importa a classe para criar intercetores de rede
import okhttp3.OkHttpClient // Importa o cliente HTTP
import okhttp3.logging.HttpLoggingInterceptor // Importa o intercetor para logging
import retrofit2.Retrofit // Importa a classe principal do Retrofit
import retrofit2.converter.gson.GsonConverterFactory // Importa o conversor para JSON
import java.util.concurrent.TimeUnit // Importa a unidade de tempo para os timeouts
import pt.ipt.dam2025.vetconnect.util.SessionManager // Importa o nosso gestor de sessão

/**
 * Objeto singleton que gere a instância do Retrofit para toda a aplicação
 * Configura o cliente HTTP e como a app comunica com a API
 */

object ApiClient {

    // URL base da nossa API alojada no Render
    private const val BASE_URL = "https://vetconnect-api-5g9p.onrender.com"

    // Declaração da instância do Retrofit que será inicializada mais tarde
    private lateinit var retrofit: Retrofit

    /**
     * Inicializa o ApiClient com as configurações necessárias
     * Este métdo tem de ser chamado uma única vez na classe Application da app
     */
    fun init(sessionManager: SessionManager) {
        // Cria um intercetor para registar os detalhes dos pedidos e respostas HTTP no Logcat
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            // Define o nível de detalhe do log para BODY que é o mais completo
            level = HttpLoggingInterceptor.Level.BODY
        }

        /**
         * Cria um intercetor para adicionar o token de autenticação a todos os pedidos à API
         */
        val authInterceptor = Interceptor { chain ->
            // Obtém o construtor do pedido original
            val requestBuilder = chain.request().newBuilder()
            // Pede o token de autenticação ao SessionManager
            sessionManager.getAuthToken()?.let { token ->
                // Se o token existir adiciona-o ao cabeçalho (header) "Authorization"
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            // Continua a cadeia de execução com o pedido modificado (ou não, se não houver token)
            chain.proceed(requestBuilder.build())
        }

        /**
         * Constrói o cliente OkHttpClient com os intercetores e tempos de espera
         */
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // Adiciona o intercetor de logging
            .addInterceptor(authInterceptor) // Adiciona o intercetor de autenticação
            .connectTimeout(30, TimeUnit.SECONDS) // Define o tempo máximo de espera para estabelecer uma conexão
            .readTimeout(30, TimeUnit.SECONDS) // Define o tempo máximo de espera para ler dados da resposta
            .writeTimeout(30, TimeUnit.SECONDS) // Define o tempo máximo de espera para enviar dados do pedido
            .build() // Constrói o objeto OkHttpClient

        /**
         * Constrói a instância principal do Retrofit
         */
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL) // Define o URL base para todos os pedidos
            .addConverterFactory(GsonConverterFactory.create()) // Adiciona o conversor que transforma JSON em objetos Kotlin
            .client(okHttpClient) // Usa o nosso cliente HTTP personalizado com os intercetores
            .build() // Constrói o objeto Retrofit
    }

    /**
     * Fornece a instância do serviço da API (ApiService)
     * 'by lazy' significa que a instância só é criada na primeira vez que é acedida
     * e depois é reutilizada em todas as chamadas seguintes
     */
    val apiService: ApiService by lazy {
        // Verificação de segurança para garantir que o ApiClient foi inicializado antes de ser usado
        if (!::retrofit.isInitialized) {
            throw IllegalStateException("ApiClient not initialized. Call ApiClient.init() in your Application class.")
        }
        // Usa o Retrofit para criar e retornar a implementação da nossa interface ApiService
        retrofit.create(ApiService::class.java)
    }
}
