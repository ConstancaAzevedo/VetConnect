package pt.ipt.dam2025.trabalho.model

import androidx.room.Entity
import androidx.room.PrimaryKey


/*
 * define como os dados de um animal são guardados na base de dados do telemóvel (Room)
 * a anotação @Entity faz com que esta classe seja uma tabela na base de dados Room
 */

@Entity(tableName = "animais")
data class Animal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val especie: String,
    val raca: String,
    val dataNascimento: String,
    val fotoUri: String? = null // String para guardar o URI da foto
)
