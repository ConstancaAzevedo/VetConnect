package pt.ipt.dam2025.trabalho.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.trabalho.model.Animal

/**
 * DAO responsável por todas as interações com a tabela de animais
 */

@Dao
interface AnimalDao {

    //Insere o objeto animal na tabela; se já existir, substitui
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(animal: Animal)


    /*
     *Retorna todos os animais ordenados por nome em ordem crescente
     *O fluxo retornará uma lista de animais atualizada sempre que houver alterações
     */
    @Query("SELECT * FROM animais ORDER BY nome ASC")
    fun getAll(): Flow<List<Animal>>




    //continuar a comentar
    @Query("SELECT * FROM animais WHERE id = :id LIMIT 1")
    fun getById(id: Long): Animal?

    @Query("SELECT * FROM animais WHERE tutorId = :tutorId LIMIT 1")
    fun getAnimalByTutorId(tutorId: Int): Animal?
}
