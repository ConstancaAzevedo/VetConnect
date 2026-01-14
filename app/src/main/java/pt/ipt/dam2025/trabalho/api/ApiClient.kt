package pt.ipt.dam2025.trabalho.api

import pt.ipt.dam2025.trabalho.BuildConfig
import pt.ipt.dam2025.trabalho.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Configura a conexão da aplicação com o servidor
 */
object ApiClient {

    // true -> servidor local / false -> render
    private const val USE_LOCAL_URL = false

    // URL do Render (Produção)
    private const val BASE_URL = "https://vetconnect-api.onrender.com/"

    // Para desenvolvimento local
    private const val LOCAL_URL = "http://10.0.2.2:3000"

    private fun getBaseUrl(): String {
        return if (BuildConfig.DEBUG && USE_LOCAL_URL) {
            LOCAL_URL
        } else {
            BASE_URL
        }
    }
    
    //Criar um intercetor de loggin - regista os detalhes das chamadas de rede
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY //Regista o máximo de informação possível
    }

    //Cria o cliente HTTP com os interceptores
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) //Adiciona o interceptor de loggin ao cliente - todas as chamadas serão registadas no Logcat
        .connectTimeout(30, TimeUnit.SECONDS) //Tempo limite para conexção com o servidor
        .readTimeout(30, TimeUnit.SECONDS) //Tempo limite para receber resposta do servidor
        .writeTimeout(30, TimeUnit.SECONDS) //Tempo limite para enviar dados ao servidor
        .build()

    //Cria o objeto principal do Retrofit
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(getBaseUrl()) //Usa a função para obter o URL dinamicamente
        .addConverterFactory(GsonConverterFactory.create()) //Converte o JSON para objetos Kotlin
        .client(okHttpClient) // Adiciona o cliente HTTP com o logging e timeouts
        .build()

    //Cria a instância do serviço da API
    // by lazy significa que este código só é executado na primeira vez que o ApiService é usado
    // nas vezes seguintes a instância é reutilizada
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java) //Cria uma instância do serviço da API usando o objeto Retrofit
    }
}
