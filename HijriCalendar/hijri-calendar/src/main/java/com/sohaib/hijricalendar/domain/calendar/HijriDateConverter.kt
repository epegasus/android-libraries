package com.sohaib.hijricalendar.domain.calendar

import com.sohaib.hijricalendar.domain.model.HijriDate
import java.time.LocalDate

internal interface HijriDateConverter {
    fun toHijri(date: LocalDate): HijriDate
}