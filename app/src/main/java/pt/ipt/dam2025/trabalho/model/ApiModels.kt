package pt.ipt.dam2025.trabalho.model

/**
 * Data class para representar um utilizador recebido da API.
 */
data class Usuario(
    val id: Int,
    val nome: String,
    val email: String,
    val telefone: String? = null
)

/**
 * Data class para criar um novo utilizador a ser enviado para a API.
 */
data class NovoUsuario(
    val nome: String,
    val email: String,
    val telefone: String? = null
)
