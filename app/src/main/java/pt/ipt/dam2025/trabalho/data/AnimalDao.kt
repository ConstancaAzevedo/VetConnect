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
}
