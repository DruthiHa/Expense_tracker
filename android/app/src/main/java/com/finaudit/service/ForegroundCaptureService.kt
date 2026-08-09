package com.finaudit.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.finaudit.R

class ForegroundCaptureService : Service() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = createNotification("FinAudit is scanning transaction notifications safely in the background.")
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val extraText = intent?.getStringExtra("STATUS_TEXT")
        if (extraText != null) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, createNotification(extraText))
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("FinAudit Active")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Transaction Scan Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors background SMS & push notifications for transaction detection"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "finaudit_capture_service"
        private const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, ForegroundCaptureService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun updateStatus(context: Context, status: String) {
            val intent = Intent(context, ForegroundCaptureService::class.java).apply {
                putExtra("STATUS_TEXT", status)
            }
            context.startService(intent)
        }
    }
}
