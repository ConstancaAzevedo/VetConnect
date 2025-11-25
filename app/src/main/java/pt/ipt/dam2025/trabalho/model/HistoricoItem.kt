package pt.ipt.dam2025.trabalho.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * representa uma entrada no histórico médico de um animal
 * a anotação @Entity faz com que esta classe seja uma tabela na base de dados Room
 */
@Entity(tableName = "historico_medico") //Room cria uma tabela chamada "historico_medico"
data class HistoricoItem(
    @PrimaryKey(autoGenerate = true) //a coluna id é a chave principal (identificador único de cada linha)
    val id: Int = 0, //sempre que se adicionar um registo novo, o id é gerado automaticamente (1,2,3,...)

    val data: String,
    val descricao: String
)
