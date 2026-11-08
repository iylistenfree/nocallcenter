package com.nocallcenter.app.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.nocallcenter.app.MainActivity
import com.nocallcenter.app.R
import com.nocallcenter.app.data.model.CallScreeningOutcome
import com.nocallcenter.app.data.preferences.AppPreferences

class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val callsChannel = NotificationChannel(
                CHANNEL_CALLS_ID,
                context.getString(R.string.nav_history),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifiche per chiamate schermate e bloccate"
            }

            val alertsChannel = NotificationChannel(
                CHANNEL_ALERTS_ID,
                context.getString(R.string.budget_card_title),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Avvisi di sicurezza su soglia e budget SMS"
            }

            notificationManager.createNotificationChannel(callsChannel)
            notificationManager.createNotificationChannel(alertsChannel)
        }
    }

    fun showCallScreenedNotification(phoneNumber: String, outcome: CallScreeningOutcome) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val prefs = AppPreferences(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val paypalIntent = Intent(Intent.ACTION_VIEW, Uri.parse(prefs.paypalUrl)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val paypalPendingIntent = PendingIntent.getActivity(
            context,
            2,
            paypalIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (title, content) = when (outcome) {
            CallScreeningOutcome.BLOCKED_SMS_SENT -> Pair(
                context.getString(R.string.status_blocked_sms_sent),
                "$phoneNumber • ${context.getString(R.string.notif_satisfied_note)}"
            )
            CallScreeningOutcome.BLOCKED_ANTI_FLOOD -> Pair(
                context.getString(R.string.status_blocked_sms_skipped_flood),
                phoneNumber
            )
            CallScreeningOutcome.BLOCKED_BUDGET_EXCEEDED -> Pair(
                context.getString(R.string.status_blocked_sms_skipped_budget),
                phoneNumber
            )
            CallScreeningOutcome.BLOCKED_SMS_DISABLED -> Pair(
                context.getString(R.string.status_blocked_sms_disabled),
                phoneNumber
            )
            CallScreeningOutcome.BLOCKED_FOREIGN_PREFIX -> Pair(
                context.getString(R.string.status_blocked_foreign_prefix),
                phoneNumber
            )
            CallScreeningOutcome.ALLOWED_CONTACT -> return
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_CALLS_ID)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.star_on,
                context.getString(R.string.notif_action_coffee),
                paypalPendingIntent
            )
            .setAutoCancel(true)
            .build()

        val notificationId = (System.currentTimeMillis() % 10000).toInt()
        notificationManager.notify(notificationId, notification)
    }

    fun showBudgetExhaustedAlert() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS_ID)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle(context.getString(R.string.budget_card_title))
            .setContentText(context.getString(R.string.budget_exhausted_warning))
            .setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.budget_exhausted_warning)))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_BUDGET_ID, notification)
    }

    companion object {
        const val CHANNEL_CALLS_ID = "channel_screened_calls"
        const val CHANNEL_ALERTS_ID = "channel_budget_alerts"
        const val NOTIFICATION_BUDGET_ID = 9999
    }
}
