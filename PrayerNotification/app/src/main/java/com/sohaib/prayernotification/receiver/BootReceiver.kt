package com.sohaib.prayernotification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sohaib.prayernotification.scheduler.PrayerAlarmScheduler

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_DATE_CHANGED,
                -> {
                val result = PrayerAlarmScheduler(context).scheduleNextPrayer()
                Log.d(TAG, "System event received: ${intent.action} & reschedule prayer alarm {${result.message}}")
            }
        }
    }

    private companion object {
        private const val TAG = "TAG_BootReceiver"
    }
}