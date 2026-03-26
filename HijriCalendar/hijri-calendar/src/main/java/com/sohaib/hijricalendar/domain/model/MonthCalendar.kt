package com.sohaib.hijricalendar.domain.model

import java.time.YearMonth

internal data class MonthCalendar(
    val month: YearMonth,
    val days: List<CalendarDay>
)