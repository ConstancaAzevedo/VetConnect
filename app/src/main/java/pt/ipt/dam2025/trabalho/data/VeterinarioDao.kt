package pt.ipt.dam2025.trabalho.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.trabalho.model.Veterinario

// DAO para a entidade Veterinario
@Dao
interface VeterinarioDao {
    // Insere ou atualiza uma lista de veterinarios
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(veterinarios: List<Veterinario>)

    // Obtém todos os veterinarios ordenados por nome
    @Query("SELECT * FROM veterinarios ORDER BY nome ASC")
    fun getAll(): Flow<List<Veterinario>>

    // Obtém os veterinarios de uma clinica específica
    @Query("SELECT * FROM veterinarios WHERE clinicaId = :clinicaId ORDER BY nome ASC")
    fun getByClinicaId(clinicaId: Int): Flow<List<Veterinario>>

    // Apaga todos os veterinarios
    @Query("DELETE FROM veterinarios")
    suspend fun clearAll()
}
