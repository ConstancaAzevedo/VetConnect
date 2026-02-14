package pt.ipt.dam2025.vetconnect.data

import androidx.room.Dao // Importa a anotação para identificar a interface como um DAO
import androidx.room.Insert // Importa a anotação para funções de inserção
import androidx.room.OnConflictStrategy // Importa as estratégias de conflito para inserções
import androidx.room.Query // Importa a anotação para definir queries SQL
import kotlinx.coroutines.flow.Flow // Importa a classe Flow para streams de dados assíncronos
import pt.ipt.dam2025.vetconnect.model.Exame

/**
 * DAO para a entidade Exame
 * Define todas as operações de base de dados para a tabela 'exames'
 */
@Dao
interface ExameDao {

    /**
     * Insere um único exame na base de dados
     * Se o exame já existir (pelo ID) será substituído
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Define a operação de inserção com estratégia de substituição
    suspend fun insert(exame: Exame) // Função suspensa para ser chamada a partir de uma coroutine

    /**
     * Insere uma lista de exames substituindo os existentes em caso de conflito
     * Útil para sincronizar os dados vindos da API
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Usa a mesma estratégia de substituição
    suspend fun insertAll(exames: List<Exame>) // Função suspensa para inserir múltiplos exames

    /**
     * Obtém todos os exames de um animal específico ordenados por data (do mais recente para o mais antigo)
     * Retorna um Flow que a UI pode observar para se atualizar automaticamente
     */
    @Query("SELECT * FROM exames WHERE animalId = :animalId ORDER BY dataExame DESC") // Query SQL para selecionar e ordenar os exames
    fun getExamesByAnimal(animalId: Int): Flow<List<Exame>> // O Flow permite observação reativa dos dados

    /**
     * Apaga um exame específico pelo seu ID
     */
    @Query("DELETE FROM exames WHERE id = :id") // Query SQL para apagar um exame por ID
    suspend fun deleteById(id: Int) // Função suspensa para a operação de apagar

    /**
     * Apaga todos os exames de um animal específico da base de dados
     */
    @Query("DELETE FROM exames WHERE animalId = :animalId") // Query SQL para apagar todos os exames de um animal
    suspend fun deleteByAnimal(animalId: Int) // Função suspensa para a operação de limpeza
}
