package pt.ipt.dam2025.trabalho.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Interface comum para todos os tipos de documentos (Receita, Exame, Vacina)
 * Permite que sejam tratados de forma polimórfica numa lista de histórico
 */
interface Documento : Serializable {
    // Propriedades comuns a todos os documentos
    val id: Long
    val animalId: Int
    val data: String
    val tipo: String // "Receita", "Exame", "Vacina"
    val nome: String   // Nome do medicamento, tipo de exame ou nome da vacina
}

// Modelos para a CRIAÇÃO de documentos

/**
 * Payload genérico para criar qualquer documento
 * Contém um sub-objeto "dados"
 */
data class CreateDocumentRequestInternal(
    @SerializedName("tipo") val tipo: String, // "receita", "vacina", "exame"
    @SerializedName("animalId") val animalId: Int,
    @SerializedName("dados") val dados: Any // ReceitaData, VacinaData ou ExameData
)


// Detalhes específicos para criar uma Receita
data class ReceitaData(
    @SerializedName("dataPrescricao") val dataPrescricao: String,
    @SerializedName("medicamento") val medicamento: String,
    @SerializedName("dosagem") val dosagem: String?,
    @SerializedName("frequencia") val frequencia: String?,
    @SerializedName("duracao") val duracao: String?,
    @SerializedName("veterinario") val veterinario: String?,
    @SerializedName("observacoes") val observacoes: String?
)


// Detalhes específicos para criar uma Vacina
data class VacinaData(
    @SerializedName("dataAplicacao") val dataAplicacao: String?,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("dataProxima") val dataProxima: String?,
    @SerializedName("veterinario") val veterinario: String?,
    @SerializedName("lote") val lote: String?,
    @SerializedName("observacoes") val observacoes: String?
)


// Detalhes específicos para criar um Exame
data class ExameData(
    @SerializedName("dataExame") val dataExame: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("resultado") val resultado: String?,
    @SerializedName("laboratorio") val laboratorio: String?,
    @SerializedName("veterinario") val veterinario: String?,
    @SerializedName("ficheiroUrl") val ficheiroUrl: String?,
    @SerializedName("observacoes") val observacoes: String?
)
