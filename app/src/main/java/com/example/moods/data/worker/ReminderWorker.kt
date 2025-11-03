/*package com.example.moods.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.moods.data.repository.MoodRepository
import com.example.moods.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

// Definimos el tiempo de recordatorio.
// Para la demostración, usamos 15 minutos.
// Para producción, serían 18 horas.
private val REMINDER_THRESHOLD_MS = TimeUnit.MINUTES.toMillis(15)
// private val REMINDER_THRESHOLD_MS = TimeUnit.HOURS.toMillis(18)

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: MoodRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "MoodReminderWork"
    }

    override suspend fun doWork(): Result {
        Log.d(WORK_NAME, "WorkManager: Verificando último registro...")

        val latestEntry = repository.getLatestEntry()
        val now = System.currentTimeMillis()

        if (latestEntry == null) {
            // Caso 1: Nunca ha registrado. Enviar notificación.
            Log.d(WORK_NAME, "WorkManager: No hay registros. Enviando notificación.")
            NotificationHelper.sendReminderNotification(context)
        } else {
            // Caso 2: Hay registros. Comprobar si son antiguos.
            val timeSinceLastEntry = now - latestEntry.timestamp
            if (timeSinceLastEntry > REMINDER_THRESHOLD_MS) {
                // Si el último registro fue hace más de 15 minutos (para demo)
                Log.d(WORK_NAME, "WorkManager: Registro antiguo. Enviando notificación.")
                NotificationHelper.sendReminderNotification(context)
            } else {
                Log.d(WORK_NAME, "WorkManager: Registro reciente. No se necesita notificación.")
            }
        }

        return Result.success()
    }
}
*/