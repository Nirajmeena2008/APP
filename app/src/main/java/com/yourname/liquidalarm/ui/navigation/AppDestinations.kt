package com.yourname.liquidalarm.ui.navigation

sealed interface AppDestination {
    data object AlarmList : AppDestination
    data class CreateAlarm(val alarmId: String? = null) : AppDestination
    data class ActiveAlarm(val alarmId: String, val isPreAlarm: Boolean) : AppDestination
    data object Timer : AppDestination
    data object Stopwatch : AppDestination
    data object WorldClock : AppDestination
    data object Settings : AppDestination
}
