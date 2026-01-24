package pt.ipt.dam2025.trabalho.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pt.ipt.dam2025.trabalho.model.TipoVacina

// DAO para a entidade TipoVacina
@Dao
interface TipoVacinaDao {

    // Insere ou atualiza uma lista de tipos de vacinas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tiposVacina: List<TipoVacina>)

    // Obtém todos os tipos de vacinas
    @Query("SELECT * FROM tipos_vacina")
    suspend fun getTiposVacina(): List<TipoVacina>

    // Apaga todos os tipos de vacinas
    @Query("DELETE FROM tipos_vacina")
    suspend fun clearAll()
}
