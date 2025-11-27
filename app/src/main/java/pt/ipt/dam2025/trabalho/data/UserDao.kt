package pt.ipt.dam2025.trabalho.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import pt.ipt.dam2025.trabalho.model.User

@Dao
interface UserDao {
    
    // Insere um utilizador. Se já existir, substitui-o.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    // Atualiza um utilizador existente.
    @Update
    suspend fun update(user: User)

    // Encontra um utilizador pelo seu email.
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    // Obtém o token do primeiro (e único) utilizador na tabela.
    @Query("SELECT token FROM users LIMIT 1")
    suspend fun getAuthToken(): String?
}
