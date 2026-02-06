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
    // Insere ou atualiza uma lista de clinicas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clinicas: List<Clinica>)

    // Obtém todas as clinicas ordenadas por nome
    @Query("SELECT * FROM clinicas ORDER BY nome ASC")
    fun getAll(): Flow<List<Clinica>>

    // Apaga todas as clinicas
    @Query("DELETE FROM clinicas")
    suspend fun clearAll()
}
