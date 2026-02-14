package pt.ipt.dam2025.vetconnect.worker

import android.app.NotificationChannel // Importa para criar canais de notificação
import android.app.NotificationManager // Importa para gerir notificações
import android.content.Context // Importa o contexto da aplicação
import androidx.core.app.NotificationCompat // Importa para construir notificações compatíveis
import androidx.work.CoroutineWorker // Importa a classe base para workers com coroutines
import androidx.work.WorkerParameters // Importa os parâmetros para o worker
import pt.ipt.dam2025.vetconnect.R // Importa os recursos da aplicação
import pt.ipt.dam2025.vetconnect.data.AppDatabase
import java.text.SimpleDateFormat // Importa para formatar datas
import java.util.Calendar // Importa para manipular datas e horas
import java.util.Locale // Importa para definir a localização para formatação

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
            // Obtém uma instância da base de dados Room
            val database = AppDatabase.getDatabase(applicationContext)
            // Obtém o DAO para as vacinas
            val vacinaDao = database.vacinaDao()
            // Obtém o DAO para os animais
            val animalDao = database.animalDao()

            // Obtém a data atual
            val calendar = Calendar.getInstance()
            // Adiciona sete dias à data atual para obter a data alvo
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            // Formata a data alvo para o formato ano-mês-dia
            val targetDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            // Procura na base de dados por vacinas agendadas para a data alvo
            val upcomingVaccines = vacinaDao.getVaccinesForDate(targetDate)

            // Itera sobre cada vacina encontrada
            upcomingVaccines.forEach { vaccine ->
                // Obtém os detalhes do animal associado a esta vacina
                val animal = animalDao.getAnimalById(vaccine.animalId)
                // Se o animal existir
                animal?.let { animalObject ->
                    // E se a vacina tiver uma data agendada
                    vaccine.dataAgendada?.let {
                        // Chama a função para enviar a notificação
                        sendNotification(
                            animalObject.nome,
                            vaccine.tipo
                        )
                    }
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
            CHANNEL_ID, // ID único para o canal
            "Lembretes de Vacinas", // Nome do canal visível para o utilizador
            NotificationManager.IMPORTANCE_HIGH // Define a importância como alta
        )
        // Regista o canal no sistema
        notificationManager.createNotificationChannel(channel)

        // Constrói a notificação
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Notificação de Vacina") // Define o título da notificação
            // Define o texto principal da notificação
            .setContentText("A vacina '$tipoVacina' para o seu animal '$animalNome' está agendada para daqui a 7 dias")
            .setSmallIcon(R.drawable.vetconnectfundoredondo) // Define o ícone pequeno da notificação
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
