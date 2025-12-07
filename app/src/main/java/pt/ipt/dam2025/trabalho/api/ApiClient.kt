package pt.ipt.dam2025.trabalho.api

import pt.ipt.dam2025.trabalho.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * configura a conexão da aplicação com o servidor
 */


//Criar um Singleton -> só exisitirá uma única instância de ApiClient em toda a aplicação
object ApiClient {
    private const val BASE_URL = "https://vetconnect-api-production.up.railway.app" //URL da API


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
        .baseUrl(BASE_URL) //Endereço base da API
        .client(okHttpClient) //Cliente HTTP com os interceptores
        .addConverterFactory(GsonConverterFactory.create()) //Converte o JSON para objetos Kotlin usando a biblioteca Gson
        .build()


    //Cria a instância do serviço da API
    // by lazy significa que este código só é executado na primeira vez que o ApiService é usado
    // nas vezes seguintes a instância é reutilizada
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java) //Cria uma instância do serviço da API usando o objeto Retrofit
    }
}