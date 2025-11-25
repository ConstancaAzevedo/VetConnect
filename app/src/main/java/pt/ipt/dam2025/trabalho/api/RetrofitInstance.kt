package pt.ipt.dam2025.trabalho.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // IMPORTANTE: Substitua pela URL base da sua API
    private const val BASE_URL = "https://api.example.com/" // mudar para a URL da API



    // Criação da instância única do Retrofit (lazy initialization)
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Criação da instância do serviço da API para ser usada na aplicação
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}