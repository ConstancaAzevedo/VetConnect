package pt.ipt.dam2025.trabalho.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Estrutura de dados para um animal
 */

@Entity(tableName = "animais")
data class Animal(
    //Propriedades
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, //chave primária autogerada única
    var tutorId: Int = 0, //associar ao tutor
    val nome: String,
    val especie: String,
    val raca: String,
    val dataNascimento: String,
    val fotoUri: String
)
