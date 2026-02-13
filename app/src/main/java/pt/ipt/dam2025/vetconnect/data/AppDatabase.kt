package pt.ipt.dam2025.vetconnect.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pt.ipt.dam2025.vetconnect.model.AnimalResponse
import pt.ipt.dam2025.vetconnect.model.Clinica
import pt.ipt.dam2025.vetconnect.model.Consulta
import pt.ipt.dam2025.vetconnect.model.Exame
import pt.ipt.dam2025.vetconnect.model.TipoExame
import pt.ipt.dam2025.vetconnect.model.TipoVacina
import pt.ipt.dam2025.vetconnect.model.Utilizador
import pt.ipt.dam2025.vetconnect.model.Vacina
import pt.ipt.dam2025.vetconnect.model.Veterinario

/**
 * Classe principal da base de dados da aplicação
 */

@Database(
    entities = [
        Utilizador::class,
        AnimalResponse::class,
        Exame::class,
        TipoExame::class, 
        Vacina::class,
        TipoVacina::class,
        Consulta::class,
        Clinica::class,
        Veterinario::class
    ],
    version = 16, // incrementae a versão após alteração do esquema
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // funções abstratas que retornam os DAOs correspondentes a cada entidade
    abstract fun userDao(): UserDao
    abstract fun animalDao(): AnimalDao
    abstract fun exameDao(): ExameDao
    abstract fun tipoExameDao(): TipoExameDao
    abstract fun vacinaDao(): VacinaDao
    abstract fun tipoVacinaDao(): TipoVacinaDao // Adicionado
    abstract fun consultaDao(): ConsultaDao
    abstract fun clinicaDao(): ClinicaDao
    abstract fun veterinarioDao(): VeterinarioDao

    // objeto companion para fornecer uma instância única do banco de dados
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
                .fallbackToDestructiveMigration(false)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
