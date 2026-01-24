package pt.ipt.dam2025.trabalho.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.trabalho.model.Receita

// DAO para a entidade Receita

@Dao
interface ReceitaDao {
    // Insere uma lista de receitas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(receitas: List<Receita>)

    // Obtém todas as receitas ordenadas por data de prescrição
    @Query("SELECT * FROM receitas ORDER BY dataPrescricao DESC")
    fun getAllReceitas(): Flow<List<Receita>>

    // Obtém as receitas de um animal específico
    @Query("SELECT * FROM receitas WHERE animalId = :animalId ORDER BY dataPrescricao DESC")
    fun getReceitasByAnimal(animalId: Int): Flow<List<Receita>>

    // Apaga as receitas de um animal
    @Query("DELETE FROM receitas WHERE animalId = :animalId")
    suspend fun deleteByAnimal(animalId: Int)
}
