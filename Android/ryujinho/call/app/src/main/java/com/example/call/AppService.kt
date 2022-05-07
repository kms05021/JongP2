package com.example.call

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log

class AppService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START_FOREGROUND -> {
                startForegroundService()
            }
            Actions.STOP_FOREGROUND -> {
                stopForegroundService()
            }
        }
        return START_STICKY
    }

    private fun startForegroundService() {
        val notification = AppNotification.createNotification(this)
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun stopForegroundService() {
        stopForeground(true)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        // bound service 가 아니므로 null
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "onCreate()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy()")
    }

    companion object {
        const val TAG = "[AppService]"
        const val NOTIFICATION_ID = 20
    }
}