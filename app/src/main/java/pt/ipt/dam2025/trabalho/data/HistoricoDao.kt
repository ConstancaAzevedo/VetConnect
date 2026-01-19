package pt.ipt.dam2025.trabalho.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.trabalho.model.HistoricoItem

/**
 *DAO responsável por todas as interações com a tabela de historico_item
 */
@Dao
interface HistoricoDao {


    //Insere o objeto historicoitem na tabela; se já existir, substitui
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HistoricoItem)


    //apaga um item da tabela
    @Delete
    suspend fun delete(item: HistoricoItem)

    /**
     * Apaga todos os itens da tabela.
     */
    @Query("DELETE FROM historico_medico")
    suspend fun deleteAll()

    /*
     * obtém todos os itens do histórico ordenados pela data mais recente primeiro
     * retorna um Flow que permite que a UI seja atualizada automaticamente quando os dados mudam
     */
    @Query("SELECT * FROM historico_medico ORDER BY SUBSTR(data, 7, 4) DESC, SUBSTR(data, 4, 2) DESC, SUBSTR(data, 1, 2) DESC")
    //7 e 4 - ano ; 4 e 2 - mês ; 1 e 2 - dia
    fun getAll(): Flow<List<HistoricoItem>>
}
