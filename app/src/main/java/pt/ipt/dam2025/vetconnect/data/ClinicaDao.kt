package pt.ipt.dam2025.vetconnect.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.vetconnect.model.Clinica

/**
 * DAO responsável por todas as interações com a tabela de clínicas
 */
@Dao
interface ClinicaDao {

    /* Obtém todas as clínicas ordenadas por nome */
    @Query("SELECT * FROM clinicas ORDER BY nome ASC")
    fun getAllClinicas(): Flow<List<Clinica>>

    /* Insere ou atualiza uma lista de clínicas */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clinicas: List<Clinica>)

    /* Apaga todas as clínicas da tabela */
    @Query("DELETE FROM clinicas")
    suspend fun clearAll()
}
