package pt.ipt.dam2025.vetconnect.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.vetconnect.model.Exame

/**
 * DAO responsável por todas as interações com a tabela de exames
 */

@Dao
interface ExameDao {

    /*
     * insere um único exame na base de dados
     * se o exame já existir (pelo ID) será substituído
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exame: Exame)

    /*
     * insere uma lista de exames, substituindo os existentes em caso de conflito
     * útil para sincronizar os dados vindos da API
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exames: List<Exame>)

    /*
     * obtém todos os exames de um animal específico ordenados por data
     */
    @Query("SELECT * FROM exames WHERE animalId = :animalId ORDER BY dataExame DESC")
    fun getExamesByAnimal(animalId: Int): Flow<List<Exame>>

    /*
     * apaga um exame específico pelo seu ID
     */
    @Query("DELETE FROM exames WHERE id = :id")
    suspend fun deleteById(id: Int)

    /*
     * apaga todos os exames de um animal específico da base de dados
     */
    @Query("DELETE FROM exames WHERE animalId = :animalId")
    suspend fun deleteByAnimal(animalId: Int)
}
