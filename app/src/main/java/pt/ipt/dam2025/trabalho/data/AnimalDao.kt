package pt.ipt.dam2025.trabalho.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.trabalho.model.AnimalResponse

/**
 * DAO responsável por todas as interações com a tabela de animais
 */
@Dao
interface AnimalDao {

    /**
     * Insere um animal na tabela
     * Se o animal já existir, é substituído
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(animal: AnimalResponse)

    /**
     * Obtém todos os animais de um tutor específico, ordenados por nome
     * Retorna um Flow para que a UI possa observar alterações em tempo real
     */
    @Query("SELECT * FROM animais WHERE tutorId = :tutorId ORDER BY nome ASC")
    fun getAnimalsByTutorId(tutorId: Int): Flow<List<AnimalResponse>>

    /**
     * Obtém um animal específico pelo seu ID
     * Retorna um Flow para que a UI possa observar alterações em tempo real
     */
    @Query("SELECT * FROM animais WHERE id = :id LIMIT 1")
    fun getById(id: Int): Flow<AnimalResponse?>

    /**
     * Apaga um animal da base de dados pelo seu ID
     */
    @Query("DELETE FROM animais WHERE id = :id")
    suspend fun deleteById(id: Int)
}
