package pt.ipt.dam2025.vetconnect.data

import android.content.Context // Importa o contexto da aplicação
import androidx.room.Database // Importa a anotação para definir a base de dados
import androidx.room.Room // Importa a classe principal do Room para construir a base de dados
import androidx.room.RoomDatabase // Importa a classe base para a base de dados
import pt.ipt.dam2025.vetconnect.model.*

/**
 * Classe principal da base de dados da aplicação usando Room
 * É o ponto de acesso central para a base de dados persistente da aplicação
 */

@Database(
    // Lista de todas as classes de entidade que a base de dados irá conter
    entities = [
        Utilizador::class, // Tabela para os utilizadores
        AnimalResponse::class, // Tabela para os animais
        Exame::class, // Tabela para os exames
        TipoExame::class, // Tabela para os tipos de exame
        Vacina::class, // Tabela para as vacinas
        TipoVacina::class, // Tabela para os tipos de vacina
        Consulta::class, // Tabela para as consultas
        Clinica::class, // Tabela para as clínicas
        Veterinario::class // Tabela para os veterinários
    ],
    version = 16, // Versão do esquema da base de dados deve ser incrementada a cada alteração
    exportSchema = false // Não exporta o esquema da base de dados para um ficheiro JSON
)
abstract class AppDatabase : RoomDatabase() {

    // O Room irá gerar a implementação para estas funções
    abstract fun userDao(): UserDao
    abstract fun animalDao(): AnimalDao
    abstract fun exameDao(): ExameDao
    abstract fun tipoExameDao(): TipoExameDao
    abstract fun vacinaDao(): VacinaDao
    abstract fun tipoVacinaDao(): TipoVacinaDao
    abstract fun consultaDao(): ConsultaDao
    abstract fun clinicaDao(): ClinicaDao
    abstract fun veterinarioDao(): VeterinarioDao

    /**
     * Companion object para implementar o padrão Singleton
     * Garante que apenas uma instância da base de dados é criada em toda a aplicação
     */
    companion object {

        // A anotação @Volatile garante que a variável INSTANCE é sempre lida da memória principal
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtém a instância única da base de dados
         * Se a instância não existir cria-a de forma segura
         */
        fun getDatabase(context: Context): AppDatabase {
            // Retorna a instância se já existir
            return INSTANCE ?: synchronized(this) { // Bloco synchronized para evitar que múltiplas threads criem a BD ao mesmo tempo
                // Cria a instância da base de dados usando o Room.databaseBuilder
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Contexto global da aplicação
                    AppDatabase::class.java, // A classe da base de dados
                    "vetconnect_database" // O nome do ficheiro da base de dados no dispositivo
                )
                // Define que a app deve falhar se uma migração for necessária mas não fornecida
                .fallbackToDestructiveMigration(false)
                .build() // Constrói a instância da base de dados
                INSTANCE = instance // Guarda a instância na variável estática
                instance // Retorna a instância recém-criada
            }
        }
    }
}
