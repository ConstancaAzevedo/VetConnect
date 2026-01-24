package pt.ipt.dam2025.trabalho.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.trabalho.model.Consulta

// DAO para a entidade Consulta
@Dao
interface ConsultaDao {

    // Obtém todas as consultas ordenadas por data e hora
    @Query("SELECT * FROM consultas WHERE userId = :userId ORDER BY data DESC, hora DESC")
    fun getConsultasByUser(userId: Int): Flow<List<Consulta>>

    // Insere uma lista de consultas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(consultas: List<Consulta>)

    // Insere uma consulta
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(consulta: Consulta)

    // Apaga todas as consultas de um utilizador
    @Query("DELETE FROM consultas WHERE userId = :userId")
    suspend fun deleteByUser(userId: Int)

    // Apaga uma consulta pelo seu ID
    @Query("DELETE FROM consultas WHERE id = :consultaId")
    suspend fun deleteById(consultaId: Int)

    // Apaga as consultas antigas de um utilizador
    @Transaction
    suspend fun clearAndInsert(userId: Int, consultas: List<Consulta>) {
        deleteByUser(userId)
        insertAll(consultas)
    }
}
