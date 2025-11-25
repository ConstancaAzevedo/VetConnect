package pt.ipt.dam2025.trabalho.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pt.ipt.dam2025.trabalho.model.HistoricoItem
import pt.ipt.dam2025.trabalho.model.User


/**
 * a classe principal da base de dados da aplicação
 * anotada com @Database para indicar que é uma base de dados "Room"
 * define as entidades (tabelas) e a versão da base de dados
 */
@Database(entities = [HistoricoItem::class, User::class], version = 2, exportSchema = false)
// entities - é a lista de todas as tabelas que a base de dados vai ter (por agora, só 1)
// version=1 - esta é a versão do esquema, se a tabela tivesse mais uma coluna, aumentaria para 2
// o Room não vai guardar o histórico das versões da base de dados num ficheiro

abstract class AppDatabase : RoomDatabase() {
// abstract - porque o Room é que vai gerar t0do o código de implementação


    // DAO para a tabela de histórico
    abstract fun historicoDao(): HistoricoDao
    // para cada tabela tem que se declarar uma função correspondente que retorna o DAO para a tabela
    // funciona como uma "porta de acesso" para a tabela


    // DAO para a nova tabela de utilizadores
    abstract fun userDao(): UserDao

    companion object {
        // garante que existe apenas uma instância desta base de dados em toda a aplicação (Singleton)
        // Garante que existe apenas uma instância da base de dados (padrão Singleton)
        @Volatile
        private var INSTANCE: AppDatabase? = null // cria uma variável para guardar a instância única

        // função que todos irão usar para obter a instância da base de dados
        fun getDatabase(context: Context): AppDatabase {


            return INSTANCE ?: synchronized(this) {
                //?: - se a instância não for nula (já existe), devolve-a imediatamente, se não executa o código a seguir
                // synchronized(this) - faz com que apena uma parte da app tente criar a base de dados ao mesmo tempo
                // evitando assim criar duas instâncias por acidente

                // contrói a base de dados
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vetconnect_database"
                )
                // Numa aplicação real, seria preciso um plano de migração.
                // Para desenvolvimento, destruir e recriar a base de dados é suficiente.
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
