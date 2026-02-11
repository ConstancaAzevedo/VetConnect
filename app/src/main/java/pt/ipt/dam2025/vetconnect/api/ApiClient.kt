package pt.ipt.dam2025.vetconnect.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import pt.ipt.dam2025.vetconnect.util.SessionManager

/**
 * Objeto que contém a instância do Retrofit para a API
 * Configura a conexão da aplicação com a API
 */

object ApiClient {

    // URL do Render 
    private const val BASE_URL = "https://vetconnect-api-5g9p.onrender.com"

    private lateinit var retrofit: Retrofit

    /*
     * inicializa o ApiClient com um intercetor de autenticação
     * deve ser chamado na classe Application
     */
    fun init(sessionManager: SessionManager) {
        // cria um intercetor de loggin - regista os detalhes das chamadas de rede
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // regista o máximo de informação possível
        }

        /*
         * intercetor para adicionar o token de autenticação a todos os pedidos
         */
        val authInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            sessionManager.getAuthToken()?.let { token ->
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }

        /*
         * cria o cliente HTTP com os interceptores
         */
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // adiciona o interceptor de loggin ao cliente - todas as chamadas serão registadas no Logcat
            .addInterceptor(authInterceptor) // adiciona o intercetor de autenticação
            .connectTimeout(30, TimeUnit.SECONDS) // tempo limite para conexção com o servidor
            .readTimeout(30, TimeUnit.SECONDS) // tempo limite para receber resposta do servidor
            .writeTimeout(30, TimeUnit.SECONDS) // tempo limite para enviar dados ao servidor
            .build()

        /*
         * cria o objeto principal do Retrofit
         */
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL) // usa a função para obter o URL dinamicamente
            .addConverterFactory(GsonConverterFactory.create()) // converte o JSON para objetos Kotlin
            .client(okHttpClient) // adiciona o cliente HTTP com o logging e timeouts
            .build()
    }

    /*
     * cria a instância do serviço da API
     * by lazy significa que este código só é executado na primeira vez que o ApiService é usado
     * nas vezes seguintes a instância é reutilizada
     */
    val apiService: ApiService by lazy {
        if (!::retrofit.isInitialized) {
            throw IllegalStateException("ApiClient not initialized. Call ApiClient.init() in your Application class.")
        }
        retrofit.create(ApiService::class.java) // cria uma instância do serviço da API usando o objeto Retrofit
    }
}