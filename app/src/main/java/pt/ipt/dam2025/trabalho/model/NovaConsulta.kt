package pt.ipt.dam2025.trabalho.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de dados para enviar um pedido de criação de uma nova consulta
 */
data class NovaConsulta(
    // propriedades
    @SerializedName("userId")
    val userId: Int,

    @SerializedName("animalId")
    val animalId: Int,

    @SerializedName("clinicaId")
    val clinicaId: Int,

    @SerializedName("veterinarioId")
    val veterinarioId: Int,

    @SerializedName("data")
    val data: String, // formato: "YYYY-MM-DD"

    @SerializedName("hora")
    val hora: String, // formato: "HH:MM:SS"

    @SerializedName("motivo")
    val motivo: String
)
