package com.sohaib.prayernotification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.sohaib.prayernotification.scheduler.PrayerAlarmScheduler
import com.sohaib.prayernotification.service.AzanForegroundService

class PrayerNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(PrayerAlarmScheduler.EXTRA_PRAYER_NAME) ?: "Prayer"

        scheduleNextPrayer(context, prayerName)
        startAzanPlayback(context, prayerName)
    }

    private fun scheduleNextPrayer(context: Context, prayerName: String) {
        val result = PrayerAlarmScheduler(context).scheduleNextPrayer()
        Log.d(TAG, "Alarm received for prayer: $prayerName and rescheduled prayer time {${result.message}}")
    }

    private fun startAzanPlayback(context: Context, prayerName: String) {
        val serviceIntent = Intent(context, AzanForegroundService::class.java).apply {
            action = AzanForegroundService.ACTION_START
            putExtra(AzanForegroundService.EXTRA_PRAYER_NAME, prayerName)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
        Log.i(TAG, "Started foreground Azan service for prayer: $prayerName")
    }

    private companion object {
        private const val TAG = "TAG_PrayerReceiver"
    }
}