package pt.ipt.dam2025.trabalho.model

import com.google.gson.annotations.SerializedName

// Classe para representar o payload do QR Code
data class QrCodePayload(
    //Propriedades da classe
    @SerializedName("payload")
    val payload: String
)
