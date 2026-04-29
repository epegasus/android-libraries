package com.sohaib.prayernotification.data

import com.sohaib.prayernotification.data.model.Prayer

interface PrayerTimeDataSource {
    fun getTodayPrayerTimes(): List<Prayer>
}