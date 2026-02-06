package pt.ipt.dam2025.vetconnect.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.vetconnect.model.TipoVacina

/**
 * DAO responsável por todas as interações com a tabela de tipos de vacina
 */
@Dao
interface TipoVacinaDao {

    /* Obtém todos os tipos de vacina, ordenados por nome */
    @Query("SELECT * FROM tipos_vacina ORDER BY nome ASC")
    fun getTiposVacina(): Flow<List<TipoVacina>>

    /* Insere ou atualiza uma lista de tipos de vacina */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tipos: List<TipoVacina>)

    /* Apaga todos os tipos de vacina da tabela */
    @Query("DELETE FROM tipos_vacina")
    suspend fun clearAll()
}
