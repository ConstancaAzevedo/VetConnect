package pt.ipt.dam2025.vetconnect.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.vetconnect.model.AnimalResponse

/**
 * DAO responsável por todas as interações com a tabela de animais
 */
@Dao
interface AnimalDao {

    /*
     * insere um animal na tabela
     * se o animal já existir é substituído
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(animal: AnimalResponse)

    /*
     * insere uma lista de animais, substituindo os existentes
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(animais: List<AnimalResponse>)

    /*
     * obtém todos os animais de um tutor específico, ordenados por nome
     * retorna um Flow para que a UI possa observar alterações em tempo real
     */
    @Query("SELECT * FROM animais WHERE tutorId = :tutorId ORDER BY nome ASC")
    fun getAnimalsByTutorId(tutorId: Int): Flow<List<AnimalResponse>>

    /*
     * obtém um animal específico pelo seu ID
     * retorna um Flow para que a UI possa observar alterações em tempo real
     */
    @Query("SELECT * FROM animais WHERE id = :id LIMIT 1")
    fun getById(id: Int): Flow<AnimalResponse?>

    /*
     * obtém um animal específico pelo seu ID para uma operação única
     */
    @Query("SELECT * FROM animais WHERE id = :id LIMIT 1")
    suspend fun getAnimalById(id: Int): AnimalResponse?

    /*
     * apaga um animal da base de dados pelo seu ID
     */
    @Query("DELETE FROM animais WHERE id = :id")
    suspend fun deleteById(id: Int)
}
