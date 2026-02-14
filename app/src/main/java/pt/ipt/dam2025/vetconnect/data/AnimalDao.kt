package pt.ipt.dam2025.vetconnect.data

import androidx.room.Dao // Importa a anotação para identificar a interface como um DAO
import androidx.room.Insert // Importa a anotação para funções de inserção
import androidx.room.OnConflictStrategy // Importa as estratégias de conflito para inserções
import androidx.room.Query // Importa a anotação para definir queries SQL
import kotlinx.coroutines.flow.Flow // Importa a classe Flow para streams de dados assíncronos
import pt.ipt.dam2025.vetconnect.model.AnimalResponse

/**
 * DAO para a entidade Animal
 * Define todas as operações de base de dados para a tabela 'animais'
 */
@Dao // Anotação que marca esta interface como um Data Access Object para o Room
interface AnimalDao {

    /**
     * Insere um novo animal ou atualiza um existente
     * Se um animal com o mesmo ID já existir na tabela ele será substituído
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Define a operação de inserção com estratégia de conflito
    suspend fun insertOrUpdate(animal: AnimalResponse) // Função suspensa para ser chamada a partir de uma coroutine

    /**
     * Insere uma lista de animais ou atualiza os existentes
     * Útil para popular a base de dados com dados vindos da API
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Usa a mesma estratégia de substituição
    suspend fun insertAll(animais: List<AnimalResponse>) // Função suspensa para inserir múltiplos animais

    /**
     * Obtém todos os animais de um tutor específico ordenados por nome
     * Retorna um Flow que emite a lista de animais sempre que os dados mudam na tabela
     * A UI pode observar este Flow para se atualizar automaticamente
     */
    @Query("SELECT * FROM animais WHERE tutorId = :tutorId ORDER BY nome ASC") // Query SQL para selecionar animais
    fun getAnimalsByTutorId(tutorId: Int): Flow<List<AnimalResponse>> // O Flow permite observação reativa

    /**
     * Obtém um animal específico pelo seu ID
     * Retorna um Flow que emite o animal sempre que os seus dados mudam
     */
    @Query("SELECT * FROM animais WHERE id = :id LIMIT 1") // Query SQL para selecionar um animal
    fun getById(id: Int): Flow<AnimalResponse?> // O Flow notifica a UI de atualizações no perfil do animal

    /**
     * Obtém um animal específico pelo seu ID para uma operação única e imediata
     * Por ser 'suspend' não retorna um Flow e apenas devolve o valor uma vez
     */
    @Query("SELECT * FROM animais WHERE id = :id LIMIT 1") // Query SQL idêntica
    suspend fun getAnimalById(id: Int): AnimalResponse? // Função suspensa para uma única leitura

    /**
     * Apaga um animal da base de dados usando o seu ID
     */
    @Query("DELETE FROM animais WHERE id = :id") // Query SQL para apagar um animal
    suspend fun deleteById(id: Int) // Função suspensa para a operação de apagar
}
