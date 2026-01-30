package pt.ipt.dam2025.vetconnect.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import pt.ipt.dam2025.vetconnect.R
import pt.ipt.dam2025.vetconnect.VetConnectApplication
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class VaccineReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        try {
            // Obtém a instância da base de dados a partir da classe Application
            val database = (applicationContext as VetConnectApplication).database
            
            // NOTA: O código abaixo assume que temos um 'vaccineDao' e um 'animalDao'
            // na nossa AppDatabase. Isto será criado no próximo passo.
            val vaccineDao = database.vaccineDao() 
            val animalDao = database.animalDao()

            // Calcula a data de "amanhã"
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val tomorrow = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            // Procura vacinas agendadas para amanhã
            val upcomingVaccines = vaccineDao.getVaccinesForDate(tomorrow)

            // Para cada vacina encontrada, envia uma notificação
            upcomingVaccines.forEach { vaccine ->
                val animal = animalDao.getAnimalById(vaccine.animalId) // Obtém os detalhes do animal
                animal?.let {
                    sendNotification(
                        animal.name,
                        vaccine.type,
                        vaccine.date
                    )
                }
            }
            return Result.success()
        } catch (e: Exception) {
            // Se algo correr mal, marca a tarefa como falhada
            return Result.failure()
        }
    }

    private fun sendNotification(animalNome: String, tipoVacina: String, dataVacina: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Lembretes de Vacinas",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Lembrete de Vacina")
            .setContentText("A vacina '$tipoVacina' para o seu animal '$animalNome' está agendada para amanhã, $dataVacina.")
            .setSmallIcon(R.drawable.ic_default_animal) // Assumindo que este ícone existe
            .build()

        // Usar um ID único para cada notificação para evitar que se sobreponham
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        const val WORK_NAME = "VaccineReminderWork"
        private const val CHANNEL_ID = "vaccine_reminder_channel"
    }
}