package com.divinebudget.app.ejgpoer.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.divinebudget.app.DivineBudgetActivity
import com.divinebudget.app.R
import com.divinebudget.app.ejgpoer.presentation.app.DivineBudgetApplication
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val DIVINE_BUDGET_CHANNEL_ID = "divine_budget_notifications"
private const val DIVINE_BUDGET_CHANNEL_NAME = "DivineBudget Notifications"
private const val DIVINE_BUDGET_NOT_TAG = "DivineBudget"

class DivineBudgetPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                divineBudgetShowNotification(it.title ?: DIVINE_BUDGET_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                divineBudgetShowNotification(it.title ?: DIVINE_BUDGET_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            divineBudgetHandleDataPayload(remoteMessage.data)
        }
    }

    private fun divineBudgetShowNotification(title: String, message: String, data: String?) {
        val divineBudgetNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                DIVINE_BUDGET_CHANNEL_ID,
                DIVINE_BUDGET_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            divineBudgetNotificationManager.createNotificationChannel(channel)
        }

        val divineBudgetIntent = Intent(this, DivineBudgetActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val divineBudgetPendingIntent = PendingIntent.getActivity(
            this,
            0,
            divineBudgetIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val divineBudgetNotification = NotificationCompat.Builder(this, DIVINE_BUDGET_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.divine_budgest_noti_icon)
            .setAutoCancel(true)
            .setContentIntent(divineBudgetPendingIntent)
            .build()

        divineBudgetNotificationManager.notify(System.currentTimeMillis().toInt(), divineBudgetNotification)
    }

    private fun divineBudgetHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(DivineBudgetApplication.DIVINE_BUDGET_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}