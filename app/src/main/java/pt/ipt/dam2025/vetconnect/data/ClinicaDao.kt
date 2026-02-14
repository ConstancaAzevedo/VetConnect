package pt.ipt.dam2025.vetconnect.data

import androidx.room.Dao // Importa a anotação para identificar a interface como um DAO
import androidx.room.Insert // Importa a anotação para funções de inserção
import androidx.room.OnConflictStrategy // Importa as estratégias de conflito para inserções
import androidx.room.Query // Importa a anotação para definir queries SQL
import kotlinx.coroutines.flow.Flow // Importa a classe Flow para streams de dados assíncronos
import pt.ipt.dam2025.vetconnect.model.Clinica

/**
 * DAO (Data Access Object) para a entidade Clinica
 * Define todas as operações de base de dados para a tabela 'clinicas'
 */

@Dao
interface ClinicaDao {

    /**
     * Obtém todas as clínicas guardadas na base de dados ordenadas por nome
     * Retorna um Flow que emite a lista sempre que os dados na tabela mudam
     */
    @Query("SELECT * FROM clinicas ORDER BY nome ASC") // Query SQL para selecionar e ordenar as clínicas
    fun getAllClinicas(): Flow<List<Clinica>> // O Flow permite que a UI observe as alterações de forma reativa

    /**
     * Insere uma lista de clínicas na base de dados
     * Se uma clínica com o mesmo ID já existir ela será substituída
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Define a operação de inserção com estratégia de substituição
    suspend fun insertAll(clinicas: List<Clinica>) // Função suspensa para ser chamada a partir de uma coroutine

    /**
     * Apaga todas as clínicas da tabela
     * Útil para limpar os dados antigos antes de inserir uma lista nova vinda da API
     */
    @Query("DELETE FROM clinicas") // Query SQL para apagar todos os registos da tabela
    suspend fun clearAll() // Função suspensa para a operação de limpeza
}
