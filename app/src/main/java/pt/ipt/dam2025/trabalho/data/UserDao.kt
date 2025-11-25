package pt.ipt.dam2025.trabalho.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import pt.ipt.dam2025.trabalho.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE identifier = :identifier LIMIT 1")
    suspend fun findByIdentifier(identifier: String): User?

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getAnyUser(): User? //verificar se já existe um utilizador na base de dados
}
