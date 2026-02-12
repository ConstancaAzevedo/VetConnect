package pt.ipt.dam2025.vetconnect.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.vetconnect.model.Vacina

/**
 * DAO responsável por todas as interações com a tabela de vacinas
 */
@Dao
interface VacinaDao {

    /* Obtém as vacinas agendadas para uma data específica */
    @Query("SELECT * FROM vacinas WHERE data_agendada LIKE :date || '%'")
    suspend fun getVaccinesForDate(date: String): List<Vacina>

    /* Insere uma lista de vacinas */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vacinas: List<Vacina>)

    /* Obtém todas as vacinas ordenadas por data de aplicação */
    @Query("SELECT * FROM vacinas ORDER BY dataAplicacao DESC")
    fun getAllVacinas(): Flow<List<Vacina>>

    /* Obtém as vacinas de um animal específico */
    @Query("SELECT * FROM vacinas WHERE animalId = :animalId ORDER BY dataAplicacao DESC")
    fun getVacinasByAnimal(animalId: Int): Flow<List<Vacina>>

    /* Apaga as vacinas de um animal */
    @Query("DELETE FROM vacinas WHERE animalId = :animalId")
    suspend fun deleteByAnimal(animalId: Int)

    /* Apaga todas as vacinas */
    @Query("DELETE FROM vacinas")
    suspend fun clearAll()
}
