package com.yourname.liquidalarm

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.ViewModel
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
                    lifecycleScope.launch {
                        val alarm = repository.getAlarms().firstOrNull { it.id == alarmId }
                        alarm?.let {
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
                    }
                } else {
                    AppNavHost(
                        repository = repository,
                        prefs = prefs,
                        startDestination = intent.getStringExtra("nav_destination")?.let { AppDestination.valueOf(it) } ?: AppDestination.AlarmList
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
