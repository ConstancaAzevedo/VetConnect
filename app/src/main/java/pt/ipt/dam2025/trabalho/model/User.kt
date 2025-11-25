package pt.ipt.dam2025.trabalho.model

import androidx.room.Entity
import androidx.room.PrimaryKey


/*
 * define como os dados de um utilizador são guardados na base de dados do telemóvel (Room)
 * a anotação @Entity faz com que esta classe seja uma tabela na base de dados Room
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val identifier: String, // Número de telemóvel para o tutor, cédula para o veterinário
    val userType: String, // "TUTOR" ou "VETERINARIO"
    val pin: String
)
