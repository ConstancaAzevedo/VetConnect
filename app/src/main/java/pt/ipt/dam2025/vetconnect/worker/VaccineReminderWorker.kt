package pt.ipt.dam2025.vetconnect.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.data.AppDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Classe para o worker de notificação
 */
class VaccineReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(applicationContext)
            val vacinaDao = database.vacinaDao()
            val animalDao = database.animalDao()

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val tomorrow = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            val upcomingVaccines = vacinaDao.getVaccinesForDate(tomorrow)

            upcomingVaccines.forEach { vaccine ->
                val animal = animalDao.getAnimalById(vaccine.animalId)
                animal?.let { animalObject ->
                    vaccine.dataAgendada?.let { dataAgendadaString ->
                        sendNotification(
                            animalObject.nome,
                            vaccine.tipo,
                            dataAgendadaString
                        )
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun sendNotification(animalNome: String, tipoVacina: String, dataVacina: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Lembretes de Vacinas",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Lembrete de Vacina")
            .setContentText("A vacina '$tipoVacina' para o seu animal '$animalNome' está agendada para amanhã.")
            .setSmallIcon(R.drawable.ic_default_animal)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        const val WORK_NAME = "VaccineReminderWork"
        private const val CHANNEL_ID = "vaccine_reminder_channel"
    }
}
