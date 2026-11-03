package com.yourname.liquidalarm

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.yourname.liquidalarm.ui.theme.LiquidAlarmTheme
import com.yourname.liquidalarm.data.AlarmEntity
import com.yourname.liquidalarm.data.AlarmRepository
import com.yourname.liquidalarm.data.PreferencesManager
import com.yourname.liquidalarm.ui.navigation.AppDestination
import com.yourname.liquidalarm.ui.navigation.AppNavHost
import com.yourname.liquidalarm.ui.screens.ActiveAlarmScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val repository: AlarmRepository by lazy { AlarmRepository(this) }
    private val prefs: PreferencesManager by lazy { PreferencesManager(this) }
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiquidAlarmTheme {
                // Check for alarm intent
                val alarmId = intent.getStringExtra("alarm_id")
                val isPreAlarm = intent.getBooleanExtra("is_pre_alarm", false)

                if (alarmId != null) {
                    var currentAlarm by remember { mutableStateOf<AlarmEntity?>(null) }
                    LaunchedEffect(alarmId) {
                        currentAlarm = repository.getAlarms().firstOrNull { it.id == alarmId }
                    }
                    currentAlarm?.let {
                        ActiveAlarmScreen(
                            alarm = it,
                            isPreAlarm = isPreAlarm,
                            onDismiss = { finish() },
                            onSnooze = { /* Handled by service */ },
                            onMathChallenge = { /* Show math challenge */ },
                            onQRChallenge = { /* Show QR challenge */ },
                            onShakeChallenge = { /* Show shake challenge */ },
                            onChallengeCompleted = { finish() }
                        )
                    }
                } else {
                    val navDest = intent.getStringExtra("nav_destination")
                    val startDest = when (navDest) {
                        "Timer" -> AppDestination.Timer
                        "Stopwatch" -> AppDestination.Stopwatch
                        "WorldClock" -> AppDestination.WorldClock
                        "Settings" -> AppDestination.Settings
                        else -> AppDestination.AlarmList
                    }
                    AppNavHost(
                        repository = repository,
                        prefs = prefs,
                        startDestination = startDest
                    )
                }
            }
        }
    }

    private fun enableEdgeToEdge() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
        }
    }
}

class MainViewModel : ViewModel()
