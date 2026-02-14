package pt.ipt.dam2025.vetconnect.data

import androidx.room.Dao // Importa a anotação para identificar a interface como um DAO
import androidx.room.Insert // Importa a anotação para funções de inserção
import androidx.room.OnConflictStrategy // Importa as estratégias de conflito para inserções
import androidx.room.Query // Importa a anotação para definir queries SQL
import kotlinx.coroutines.flow.Flow // Importa a classe Flow para streams de dados assíncronos
import pt.ipt.dam2025.vetconnect.model.Veterinario

/**
 * DAO para a entidade Veterinario
 * Define todas as operações de base de dados para a tabela 'veterinarios'
 */
@Dao
interface VeterinarioDao {

    /**
     * Obtém todos os veterinários guardados na base de dados ordenados por nome
     */
    @Query("SELECT * FROM veterinarios ORDER BY nome ASC") // Query SQL para selecionar e ordenar todos os veterinários
    fun getAll(): Flow<List<Veterinario>> // O Flow permite que a UI observe as alterações de forma reativa

    /**
     * Obtém os veterinários de uma clínica específica ordenados por nome
     */
    @Query("SELECT * FROM veterinarios WHERE clinicaId = :clinicaId ORDER BY nome ASC") // Query SQL para selecionar e ordenar os veterinários de uma clínica
    fun getVeterinariosByClinica(clinicaId: Int): Flow<List<Veterinario>> // O Flow permite observação reativa

    /**
     * Insere uma lista de veterinários na base de dados
     * Se um veterinário com o mesmo ID já existir ele será substituído
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Define a operação de inserção com estratégia de substituição
    suspend fun insertAll(veterinarios: List<Veterinario>) // Função suspensa para inserir múltiplos registos

    /**
     * Apaga todos os veterinários de uma clínica específica
     * Útil para limpar os dados antes de sincronizar com a API
     */
    @Query("DELETE FROM veterinarios WHERE clinicaId = :clinicaId") // Query SQL para apagar os veterinários de uma clínica
    suspend fun deleteByClinica(clinicaId: Int) // Função suspensa para a operação de apagar

    /**
     * Apaga todos os veterinários da tabela
     */
    @Query("DELETE FROM veterinarios") // Query SQL para apagar todos os registos da tabela
    suspend fun clearAll() // Função suspensa para a operação de limpeza total
}
