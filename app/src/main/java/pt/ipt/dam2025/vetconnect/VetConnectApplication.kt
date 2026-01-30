package pt.ipt.dam2025.vetconnect

import android.app.Application
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.data.AppDatabase
import pt.ipt.dam2025.vetconnect.util.SessionManager
import pt.ipt.dam2025.vetconnect.worker.VaccineReminderWorker
import java.util.concurrent.TimeUnit

/**
 * classe de Aplicação personalizada para o VetConnect
 * é usada para inicializar e manter uma instância única da base de dados
 */
class VetConnectApplication : Application() {
    // a base de dados será criada de forma "lazy" (preguiçosa), ou seja,
    // só na primeira vez que for acedida
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
        // Inicializa o SessionManager
        val sessionManager = SessionManager(this)
        // Inicializa o ApiClient com o token de autenticação
        ApiClient.init(sessionManager)
        setupRecurringWork()
    }

    private fun setupRecurringWork() {
        // Definir restrições para a tarefa (ex: só executar com ligação à net)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Criar o pedido de trabalho periódico para ser executado uma vez por dia
        val repeatingRequest = PeriodicWorkRequestBuilder<VaccineReminderWorker>(
            1, TimeUnit.DAYS
        )
        .setConstraints(constraints)
        .build()

        // Agendar a tarefa, garantindo que não há duplicados
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            VaccineReminderWorker.WORK_NAME,
            androidx.work.ExistingPeriodicWorkPolicy.KEEP, // Mantém a tarefa existente se já estiver agendada
            repeatingRequest
        )
    }
}
