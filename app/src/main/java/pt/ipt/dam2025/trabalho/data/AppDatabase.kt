package pt.ipt.dam2025.trabalho.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pt.ipt.dam2025.trabalho.model.Animal
import pt.ipt.dam2025.trabalho.model.Exame
import pt.ipt.dam2025.trabalho.model.Receita
import pt.ipt.dam2025.trabalho.model.User
import pt.ipt.dam2025.trabalho.model.Vacina

/**
 * Classe principal da base de dados da aplicação
 */
@Database(entities = [User::class, Animal::class, Receita::class, Exame::class, Vacina::class], version = 9, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    // Funções abstratas que retornam os DAOs correspondentes a cada entidade
    abstract fun userDao(): UserDao
    abstract fun animalDao(): AnimalDao
    abstract fun receitaDao(): ReceitaDao
    abstract fun exameDao(): ExameDao
    abstract fun vacinaDao(): VacinaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vetconnect_database"
                )
                .fallbackToDestructiveMigration() // Recria a BD se não houver migração
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
