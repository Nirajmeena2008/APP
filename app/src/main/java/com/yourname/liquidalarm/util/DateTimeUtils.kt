package com.yourname.liquidalarm.util

import kotlinx.datetime.LocalTime
import java.text.SimpleDateFormat
import java.util.Locale

object DateTimeUtils {
    private val TIME_FORMAT_24H = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val TIME_FORMAT_12H = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val TIME_FORMAT_24H_SEC = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val TIME_FORMAT_12H_SEC = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())

    fun formatTime(time: LocalTime, is24Hour: Boolean, showSeconds: Boolean = false): String {
        val hour = time.hour
        val minute = time.minute
        val second = time.second

        if (is24Hour) {
            return if (showSeconds) {
                String.format("%02d:%02d:%02d", hour, minute, second)
            } else {
                String.format("%02d:%02d", hour, minute)
            }
        } else {
            val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
            val amPm = if (hour >= 12) "PM" else "AM"
            return if (showSeconds) {
                String.format("%02d:%02d:%02d %s", displayHour, minute, second, amPm)
            } else {
                String.format("%02d:%02d %s", displayHour, minute, amPm)
            }
        }
    }

    fun parseTime(timeString: String, is24Hour: Boolean): LocalTime? {
        try {
            return if (is24Hour) {
                val parts = timeString.split(":")
                val hour = parts[0].toInt()
                val minute = parts[1].toInt()
                val second = if (parts.size > 2) parts[2].toInt() else 0
                LocalTime(hour, minute, second)
            } else {
                // Parse 12-hour format with AM/PM
                val formatter = java.time.format.DateTimeFormatter.ofPattern("hh:mm a")
                val localTime = java.time.LocalTime.parse(timeString.trim(), formatter)
                LocalTime(localTime.hour, localTime.minute, localTime.second)
            }
        } catch (e: Exception) {
            return null
        }
    }

    fun getTimeUntil(targetTime: LocalTime): Long {
        val now = java.time.LocalTime.now()
        val target = java.time.LocalTime.of(targetTime.hour, targetTime.minute, targetTime.second)
        var diff = java.time.Duration.between(now, target).toMillis()
        if (diff <= 0) {
            diff += 24 * 60 * 60 * 1000 // Add one day
        }
        return diff
    }
}
