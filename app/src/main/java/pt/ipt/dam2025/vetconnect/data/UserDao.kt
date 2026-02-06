package pt.ipt.dam2025.vetconnect.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.vetconnect.model.Usuario

/**
 * DAO responsável por todas as interações com a tabela de utilizadores
 */

@Dao
interface UserDao {

    /*
     * insere o objeto user na tabela se já existir substitui
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: Usuario)

    /*
     * atualiza um utilizador existente na base de dados
     */
    @Update
    suspend fun update(user: Usuario)

    /*
     * obtém os dados de um utilizador específico pelo seu ID e expõe-os como um Flow
     */
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Int): Flow<Usuario?>

    /*
     * obtém os dados de um utilizador específico pelo seu ID
     * só é chamada uma vez vai à base de daos obtem o utilizador devolve e termina
     */
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserByIdOnce(id: Int): Usuario?

    /*
     * elimina um utilizador específico pelo seu ID
     */
    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteById(id: Int)

    /*
     * elimina todos os utilizadores da tabela
     * útil para fazer logout e limpar a sessão do utilizador
     */
    @Query("DELETE FROM users")
    suspend fun clearAll()
}