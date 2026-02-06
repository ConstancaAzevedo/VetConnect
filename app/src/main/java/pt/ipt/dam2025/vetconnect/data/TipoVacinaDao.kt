package pt.ipt.dam2025.vetconnect.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pt.ipt.dam2025.vetconnect.model.TipoVacina

/**
 * DAO responsável por todas as interações com a tabela de tipo de vacinas
 */

@Dao
interface TipoVacinaDao {

    // Insere ou atualiza uma lista de tipos de vacinas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tiposVacina: List<TipoVacina>)

    // Obtém todos os tipos de vacinas
    @Query("SELECT * FROM tipos_vacina")
    suspend fun getTiposVacina(): List<TipoVacina>

    // Apaga todos os tipos de vacinas
    @Query("DELETE FROM tipos_vacina")
    suspend fun clearAll()
}
