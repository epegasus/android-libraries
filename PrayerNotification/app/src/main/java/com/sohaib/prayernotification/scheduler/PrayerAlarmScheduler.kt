package com.sohaib.prayernotification.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.sohaib.prayernotification.data.PrayerTimeDataSource
import com.sohaib.prayernotification.data.PrayerTimeDataSourceImpl
import com.sohaib.prayernotification.data.model.Prayer
import com.sohaib.prayernotification.receiver.PrayerNotificationReceiver
import java.util.Date
import java.util.concurrent.TimeUnit

class PrayerAlarmScheduler(
    private val context: Context,
    private val dataSource: PrayerTimeDataSource = PrayerTimeDataSourceImpl(),
) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleNextPrayer(): ScheduleResult {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            val message = "Exact alarm permission is not granted."
            Log.w(TAG, message)
            return ScheduleResult(success = false, message = message)
        }

        return try {
            val now = System.currentTimeMillis()
            val todayPrayers = dataSource.getTodayPrayerTimes().sortedBy { it.timeMillis }
            val nextPrayer = todayPrayers.firstOrNull { it.timeMillis > now } ?: resolveNextDayFajr(todayPrayers, now)

            cancelNextPrayer()

            val pendingIntent = createPrayerPendingIntent(context = context, prayerName = nextPrayer.name)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextPrayer.timeMillis, pendingIntent)

            val message = "Scheduled ${nextPrayer.name} at ${Date(nextPrayer.timeMillis)}."
            Log.i(TAG, message)
            ScheduleResult(success = true, message = message)
        } catch (e: Exception) {
            val message = "Failed to schedule next prayer: ${e.message}"
            Log.e(TAG, message, e)
            ScheduleResult(success = false, message = message)
        }
    }

    fun cancelNextPrayer(): ScheduleResult {
        return try {
            alarmManager.cancel(createPrayerPendingIntent(context = context))
            val message = "Prayer alarm cancelled."
            Log.i(TAG, message)
            ScheduleResult(success = true, message = message)
        } catch (e: Exception) {
            val message = "Failed to cancel prayer alarm: ${e.message}"
            Log.e(TAG, message, e)
            ScheduleResult(success = false, message = message)
        }
    }

    private fun resolveNextDayFajr(todayPrayers: List<Prayer>, now: Long): Prayer {
        val fajrToday = todayPrayers.firstOrNull { it.name.equals("Fajr", ignoreCase = true) }
            ?: todayPrayers.minByOrNull { it.timeMillis }
            ?: Prayer(name = "Fajr", timeMillis = now + TimeUnit.HOURS.toMillis(6))

        var nextFajrTime = fajrToday.timeMillis + TimeUnit.DAYS.toMillis(1)
        if (nextFajrTime <= now) {
            nextFajrTime = now + TimeUnit.MINUTES.toMillis(1)
        }
        return Prayer(name = "Fajr", timeMillis = nextFajrTime)
    }

    data class ScheduleResult(
        val success: Boolean,
        val message: String,
    )

    companion object {
        private const val TAG = "TAG_PrayerAlarmScheduler"
        private const val REQUEST_CODE_PRAYER = 1001
        const val EXTRA_PRAYER_NAME = "extra_prayer_name"

        fun createPrayerPendingIntent(context: Context, prayerName: String = "Prayer"): PendingIntent {
            val intent = Intent(context, PrayerNotificationReceiver::class.java).apply {
                putExtra(EXTRA_PRAYER_NAME, prayerName)
            }
            return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_PRAYER,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}