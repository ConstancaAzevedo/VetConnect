package pt.ipt.dam2025.vetconnect.data

import androidx.room.Dao // Importa a anotação para identificar a interface como um DAO
import androidx.room.Insert // Importa a anotação para funções de inserção
import androidx.room.OnConflictStrategy // Importa as estratégias de conflito para inserções
import androidx.room.Query // Importa a anotação para definir queries SQL
import kotlinx.coroutines.flow.Flow // Importa a classe Flow para streams de dados assíncronos
import pt.ipt.dam2025.vetconnect.model.TipoVacina

/**
 * DAO para a entidade TipoVacina
 * Define todas as operações de base de dados para a tabela 'tipos_vacina'
 */
@Dao
interface TipoVacinaDao {

    /**
     * Obtém todos os tipos de vacina guardados na base de dados ordenados por nome
     * Retorna um Flow que emite a lista sempre que os dados na tabela mudam
     */
    @Query("SELECT * FROM tipos_vacina ORDER BY nome ASC") // Query SQL para selecionar e ordenar os tipos de vacina
    fun getTiposVacina(): Flow<List<TipoVacina>> // O Flow permite que a UI observe as alterações de forma reativa

    /**
     * Insere uma lista de tipos de vacina na base de dados
     * Se um tipo de vacina com o mesmo ID já existir ele será substituído
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Define a operação de inserção com estratégia de substituição
    suspend fun insertAll(tiposVacina: List<TipoVacina>) // Função suspensa para ser chamada a partir de uma coroutine

    /**
     * Apaga todos os tipos de vacina da tabela
     * Útil para limpar os dados antigos antes de inserir uma lista nova vinda da API
     */
    @Query("DELETE FROM tipos_vacina") // Query SQL para apagar todos os registos da tabela
    suspend fun clearAll() // Função suspensa para a operação de limpeza
}
