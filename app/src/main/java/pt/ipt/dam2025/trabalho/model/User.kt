package pt.ipt.dam2025.trabalho.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * define como os dados de um utilizador são guardados na base de dados do telemóvel (Room)
 * esta classe representa a tabela 'users' na base de dados
 */
@Entity(tableName = "users")
data class User(
    //Propriedades da entidade
    @PrimaryKey
    @SerializedName("id")
    val id: Int,
    @SerializedName("nome")
    var nome: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("telemovel")
    var telemovel: String,
    @SerializedName("tipo")
    var tipo: String,
    @SerializedName("token")
    var token: String?
)
