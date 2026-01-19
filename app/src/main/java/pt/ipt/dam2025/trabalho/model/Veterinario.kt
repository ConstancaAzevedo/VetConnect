package pt.ipt.dam2025.trabalho.model

import com.google.gson.annotations.SerializedName

/**
 * representa um veterinário, conforme os dados da API
 */
data class Veterinario(
    // propriedades
    @SerializedName("id")
    val id: Int,

    @SerializedName("nome")
    val nome: String,

    @SerializedName("clinicaId")
    val clinicaId: Int // ID da clínica a que o veterinário pertence
)
