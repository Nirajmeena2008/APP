package com.yourname.liquidalarm.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourname.liquidalarm.data.AlarmEntity
import com.yourname.liquidalarm.data.AlarmRepository
import com.yourname.liquidalarm.data.PreferencesManager
import com.yourname.liquidalarm.ui.screens.ActiveAlarmScreen
import com.yourname.liquidalarm.ui.screens.AlarmListScreen
import com.yourname.liquidalarm.ui.screens.CreateAlarmScreen
import com.yourname.liquidalarm.ui.screens.SettingsScreen
import com.yourname.liquidalarm.ui.screens.StopwatchScreen
import com.yourname.liquidalarm.ui.screens.TimerScreen
import com.yourname.liquidalarm.ui.screens.WorldClockScreen

@Composable
fun AppNavHost(
    repository: AlarmRepository,
    prefs: PreferencesManager,
    startDestination: AppDestination = AppDestination.AlarmList
) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(0) }

    androidx.navigation.compose.NavHost(navController, startDestination = startDestination.route) {
        composable(AppDestination.AlarmList.route) {
            AlarmListScreen(
                repository = repository,
                prefs = prefs,
                onCreateAlarm = { navController.navigate(AppDestination.CreateAlarm().route) },
                onEditAlarm = { alarm -> navController.navigate(AppDestination.CreateAlarm(alarm.id).route) },
                onNavigateToTimer = { selectedTab = 1; navController.navigate(AppDestination.Timer.route) },
                onNavigateToStopwatch = { selectedTab = 2; navController.navigate(AppDestination.Stopwatch.route) },
                onNavigateToWorldClock = { selectedTab = 3; navController.navigate(AppDestination.WorldClock.route) },
                onNavigateToSettings = { navController.navigate(AppDestination.Settings.route) }
            )
        }

        composable(
            route = AppDestination.CreateAlarm.routePattern,
            arguments = listOf(androidx.navigation.navArgument("alarmId") { type = androidx.navigation.NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val alarmId = backStackEntry.getString()?.getString("alarmId")
            val alarm = alarmId?.let { repository.getAlarms().firstOrNull { it.id == it } }
            CreateAlarmScreen(
                initialAlarm = alarm,
                onSave = { savedAlarm ->
                    if (alarm == null) repository.addAlarm(savedAlarm) else repository.updateAlarm(savedAlarm)
                    navController.popBackStack()
                },
                onDismiss = { navController.popBackStack() },
                onPickSoundFile = { /* Launch file picker */ },
                onPreviewSound = { /* Preview sound */ },
                onPreviewVibration = { /* Preview vibration */ }
            )
        }

        composable(
            route = AppDestination.ActiveAlarm.routePattern,
            arguments = listOf(
                androidx.navigation.navArgument("alarmId") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("isPreAlarm") { type = androidx.navigation.NavType.BooleanType }
            )
        ) { backStackEntry ->
            val alarmId = backStackEntry.getString()?.getString("alarmId") ?: ""
            val isPreAlarm = backStackEntry.getBoolean("isPreAlarm")
            val alarm = repository.getAlarms().firstOrNull { it.id == alarmId }
            alarm?.let {
                ActiveAlarmScreen(
                    alarm = it,
                    isPreAlarm = isPreAlarm,
                    onDismiss = { navController.popBackStack() },
                    onSnooze = { /* Handled by service */ },
                    onMathChallenge = { /* Show math challenge */ },
                    onQRChallenge = { /* Show QR challenge */ },
                    onShakeChallenge = { /* Show shake challenge */ },
                    onChallengeCompleted = { navController.popBackStack() }
                )
            }
        }

        composable(AppDestination.Timer.route) {
            TimerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(AppDestination.Stopwatch.route) {
            StopwatchScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(AppDestination.WorldClock.route) {
            WorldClockScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(AppDestination.Settings.route) {
            SettingsScreen(prefs = prefs, onNavigateBack = { navController.popBackStack() })
        }
    }
}

private val AppDestination.AlarmList
    get() = "alarms"

private val AppDestination.CreateAlarm
    get() = "create_alarm"

private val AppDestination.CreateAlarm.routePattern
    get() = "create_alarm?alarmId={alarmId}"

private val AppDestination.ActiveAlarm
    get() = "active_alarm"

private val AppDestination.ActiveAlarm.routePattern
    get() = "active_alarm/{alarmId}/{isPreAlarm}"

private val AppDestination.Timer
    get() = "timer"

private val AppDestination.Stopwatch
    get() = "stopwatch"

private val AppDestination.WorldClock
    get() = "world_clock"

private val AppDestination.Settings
    get() = "settings"
