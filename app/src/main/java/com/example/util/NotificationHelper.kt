package com.example.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.receiver.NotificationReceiver

object NotificationHelper {
    const val CHANNEL_ID = "tahatodo_deadline_channel"
    private const val CHANNEL_NAME = "BitTask Deadlines"
    private const val CHANNEL_DESC = "Notifications for BitTask deadlines and reminders"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("NotificationHelper", "Notification channel created: $CHANNEL_ID")
        }
    }

    fun scheduleNotification(
        context: Context,
        taskId: Int,
        title: String,
        message: String,
        triggerTimeMs: Long
    ) {
        if (triggerTimeMs <= System.currentTimeMillis()) {
            // If it is already in the past, don't schedule or send immediately
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("task_id", taskId)
            putExtra("task_title", title)
            putExtra("task_message", message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMs,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMs,
                    pendingIntent
                )
            }
            Log.d("NotificationHelper", "Scheduled notification for task $taskId at $triggerTimeMs ms")
        } catch (e: SecurityException) {
            // Some newer Android versions require explicit schedule exact alarm permissions,
            // fall back to standard alarm if exact is restricted
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMs,
                pendingIntent
            )
            Log.e("NotificationHelper", "Failed to schedule exact alarm, falling back to standard set()", e)
        }
    }

    fun cancelNotification(context: Context, taskId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            Log.d("NotificationHelper", "Cancelled notification for task $taskId")
        }
    }
}
