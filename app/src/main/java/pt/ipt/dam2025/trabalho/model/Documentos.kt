package pt.ipt.dam2025.trabalho.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Data class que representa a estrutura genérica de um QR Code lido.
 */
data class QrCodePayload(
    @SerializedName("tipo") val tipo: String, // "receita", "vacina", "exame"
    @SerializedName("animalId") val animalId: Int,
    @SerializedName("dados") val dados: Map<String, Any>
)

/**
 * Data class para uma Receita. Agora é uma entidade Room.
 */
@Entity(tableName = "receitas")
data class Receita(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val data: String,
    val medicamento: String,
    val posologia: String,
    val medico: String
)

/**
 * Data class para um Exame. Agora é uma entidade Room.
 */
@Entity(tableName = "exames")
data class Exame(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val data: String,
    val tipoExame: String,
    val resultado: String,
    val laboratorio: String
)

/**
 * Data class para uma Vacina. Agora é uma entidade Room.
 */
@Entity(tableName = "vacinas")
data class Vacina(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val data: String,
    val nomeVacina: String,
    val lote: String,
    val proximaDose: String
)
