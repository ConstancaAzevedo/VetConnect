package pt.ipt.dam2025.trabalho.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.trabalho.model.Exame

// DAO para a entidade Exame
@Dao
interface ExameDao {
    // Insere uma lista de exames
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exames: List<Exame>)

    // Obtém todos os exames ordenados por data
    @Query("SELECT * FROM exames ORDER BY dataExame DESC")
    fun getAllExames(): Flow<List<Exame>>

    // Obtém os exames de um animal específico
    @Query("SELECT * FROM exames WHERE animalId = :animalId ORDER BY dataExame DESC")
    fun getExamesByAnimal(animalId: Int): Flow<List<Exame>>

    // Apaga os exames de um animal
    @Query("DELETE FROM exames WHERE animalId = :animalId")
    suspend fun deleteByAnimal(animalId: Int)
}
