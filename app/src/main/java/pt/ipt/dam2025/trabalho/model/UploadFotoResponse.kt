package pt.ipt.dam2025.trabalho.model

import com.google.gson.annotations.SerializedName

data class UploadFotoResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("fotoUrl") val fotoUrl: String,
    @SerializedName("filename") val filename: String,
    @SerializedName("animal") val animal: AnimalResumo? = null
)

data class AnimalResumo(
    @SerializedName("id") val id: Int,
    @SerializedName("nome") val nome: String
)