package pt.ipt.dam2025.vetconnect.data

import androidx.room.Dao // Importa a anotação para identificar a interface como um DAO
import androidx.room.Insert // Importa a anotação para funções de inserção
import androidx.room.OnConflictStrategy // Importa as estratégias de conflito para inserções
import androidx.room.Query // Importa a anotação para definir queries SQL
import kotlinx.coroutines.flow.Flow // Importa a classe Flow para streams de dados assíncronos
import pt.ipt.dam2025.vetconnect.model.Vacina

/**
 * DAO para a entidade Vacina
 * Define todas as operações de base de dados para a tabela 'vacinas'
 */
@Dao
interface VacinaDao {

    /**
     * Obtém todas as vacinas de um animal específico ordenadas por data (da mais recente para a mais antiga)
     * Retorna um Flow que a UI pode observar para se atualizar automaticamente
     */
    @Query("SELECT * FROM vacinas WHERE animalId = :animalId ORDER BY data_agendada DESC") // Query SQL para selecionar e ordenar as vacinas
    fun getVacinasByAnimal(animalId: Int): Flow<List<Vacina>> // O Flow permite observação reativa dos dados

    /**
     * Obtém uma lista de vacinas para uma data específica
     * Usado pelo VaccineReminderWorker para verificar se existem vacinas agendadas
     */
    @Query("SELECT * FROM vacinas WHERE data_agendada LIKE :date || '%' ") // Query SQL para encontrar vacinas numa data
    suspend fun getVaccinesForDate(date: String): List<Vacina> // Função suspensa para uma única leitura

    /**
     * Insere uma lista de vacinas na base de dados
     * Se uma vacina com o mesmo ID já existir ela será substituída
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Define a operação de inserção com estratégia de substituição
    suspend fun insert(vacinas: List<Vacina>) // Função suspensa para inserir múltiplos registos

    /**
     * Apaga uma vacina específica pelo seu ID
     */
    @Query("DELETE FROM vacinas WHERE id = :vacinaId") // Query SQL para apagar uma vacina pelo ID
    suspend fun delete(vacinaId: Int) // Função suspensa para a operação de apagar

    /**
     * Apaga todas as vacinas de um animal específico da base de dados
     */
    @Query("DELETE FROM vacinas WHERE animalId = :animalId") // Query SQL para apagar todas as vacinas de um animal
    suspend fun deleteByAnimal(animalId: Int) // Função suspensa para a operação de limpeza
}
