package pt.ipt.dam2025.trabalho.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// Classe que representa a entidade Animal no banco de dados
@Entity(tableName = "animais")
data class Animal(
    // Propriedades da entidade
    @PrimaryKey val id: Int, // Chave primária
    val tutorId: Int,
    val nome: String,
    val especie: String,
    val raca: String,
    val dataNascimento: String?,
    val fotoUrl: String?,
    val numeroChip: String?,
    val codigoUnico: String,
    //serializedname é para o gson
    @SerializedName("dataregisto") val dataRegisto: String?,
    @SerializedName("tutornome") val tutorNome: String?,
    @SerializedName("tutoremail") val tutorEmail: String?
)
