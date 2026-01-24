package pt.ipt.dam2025.trabalho.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.trabalho.model.Clinica

// DAO para a entidade Clinica
@Dao
interface ClinicaDao {
    // Insere ou atualiza uma lista de clinicas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clinicas: List<Clinica>)

    // Obtém todas as clinicas ordenadas por nome
    @Query("SELECT * FROM clinicas ORDER BY nome ASC")
    fun getAll(): Flow<List<Clinica>>

    // Obtém uma clinica pelo seu ID
    @Query("DELETE FROM clinicas")
    suspend fun clearAll()
}
