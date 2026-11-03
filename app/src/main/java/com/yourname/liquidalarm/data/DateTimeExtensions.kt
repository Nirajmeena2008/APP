package com.yourname.liquidalarm.data

import kotlinx.datetime.*
import kotlin.time.Duration.Companion.seconds

fun Instant.plusDays(days: Int, zone: TimeZone = TimeZone.currentSystemDefault()): Instant {
    return this.plus(days, DateTimeUnit.DAY, zone)
}

fun Instant.plusMonths(months: Int, zone: TimeZone = TimeZone.currentSystemDefault()): Instant {
    return this.plus(months, DateTimeUnit.MONTH, zone)
}

fun Instant.plusYears(years: Int, zone: TimeZone = TimeZone.currentSystemDefault()): Instant {
    return this.plus(years, DateTimeUnit.YEAR, zone)
}

fun Instant.minusSeconds(seconds: Long): Instant {
    return this.minus(seconds.seconds)
}

val Instant.dayOfWeek: DayOfWeek
    get() = this.toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek

val Instant.dayOfMonth: Int
    get() = this.toLocalDateTime(TimeZone.currentSystemDefault()).dayOfMonth

val Instant.month: Month
    get() = this.toLocalDateTime(TimeZone.currentSystemDefault()).month
