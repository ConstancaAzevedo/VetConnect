package pt.ipt.dam2025.vetconnect.data

import androidx.room.Dao // Importa a anotação para identificar a interface como um DAO
import androidx.room.Insert // Importa a anotação para funções de inserção
import androidx.room.OnConflictStrategy // Importa as estratégias de conflito para inserções
import androidx.room.Query // Importa a anotação para definir queries SQL
import androidx.room.Update // Importa a anotação para funções de atualização
import kotlinx.coroutines.flow.Flow // Importa a classe Flow para streams de dados assíncronos
import pt.ipt.dam2025.vetconnect.model.Utilizador

/**
 * DAO para a entidade Utilizador
 * Define todas as operações de base de dados para a tabela 'utilizadores'
 */
@Dao
interface UserDao {

    /**
     * Insere um utilizador na base de dados
     * Se um utilizador com o mesmo ID já existir ele será substituído
     * Útil para guardar os dados do utilizador após o login
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Define a operação de inserção com estratégia de substituição
    suspend fun insert(utilizador: Utilizador) // Função suspensa para ser chamada a partir de uma coroutine

    /**
     * Obtém um utilizador da base de dados pelo seu ID
     * Retorna um Flow que emite o utilizador sempre que os seus dados mudam
     * A UI pode observar este Flow para se atualizar automaticamente
     */
    @Query("SELECT * FROM users WHERE id = :userId") // Query SQL para selecionar um utilizador pelo ID
    fun getUserById(userId: Int): Flow<Utilizador?> // O Flow permite observação reativa dos dados

    /**
     * Obtém um utilizador da base de dados pelo seu ID para uma única leitura
     * Não retorna um Flow e por isso não é reativo
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserByIdOnce(userId: Int): Utilizador?

    /**
     * Atualiza os dados de um utilizador existente na base de dados
     */
    @Update // Define a operação de atualização
    suspend fun update(utilizador: Utilizador) // Função suspensa para a operação de atualização

}
