package com.sohaib.prayernotification.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sohaib.prayernotification.MainActivity
import com.sohaib.prayernotification.R

class AzanForegroundService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        createChannelIfNeeded()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: ACTION_START
        val prayerName = intent?.getStringExtra(EXTRA_PRAYER_NAME) ?: "Prayer"

        return when (action) {
            ACTION_STOP -> {
                stopPlaybackAndSelf()
                START_NOT_STICKY
            }

            else -> {
                startForeground(FOREGROUND_NOTIFICATION_ID, buildForegroundNotification(prayerName))
                startPlayback(prayerName)
                START_NOT_STICKY
            }
        }
    }

    private fun buildForegroundNotification(prayerName: String): Notification {
        val openAppIntent = PendingIntent.getActivity(
            this,
            OPEN_APP_REQUEST_CODE,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = PendingIntent.getService(
            this,
            STOP_REQUEST_CODE,
            Intent(this, AzanForegroundService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Prayer Time")
            .setContentText("It's time for $prayerName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setContentIntent(openAppIntent)
            .addAction(0, "Stop", stopIntent)
            .build()
    }

    private fun startPlayback(prayerName: String) {
        stopPlayback()

        val player = MediaPlayer.create(this, R.raw.azan)
        if (player == null) {
            Log.e(TAG, "Unable to create MediaPlayer for azan resource")
            stopPlaybackAndSelf()
            return
        }

        mediaPlayer = player.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            isLooping = false
            setOnCompletionListener {
                Log.i(TAG, "Azan playback completed for $prayerName")
                stopPlaybackAndSelf()
            }
            setOnErrorListener { _, what, extra ->
                Log.e(TAG, "MediaPlayer error what=$what extra=$extra")
                stopPlaybackAndSelf()
                true
            }
            start()
            Log.i(TAG, "Azan playback started for $prayerName")
        }
    }

    private fun stopPlayback() {
        mediaPlayer?.runCatching {
            if (isPlaying) stop()
            reset()
            release()
        }
        mediaPlayer = null
    }

    private fun stopPlaybackAndSelf() {
        stopPlayback()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Prayer Playback",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Foreground service for Azan playback"
            setSound(null, null)
        }
        manager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        stopPlayback()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val TAG = "TAG_AzanForegroundService"
        private const val CHANNEL_ID = "prayer_playback_channel"
        private const val FOREGROUND_NOTIFICATION_ID = 3001
        private const val OPEN_APP_REQUEST_CODE = 2001
        private const val STOP_REQUEST_CODE = 2002

        const val ACTION_START = "azan_action_start"
        const val ACTION_STOP = "azan_action_stop"
        const val EXTRA_PRAYER_NAME = "extra_prayer_name"
    }
}