package pt.ipt.dam2025.trabalho.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Define como os dados de um utilizador são guardados na base de dados do telemóvel (Room).
 * A anotação @Entity faz com que esta classe seja uma tabela na base de dados Room.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val email: String, // Email é um identificador único, perfeito para chave primária
    var token: String? = null // Token de autenticação recebido da API
)
