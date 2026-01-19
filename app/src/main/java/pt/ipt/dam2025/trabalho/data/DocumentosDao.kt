package pt.ipt.dam2025.trabalho.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ipt.dam2025.trabalho.model.Exame
import pt.ipt.dam2025.trabalho.model.Receita
import pt.ipt.dam2025.trabalho.model.Vacina

@Dao
interface ReceitaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(receita: Receita)

    @Query("SELECT * FROM receitas ORDER BY data DESC")
    fun getAllReceitas(): Flow<List<Receita>>
}

@Dao
interface ExameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exame: Exame)

    @Query("SELECT * FROM exames ORDER BY data DESC")
    fun getAllExames(): Flow<List<Exame>>
}

@Dao
interface VacinaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vacina: Vacina)

    @Query("SELECT * FROM vacinas ORDER BY data DESC")
    fun getAllVacinas(): Flow<List<Vacina>>
}
