package com.orbitalsonic.sonicopt.models

/**
 * Represents fasting times for a specific day, including Sehri and Iftaar times.
 *
 * @property date The date of the fasting times in milliseconds since epoch.
 * @property sehriTime The time for Sehri (pre-dawn meal), equivalent to the Fajr prayer time.
 * @property sehriTimeMillis The time for Sehri (pre-dawn meal), equivalent to the Fajr prayer time.
 * @property iftaarTime The time for Iftaar (breaking the fast), equivalent to the Maghrib prayer time.
 * @property iftaarTimeMillis The time for Iftaar (breaking the fast), equivalent to the Maghrib prayer time.
 */
data class FastingItem(
    var date: Long, // The date of the specific fasting day in milliseconds.
    val sehriTime: String, // Time for Sehri, usually matches Fajr prayer time.
    val sehriTimeMillis: Long, // Time for Sehri, usually matches Fajr prayer time.
    val iftaarTime: String, // Time for Iftaar, usually matches Maghrib prayer time.
    val iftaarTimeMillis: Long // Time for Iftaar, usually matches Maghrib prayer time.
)