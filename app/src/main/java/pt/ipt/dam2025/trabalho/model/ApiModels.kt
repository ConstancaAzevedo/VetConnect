package pt.ipt.dam2025.trabalho.model

import com.google.gson.annotations.SerializedName

/**
 * Data class para representar um utilizador recebido da API
 */

//representa um utilizador como ele vem do servidor
data class Usuario(
    val id: Int,
    val nome: String,
    val email: String,
    val telemovel: String? = null,
    val tipo: String? = null,
    @SerializedName("codigoVerificacao")
    val codigoVerificacao: String? = null
)

//data class para criar um novo utilizador a ser enviado para a API
data class NovoUsuario(
    val nome: String,
    val email: String,
    val tipo: String
)

//data class para a resposta do registo, que contém o utilizador criado
data class RegistrationResponse(
    val user: Usuario,
    @SerializedName("verificationCode")
    val codigoVerificacao: String?
)

//data class para validar o código de verificação
data class VerificationRequest(
    val email: String,
    @SerializedName("codigoVerificacao")
    val codigoVerificacao: String
)

//data class para a resposta da validação do código
data class VerificationResponse(
    val message: String
)

//data class para o corpo do pedido de criação de PIN
data class CreatePinRequest(
    val nome: String,
    val pin: String
)

//data class para a resposta da criação de PIN
data class CreatePinResponse(
    val message: String
)

//data class para o pedido de login
data class LoginRequest(
    val email: String,
    val pin: String
)

//data class para a resposta do login
data class LoginResponse(
    val message: String,
    val token: String,
    val user: Usuario //recebe os dados do utilizador no login
)
