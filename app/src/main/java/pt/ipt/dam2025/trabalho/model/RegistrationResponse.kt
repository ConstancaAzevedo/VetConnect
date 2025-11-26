package pt.ipt.dam2025.trabalho.model

/**
 * representa a resposta da API ao criar um novo utilizador
 * contém os dados do utilizador e uma mensagem do servidor
 */
data class RegistrationResponse(
    val user: Usuario,
    val message: String
)
