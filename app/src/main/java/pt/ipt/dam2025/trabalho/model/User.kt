package pt.ipt.dam2025.trabalho.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Define como os dados de um utilizador são guardados na base de dados do telemóvel (Room).
 * Esta classe representa a tabela 'users' na base de dados.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: Int, // ID único do utilizador, vindo da API
    var nome: String,
    var email: String,
    var telemovel: String? = null,
    var nacionalidade: String? = null,
    var sexo: String? = null,
    var cc: String? = null,
    var dataNascimento: String? = null,
    var morada: String? = null,
    var token: String? = null, // Token de autenticação recebido da API
    var codigo: String? = null // Código de verificação
)
