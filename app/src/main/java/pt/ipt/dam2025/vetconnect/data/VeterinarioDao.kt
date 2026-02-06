package pt.ipt.dam2025.vetconnect.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.vetconnect.model.Veterinario

/**
 * DAO responsável por todas as interações com a tabela de veterinários
 */
@Dao
interface VeterinarioDao {

    /*
     * obtém todos os veterinários, ordenados por nome
     */
    @Query("SELECT * FROM veterinarios ORDER BY nome ASC")
    fun getAll(): Flow<List<Veterinario>>

    /*
     * obtém os veterinários de uma clínica específica, ordenados por nome
     */
    @Query("SELECT * FROM veterinarios WHERE clinicaId = :clinicaId ORDER BY nome ASC")
    fun getVeterinariosByClinica(clinicaId: Int): Flow<List<Veterinario>>

    /*
     * insere uma lista de veterinários na base de dados
     * se um veterinário com o mesmo ID já existir, será substituído
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(veterinarios: List<Veterinario>)

    /*
     * apaga os veterinários de uma clínica específica
     */
    @Query("DELETE FROM veterinarios WHERE clinicaId = :clinicaId")
    suspend fun deleteByClinica(clinicaId: Int)

    /*
     * apaga todos os veterinários da tabela
     */
    @Query("DELETE FROM veterinarios")
    suspend fun clearAll()
}
