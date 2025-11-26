package pt.ipt.dam2025.trabalho.model

import com.google.gson.annotations.SerializedName

/**
 * Data class para representar um utilizador recebido da API
 */
data class Usuario(
    val id: Int,
    val nome: String,
    val email: String,
    val telefone: String? = null,
    // Adiciona o campo para o código de verificação, que pode ser nulo
    @SerializedName("codigoVerificacao")
    val codigoVerificacao: String? = null
)

/**
 * Data class para criar um novo utilizador a ser enviado para a API.
 */
data class NovoUsuario(
    val nome: String,
    val email: String,
    val telefone: String? = null,
    // Adiciona os campos de password e tipo, necessários para o registo
    val password: String,
    val tipo: String
)
