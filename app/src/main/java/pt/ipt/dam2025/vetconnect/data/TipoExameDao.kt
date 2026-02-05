package pt.ipt.dam2025.vetconnect.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.vetconnect.model.TipoExame

/**
 * DAO para a entidade TipoExame
 */
@Dao
interface TipoExameDao {

    /**
     * Insere uma lista de tipos de exame, substituindo os existentes.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tiposExame: List<TipoExame>)

    /**
     * Obtém todos os tipos de exame guardados localmente.
     */
    @Query("SELECT * FROM tipos_exame ORDER BY nome ASC")
    fun getAll(): Flow<List<TipoExame>>

    /**
     * Apaga todos os tipos de exame da tabela.
     */
    @Query("DELETE FROM tipos_exame")
    suspend fun clearAll()
}
