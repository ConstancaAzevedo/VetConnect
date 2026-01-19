package pt.ipt.dam2025.trabalho.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * estrutura de dados para um animal
 */

@Entity(tableName = "animais")
data class Animal(
    // propriedades
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // ID único do animal
    var tutorId: Int = 0, // ID do tutor do animal
    val nome: String,
    val especie: String,
    val raca: String,
    val dataNascimento: String?,
    val fotoUrl: String?,
    val numeroChip: Int?
)
