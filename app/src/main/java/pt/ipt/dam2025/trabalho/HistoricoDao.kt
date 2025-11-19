package pt.ipt.dam2025.trabalho

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para a tabela historico_medico.
 * É aqui que se definem as interações com a base de dados.
 */
@Dao
interface HistoricoDao {

    /**
     * Insere um novo item na tabela. Se já existir, substitui-o.
     */
    @Insert
    suspend fun insert(item: HistoricoItem)

    /**
     * Apaga um item da tabela.
     */
    @Delete
    suspend fun delete(item: HistoricoItem)

    /**
     * Obtém todos os itens do histórico, ordenados pela data mais recente primeiro.
     * Retorna um Flow, que permite que a UI seja atualizada automaticamente quando os dados mudam.
     */
    @Query("SELECT * FROM historico_medico ORDER BY data DESC")
    fun getAll(): Flow<List<HistoricoItem>>
}
