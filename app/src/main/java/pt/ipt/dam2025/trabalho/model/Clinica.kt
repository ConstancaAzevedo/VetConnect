package pt.ipt.dam2025.trabalho.model

import com.google.gson.annotations.SerializedName

/**
 * representa uma clínica veterinária
 */
data class Clinica(
    // propriedades
    @SerializedName("id")
    val id: Int,

    @SerializedName("nome")
    val nome: String
)
