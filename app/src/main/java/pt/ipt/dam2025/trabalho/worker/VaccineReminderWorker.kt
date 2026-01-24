package pt.ipt.dam2025.trabalho.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import pt.ipt.dam2025.trabalho.R

// Classe para o worker de notificação
class VaccineReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val animalNome = inputData.getString(KEY_ANIMAL_NOME) ?: return Result.failure()
        val tipoVacina = inputData.getString(KEY_TIPO_VACINA) ?: return Result.failure()
        val dataVacina = inputData.getString(KEY_DATA_VACINA) ?: return Result.failure()

        sendNotification(animalNome, tipoVacina, dataVacina)

        return Result.success()
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
            .setContentText("A vacina '$tipoVacina' para o seu animal '$animalNome' está agendada para amanhã, $dataVacina.")
            .setSmallIcon(R.drawable.ic_default_animal)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        const val WORK_NAME = "VaccineReminderWork"
        const val KEY_ANIMAL_NOME = "animal_nome"
        const val KEY_TIPO_VACINA = "tipo_vacina"
        const val KEY_DATA_VACINA = "data_vacina"
        private const val CHANNEL_ID = "vaccine_reminder_channel"
    }
}
