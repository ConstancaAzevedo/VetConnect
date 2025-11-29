package pt.ipt.dam2025.trabalho.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pt.ipt.dam2025.trabalho.model.Animal
import pt.ipt.dam2025.trabalho.model.HistoricoItem
import pt.ipt.dam2025.trabalho.model.User

/**
 * Classe principal da base de dados da aplicação.
 */
@Database(entities = [HistoricoItem::class, User::class, Animal::class], version = 5, exportSchema = false) // <-- VERSÃO INCREMENTADA
abstract class AppDatabase : RoomDatabase() {

    abstract fun historicoDao(): HistoricoDao
    abstract fun userDao(): UserDao
    abstract fun animalDao(): AnimalDao

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
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
