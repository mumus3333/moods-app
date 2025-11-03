package com.example.moods.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.moods.MainActivity
import com.example.moods.R

// Constantes para el canal de notificación
const val NOTIFICATION_CHANNEL_ID = "moods_reminder_channel"
const val NOTIFICATION_CHANNEL_NAME = "Recordatorios de Ánimo"
const val NOTIFICATION_ID = 1

object NotificationHelper {

    /**
     * Crea el canal de notificación. Es obligatorio en Android 8.0 (API 26) y superior.
     * Esto debe llamarse al iniciar la aplicación.
     */
    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Canal para los recordatorios de registro de estado de ánimo."
        }

        // Registrar el canal en el sistema
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Construye y muestra la notificación de recordatorio.
     */
    fun sendReminderNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent para abrir la MainActivity cuando se toque la notificación
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Construir la notificación
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Asegúrate de tener este drawable
            .setContentTitle("¿Cómo te sientes?")
            .setContentText("No has registrado tu estado de ánimo hoy. ¡Tómate un momento!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // Acción al tocar
            .setAutoCancel(true) // Descartar al tocar
            .build()

        // Mostrar la notificación
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
