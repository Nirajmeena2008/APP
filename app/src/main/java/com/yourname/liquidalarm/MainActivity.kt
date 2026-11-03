package com.yourname.liquidalarm

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.*
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
                val alarmId = intent.getStringExtra("alarm_id")
                val isPreAlarm = intent.getBooleanExtra("is_pre_alarm", false)
                if (alarmId != null) {
                    var currentAlarm by remember { mutableStateOf<AlarmEntity?>(null) }
                    LaunchedEffect(alarmId) {
                        currentAlarm = repository.getAlarms().firstOrNull { it.id == alarmId }
                    }
                    currentAlarm?.let {
                        ActiveAlarmScreen(alarm = it, isPreAlarm = isPreAlarm, onDismiss = { finish() }, onSnooze = {}, onMathChallenge = {}, onQRChallenge = {}, onShakeChallenge = {}, onChallengeCompleted = { finish() })
                    }
                } else {
                    AppNavHost(repository = repository, prefs = prefs, startDestination = AppDestination.AlarmList)
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