package pt.ipt.dam2025.trabalho

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * a classe principal da base de dados da aplicação
 * anotada com @Database para indicar que é uma base de dados Room
 * define as entidades (tabelas) e a versão da base de dados
 */
@Database(entities = [HistoricoItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    /**
     * fornece acesso ao DAO do histórico
     */
    abstract fun historicoDao(): HistoricoDao

    /**
     * companion object para fornecer uma instância única (Singleton) da base de dados
     * isto previne que múltiplas instâncias da base de dados sejam abertas ao mesmo tempo
     */
    companion object {
        // a anotação @Volatile garante que o valor de INSTANCE está sempre atualizado
        // e é o mesmo para todos os threads de execução
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // se a instância já existir, retorna-a
            // se não, cria a base de dados
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vetconnect_database" // Nome do ficheiro da base de dados
                ).build()
                INSTANCE = instance
                // retorna a instância
                instance
            }
        }
    }
}
