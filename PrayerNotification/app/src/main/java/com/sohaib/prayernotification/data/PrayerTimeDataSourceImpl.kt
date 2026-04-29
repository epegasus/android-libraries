package com.sohaib.prayernotification.data

import android.util.Log
import com.orbitalsonic.sonicopt.PrayerTimeManager
import com.orbitalsonic.sonicopt.enums.AsrJuristicMethod
import com.orbitalsonic.sonicopt.enums.HighLatitudeAdjustment
import com.orbitalsonic.sonicopt.enums.PrayerTimeConvention
import com.orbitalsonic.sonicopt.enums.TimeFormat
import com.orbitalsonic.sonicopt.models.PrayerCustomAngle
import com.orbitalsonic.sonicopt.models.PrayerManualCorrection
import com.sohaib.prayernotification.data.model.Prayer
import java.util.Date

class PrayerTimeDataSourceImpl : PrayerTimeDataSource {

    private val prayerTimeManager = PrayerTimeManager()

    override fun getTodayPrayerTimes(): List<Prayer> {
        val todayPrayerItem = prayerTimeManager.getDailyPrayerTimes(
            latitude = DEFAULT_LATITUDE,
            longitude = DEFAULT_LONGITUDE,
            date = Date(),
            highLatitudeAdjustment = HighLatitudeAdjustment.NO_ADJUSTMENT,
            asrJuristicMethod = AsrJuristicMethod.HANAFI,
            prayerTimeConvention = PrayerTimeConvention.KARACHI,
            timeFormat = TimeFormat.HOUR_24,
            prayerManualCorrection = PrayerManualCorrection(),
            prayerCustomAngle = PrayerCustomAngle()
        )

        return todayPrayerItem.prayerList
            .filter { prayer -> prayer.prayerName in TRACKED_PRAYERS }
            .map { prayer -> Prayer(name = prayer.prayerName, timeMillis = prayer.prayerTimeMillis) }
    }

    private companion object {
        private val TRACKED_PRAYERS = setOf("Fajr", "Zuhr", "Asr", "Maghrib", "Isha")

        // Sample location (Makkah); replace with user-selected coordinates if needed.
        private const val DEFAULT_LATITUDE = 33.4981393
        private const val DEFAULT_LONGITUDE = 73.0734033
    }
}