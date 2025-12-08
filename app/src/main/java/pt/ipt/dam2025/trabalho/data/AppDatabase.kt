package pt.ipt.dam2025.trabalho.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pt.ipt.dam2025.trabalho.model.Animal
import pt.ipt.dam2025.trabalho.model.HistoricoItem
import pt.ipt.dam2025.trabalho.model.User

/**
 * Classe principal da base de dados da aplicação
 */

@Database(entities = [HistoricoItem::class, User::class, Animal::class], version = 7, exportSchema = true)
//a lista de todas as classes de entidades que estão na base de dados;
//incrementar a versão sempre que se faz alterações na base de dados
abstract class AppDatabase : RoomDatabase() {


    //funções abstratas que retornam os DAOs correspondentes a cada entidade
    abstract fun historicoDao(): HistoricoDao
    abstract fun userDao(): UserDao
    abstract fun animalDao(): AnimalDao

    //Bloco que contém membros estáticos, o conteúdo pertence à AppDataBase em si, não a uma instância dela
    companion object {
        @Volatile //garante que o valor da variável INSTANCE será sempre a mais recente
        private var INSTANCE: AppDatabase? = null

        //retorna a instância da base de dados
        fun getDatabase(context: Context): AppDatabase {
            //se já existir uma instância devolve-a; caso contrário cria uma nova; synchronized garante que só uma thread acesse o código por vez
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, //contexto da aplicação
                    AppDatabase::class.java, //classe da base de dados
                    "vetconnect_database" //nome do ficheiro de base de dados
                )
                .fallbackToDestructiveMigration() //apaga completamente a base de dados antiga e cria uma nova
                //APGAR ESTA LINHA ANTES DE PUBLICAR A APLICAÇÃO
                .build()
                INSTANCE = instance
                instance //devolve a instância
            }
        }
    }
}
