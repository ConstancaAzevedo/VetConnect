package pt.ipt.dam2025.trabalho.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    // URL de placeholder para testes. Deve ser substituída pela URL da API real.
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    // Interceptor para logging, muito útil para debug
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente OkHttp com o interceptor
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Instância do Retrofit configurada
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Exposição da API Service para ser usada na aplicação
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
