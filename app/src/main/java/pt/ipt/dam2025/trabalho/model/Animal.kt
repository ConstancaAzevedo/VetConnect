package pt.ipt.dam2025.trabalho.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animais")
data class Animal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var tutorId: Int = 0, // Adicionado para associar ao tutor
    val nome: String,
    val especie: String,
    val raca: String,
    val dataNascimento: String,
    val fotoUri: String? = null
)
