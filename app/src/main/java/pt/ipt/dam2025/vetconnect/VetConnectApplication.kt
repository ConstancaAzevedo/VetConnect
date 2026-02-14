package pt.ipt.dam2025.vetconnect

import android.app.Application
import androidx.work.Constraints
import androidx.work.NetworkType // Importa o tipo de rede para as restrições do WorkManager
import androidx.work.PeriodicWorkRequestBuilder // Importa a classe para construir um pedido de trabalho periódico
import androidx.work.WorkManager // Importa o WorkManager para agendar tarefas em segundo plano
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.worker.VaccineReminderWorker
import java.util.concurrent.TimeUnit // Importa a unidade de tempo para definir a periodicidade do trabalho

/**
 * Classe de Aplicação personalizada para o VetConnect
 * É o ponto de entrada da aplicação e é usada para inicializar componentes globais
 * como a base de dados e agendar tarefas de fundo
 */
class VetConnectApplication : Application() {

    /**
     * Este métdo é chamado quando a aplicação é criada
     * Serve para inicializar componentes que precisam de existir durante tdo o ciclo da aplicação
     */
    override fun onCreate() {
        super.onCreate() // Chama a implementação do métdo onCreate da classe Application
        // Inicializa o SessionManager que gere o token de autenticação do utilizador
        val sessionManager = SessionManager(this)
        // Inicializa o ApiClient com o gestor de sessão
        ApiClient.init(sessionManager)
        // Chama a função para configurar e agendar o trabalho de fundo recorrente
        setupRecurringWork()
    }

    /**
     * Configura e agenda uma tarefa periódica usando o WorkManager
     * Esta tarefa irá verificar os lembretes de vacinas
     */
    private fun setupRecurringWork() {
        // Define as restrições para a execução da tarefa
        val constraints = Constraints.Builder()
            // Exige que o dispositivo esteja conectado à internet para a tarefa ser executada
            .setRequiredNetworkType(NetworkType.CONNECTED)
            // Constrói o objeto de restrições
            .build()

        // Cria um pedido para uma tarefa que se repete
        val repeatingRequest = PeriodicWorkRequestBuilder<VaccineReminderWorker>(
            1, TimeUnit.DAYS // Define a frequência da tarefa: 1 vez por dia
        )
        // Aplica as restrições definidas anteriormente ao pedido
        .setConstraints(constraints)
        // Constrói o objeto do pedido de trabalho
        .build()

        // Obtém a instância do WorkManager para o contexto da aplicação
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            // Um nome único para a tarefa para evitar que seja agendada múltiplas vezes
            VaccineReminderWorker.WORK_NAME,
            // Quando já existe uma tarefa com o mesmo nome: KEEP -> mantém a tarefa existente
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            // O pedido de trabalho periódico
            repeatingRequest
        )
    }
}
