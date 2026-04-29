package com.orbitalsonic.sonicopt.models

/**
 * Represents the details of an individual prayer time.
 *
 * @property prayerName The name of the prayer (e.g., Fajr, Dhuhr, Asr, Maghrib, Isha).
 * @property prayerTime The time of the prayer as a string, formatted based on user preferences.
 * @property prayerTimeMillis The time of the prayer in milliseconds.
 */
data class PrayerTimes(
    var prayerName: String, // The name of the prayer.
    var prayerTime: String, // The formatted prayer time.
    var prayerTimeMillis: Long // The prayer time in milliseconds.
)