package pt.ipt.dam2025.trabalho.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de dados que representa uma consulta marcada
 */
data class Consulta(
    // propriedades
    @SerializedName("id")
    val id: Int,

    @SerializedName("userId")
    val userId: Int,

    @SerializedName("animalId")
    val animalId: Int,

    @SerializedName("clinicaId")
    val clinicaId: Int,

    @SerializedName("veterinarioId")
    val veterinarioId: Int,

    @SerializedName("data")
    val data: String,

    @SerializedName("hora")
    val hora: String,

    @SerializedName("motivo")
    val motivo: String?,

    @SerializedName("estado")
    val estado: String,

    @SerializedName("dataMarcacao")
    val dataMarcacao: String
)
