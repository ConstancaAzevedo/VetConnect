package pt.ipt.dam2025.trabalho.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.trabalho.model.User

@Dao
interface UserDao {

    /**
     * Insere um utilizador. Se o utilizador com o mesmo ID já existir, ele será substituído.
     * Esta é a operação principal para guardar dados do utilizador na base de dados local.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: User)

    /**
     * Atualiza um utilizador existente na base de dados.
     */
    @Update
    suspend fun update(user: User)

    /**
     * Obtém os dados de um utilizador específico pelo seu ID e expõe-os como um Flow.
     * A UI irá observar este Flow para receber atualizações automaticamente.
     * @param id O ID do utilizador a ser procurado.
     * @return Um Flow que emite o utilizador, ou null se não for encontrado.
     */
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Int): Flow<User?>

    /**
     * Obtém os dados de um utilizador específico pelo seu ID.
     * Esta é uma operação 'one-shot'.
     * @param id O ID do utilizador a ser procurado.
     * @return O utilizador, ou null se não for encontrado.
     */
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserByIdOnce(id: Int): User?

    /**
     * Elimina todos os utilizadores da tabela.
     * Útil para fazer logout e limpar a sessão do utilizador.
     */
    @Query("DELETE FROM users")
    suspend fun clearAll()
}
