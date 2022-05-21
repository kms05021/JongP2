package com.example.call

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

object AlarmNotification {
    private const val CHANNEL_ID = "foreground_service_channel" // 임의의 채널 ID

    fun createNotification(
        context: Context
    ): Notification {
        // 알림 클릭시 MainActivity 로 이동됨
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.action = Actions.MAIN
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent
            .getActivity(context, 0, notificationIntent, FLAG_UPDATE_CURRENT)

        // 알림
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Alarm Player")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true) // true 일경우 알림 리스트에서 클릭하거나 좌우로 드래그해도 사라지지 않음
            .setContentIntent(pendingIntent)
            .build()

        // Oreo 부터는 Notification Channel 을 만들어야 함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Channel", // 채널표시명
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }

        return notification
    }
}