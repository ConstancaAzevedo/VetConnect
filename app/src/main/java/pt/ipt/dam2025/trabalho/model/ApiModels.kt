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
    val password: String,
    val tipo: String
)

/**
 * Data class para a resposta do registo, que contém o utilizador criado.
 */
data class RegistrationResponse(
    val user: Usuario
)

/**
 * Data class para o corpo do pedido de verificação de código.
 */
data class VerificationRequest(
    val email: String,
    @SerializedName("codigoVerificacao")
    val codigoVerificacao: String
)

/**
 * Data class para a resposta da verificação de código.
 */
data class VerificationResponse(
    val message: String
)

/**
 * Data class para o corpo do pedido de criação de PIN.
 */
data class CreatePinRequest(
    val nome: String,
    val pin: String
)

/**
 * Data class para a resposta da criação de PIN.
 */
data class CreatePinResponse(
    val message: String
)
