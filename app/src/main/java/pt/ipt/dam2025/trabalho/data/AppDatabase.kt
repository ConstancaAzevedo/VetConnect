package pt.ipt.dam2025.trabalho.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pt.ipt.dam2025.trabalho.model.AnimalResponse
import pt.ipt.dam2025.trabalho.model.Clinica
import pt.ipt.dam2025.trabalho.model.Consulta
import pt.ipt.dam2025.trabalho.model.Exame
import pt.ipt.dam2025.trabalho.model.Receita
import pt.ipt.dam2025.trabalho.model.TipoVacina
import pt.ipt.dam2025.trabalho.model.Usuario
import pt.ipt.dam2025.trabalho.model.Vacina
import pt.ipt.dam2025.trabalho.model.Veterinario

/**
 * Classe principal da base de dados da aplicação
 */
@Database(
    entities = [
        Usuario::class,
        AnimalResponse::class,
        Receita::class,
        Exame::class,
        Vacina::class,
        TipoVacina::class,
        Consulta::class,
        Clinica::class,
        Veterinario::class
    ],
    version = 14, // Incrementar a versão devido à mudança de esquema
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Funções abstratas que retornam os DAOs correspondentes a cada entidade
    abstract fun userDao(): UserDao
    abstract fun animalDao(): AnimalDao
    abstract fun receitaDao(): ReceitaDao
    abstract fun exameDao(): ExameDao
    abstract fun vacinaDao(): VacinaDao
    abstract fun consultaDao(): ConsultaDao
    abstract fun clinicaDao(): ClinicaDao
    abstract fun veterinarioDao(): VeterinarioDao

    // Objeto companion para fornecer uma instância única do banco de dados
    companion object {

        // Variável volátil para garantir que a instância seja sempre lida a partir do cache
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Função para obter a instância do banco de dados
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
