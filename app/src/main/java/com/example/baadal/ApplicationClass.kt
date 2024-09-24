package com.example.baadal

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ApplicationClass: Application() {

    companion object {
        const val CHANNEL_ID = "MusicNotification"
        const val NEXT = "next"
        const val PREVIOUS = "previous"
        const val EXIT = "exit"
        const val PLAY = "play"
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, "Playing", NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = "Needed to Show Notification for Playing Song"
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}