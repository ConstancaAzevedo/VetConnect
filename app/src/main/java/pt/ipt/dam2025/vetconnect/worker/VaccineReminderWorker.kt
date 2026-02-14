package pt.ipt.dam2025.vetconnect.worker

import android.app.NotificationChannel // Importa para criar canais de notificação
import android.app.NotificationManager // Importa para gerir notificações
import android.content.Context // Importa o contexto da aplicação
import androidx.core.app.NotificationCompat // Importa para construir notificações compatíveis
import androidx.work.CoroutineWorker // Importa a classe base para workers com coroutines
import androidx.work.WorkerParameters // Importa os parâmetros para o worker
import pt.ipt.dam2025.vetconnect.R // Importa os recursos da aplicação
import pt.ipt.dam2025.vetconnect.api.ApiClient
import pt.ipt.dam2025.vetconnect.data.AppDatabase
import pt.ipt.dam2025.vetconnect.repository.VacinaRepository
import pt.ipt.dam2025.vetconnect.util.SessionManager

/**
 * Worker que corre em segundo plano para enviar lembretes de vacinas
 * Verifica diariamente se existem vacinas agendadas para o dia seguinte
 */
class VaccineReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    /**
     * O trabalho principal que este worker vai executar
     * Este métdo corre numa thread de fundo
     */
    override suspend fun doWork(): Result {
        return try {
            // Obtém o token de autenticação da sessão
            val sessionManager = SessionManager(applicationContext)
            val token = sessionManager.getAuthToken() ?: return Result.failure()

            // Cria uma instância do repositório
            val database = AppDatabase.getDatabase(applicationContext)
            val repository = VacinaRepository(
                ApiClient.apiService,
                database.vacinaDao(),
                database.tipoVacinaDao(),
                database.clinicaDao(),
                database.veterinarioDao()
            )

            // Chama a API para obter as vacinas próximas
            val result = repository.getVacinasProximas(token)

            result.onSuccess { response ->
                // Itera sobre a lista de vacinas que está DENTRO do objeto de resposta
                response.vacinas.forEach { vaccine ->
                    // O nome do animal já vem na resposta da API
                    val animalNome = vaccine.animalNome ?: "o seu animal"
                    sendNotification(animalNome, vaccine.tipo)
                }
            }
            // Retorna sucesso indicando que o trabalho foi concluído
            Result.success()
        } catch (_: Exception) {
            // Retorna falha indicando que o trabalho não foi concluído
            Result.failure()
        }
    }

    /**
     * Cria e envia uma notificação para o dispositivo do utilizador
     */
    private fun sendNotification(animalNome: String, tipoVacina: String) {
        // Obtém o serviço de notificações do sistema Android
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Cria um canal de notificação
        val channel = NotificationChannel(
            CHANNEL_ID, //ID único para o canal
            "Lembretes de Vacinas", // Nome do canal visivel para o utilziador
            NotificationManager.IMPORTANCE_HIGH // Define a importancia como alta
        )
        // Regista o canal no sistema
        notificationManager.createNotificationChannel(channel)

        // Constrói a notificação
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Lembrete de Vacina")
            .setContentText("A vacina '$tipoVacina' de $animalNome está agendada para breve!")
            .setSmallIcon(R.drawable.vetconnectfundoredondo)
            .build()

        // Envia a notificação para o sistema
        // Usa o tempo atual como ID para garantir que cada notificação é única
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    /**
     * Companion object para guardar constantes relacionadas com o worker
     */
    companion object {
        // Nome único para identificar este trabalho no WorkManager
        const val WORK_NAME = "VaccineReminderWork"
        // ID único para o canal de notificação
        private const val CHANNEL_ID = "vaccine_reminder_channel"
    }
}
