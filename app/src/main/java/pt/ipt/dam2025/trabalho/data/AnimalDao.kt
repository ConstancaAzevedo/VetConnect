package pt.ipt.dam2025.trabalho.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.trabalho.model.Animal

@Dao
interface AnimalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(animal: Animal)

    @Query("SELECT * FROM animais ORDER BY nome ASC")
    fun getAll(): Flow<List<Animal>>

    /**
     * Obtém os dados de um animal específico pelo seu ID de forma síncrona.
     * Útil para operações de merge de dados.
     * @param id O ID do animal a ser procurado.
     * @return O animal, ou null se não for encontrado.
     */
    @Query("SELECT * FROM animais WHERE id = :id LIMIT 1")
    fun getById(id: Int): Animal?
}
