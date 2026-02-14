package pt.ipt.dam2025.vetconnect.data

import androidx.room.Dao // Importa a anotação para identificar a interface como um DAO
import androidx.room.Insert // Importa a anotação para funções de inserção
import androidx.room.OnConflictStrategy // Importa as estratégias de conflito para inserções
import androidx.room.Query // Importa a anotação para definir queries SQL
import androidx.room.Transaction // Importa a anotação para operações transacionais
import kotlinx.coroutines.flow.Flow // Importa a classe Flow para streams de dados assíncronos
import pt.ipt.dam2025.vetconnect.model.Consulta

/**
 * DAO (Data Access Object) para a entidade Consulta
 * Define todas as operações de base de dados para a tabela 'consultas'
 */
@Dao
interface ConsultaDao {

    /**
     * Obtém todas as consultas de um utilizador específico ordenadas por data (da mais recente para a mais antiga)
     * Retorna um Flow que emite a lista de consultas sempre que os dados mudam
     */
    @Query("SELECT * FROM consultas WHERE userId = :userId ORDER BY data DESC") // Query SQL para selecionar e ordenar as consultas
    fun getConsultasByUser(userId: Int): Flow<List<Consulta>> // O Flow permite que a UI observe as alterações de forma reativa

    /**
     * Insere uma lista de consultas na base de dados
     * Se uma consulta com o mesmo ID já existir ela será substituída
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Define a operação de inserção com estratégia de substituição
    suspend fun insertAll(consultas: List<Consulta>) // Função suspensa para inserir múltiplos registos

    /**
     * Insere uma única consulta na base de dados
     * Se uma consulta com o mesmo ID já existir ela será substituída
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Define a operação de inserção com estratégia de substituição
    suspend fun insert(consulta: Consulta) // Função suspensa para inserir um único registo

    /**
     * Apaga todas as consultas de um utilizador específico da tabela
     */
    @Query("DELETE FROM consultas WHERE userId = :userId") // Query SQL para apagar as consultas de um utilizador
    suspend fun deleteByUser(userId: Int) // Função suspensa para a operação de apagar

    /**
     * Apaga uma consulta específica pelo seu ID
     */
    @Query("DELETE FROM consultas WHERE id = :consultaId") // Query SQL para apagar uma consulta
    suspend fun deleteById(consultaId: Int) // Função suspensa para a operação de apagar

    /**
     * Executa a limpeza de consultas de um utilizador e a inserção de novas como uma única transação
     * A anotação @Transaction garante que ou ambas as operações (delete e insert) são bem sucedidas ou nenhuma é
     * Isto impede que a base de dados fique num estado inconsistente
     */
    @Transaction // Garante que as operações dentro da função são atómicas
    suspend fun clearAndInsert(userId: Int, consultas: List<Consulta>) {
        // Primeiro apaga todas as consultas existentes do utilizador
        deleteByUser(userId)
        // Depois insere a nova lista de consultas recebida
        insertAll(consultas)
    }
}
