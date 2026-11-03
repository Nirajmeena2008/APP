import re
import os

def replace(path, old, new):
    if not os.path.exists(path): return
    with open(path, "r") as f: content = f.read()
    content = content.replace(old, new)
    with open(path, "w") as f: f.write(content)

# MainActivity
replace("app/src/main/java/com/yourname/liquidalarm/MainActivity.kt",
"import androidx.lifecycle.viewmodel.ViewModel",
"import androidx.lifecycle.ViewModel\nimport androidx.compose.runtime.*\nimport com.yourname.liquidalarm.ui.theme.LiquidAlarmTheme")

ma_old = """        setContent {
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
        }"""
ma_new = """        setContent {
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
        }"""
replace("app/src/main/java/com/yourname/liquidalarm/MainActivity.kt", ma_old, ma_new)

# LiquidAlarmApplication
replace("app/src/main/java/com/yourname/liquidalarm/LiquidAlarmApplication.kt",
"PermissionUtils.requestExactAlarmPermission(this)", "")

# AlarmForegroundService
replace("app/src/main/java/com/yourname/liquidalarm/alarm/AlarmForegroundService.kt",
"class AlarmForegroundService : Service()",
"class AlarmForegroundService : androidx.lifecycle.LifecycleService()")
replace("app/src/main/java/com/yourname/liquidalarm/alarm/AlarmForegroundService.kt",
"lifecycleRegistry = LifecycleRegistry(this)\n        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)", "")
replace("app/src/main/java/com/yourname/liquidalarm/alarm/AlarmForegroundService.kt",
"lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)", "")
replace("app/src/main/java/com/yourname/liquidalarm/alarm/AlarmForegroundService.kt",
"private lateinit var lifecycleRegistry: LifecycleRegistry", "")
replace("app/src/main/java/com/yourname/liquidalarm/alarm/AlarmForegroundService.kt",
"if (alarm != null) {", "val currentAlarm = alarm\n            if (currentAlarm != null) {")
replace("app/src/main/java/com/yourname/liquidalarm/alarm/AlarmForegroundService.kt", "alarm!!", "currentAlarm")
replace("app/src/main/java/com/yourname/liquidalarm/alarm/AlarmForegroundService.kt", "val currentAlarm = repository.getAlarms().firstOrNull { it.id == alarm?.id }", "val currentAlarm = repository.getAlarms().firstOrNull { it.id == alarm?.id }")
replace("app/src/main/java/com/yourname/liquidalarm/alarm/AlarmForegroundService.kt",
"override fun onBind(intent: Intent?): IBinder? = null", "override fun onBind(intent: Intent): IBinder? = super.onBind(intent)")

# AlarmScheduler
replace("app/src/main/java/com/yourname/liquidalarm/alarm/AlarmScheduler.kt",
"import kotlinx.datetime.Instant", "import kotlinx.datetime.Instant\nimport kotlinx.datetime.Clock\nimport kotlin.math.abs")
replace("app/src/main/java/com/yourname/liquidalarm/alarm/AlarmScheduler.kt",
"Instant.now()", "Clock.System.now()")
replace("app/src/main/java/com/yourname/liquidalarm/alarm/AlarmScheduler.kt",
"(alarmId.hashCode() * 31 + (if (isPreAlarm) 1 else 0)).abs()", "abs(alarmId.hashCode() * 31 + (if (isPreAlarm) 1 else 0))")
replace("app/src/main/java/com/yourname/liquidalarm/alarm/AlarmScheduler.kt",
"(alarmId.hashCode() * 31).abs()", "abs(alarmId.hashCode() * 31)")
replace("app/src/main/java/com/yourname/liquidalarm/alarm/AlarmScheduler.kt",
"(alarmId.hashCode() * 31 + 1).abs()", "abs(alarmId.hashCode() * 31 + 1)")

# ChallengeManager
replace("app/src/main/java/com/yourname/liquidalarm/alarm/ChallengeManager.kt",
"import kotlinx.coroutines.launch", "import kotlinx.coroutines.launch\nimport kotlinx.coroutines.guava.await")
replace("app/src/main/java/com/yourname/liquidalarm/alarm/ChallengeManager.kt",
"barcodeScanner?.process(image)\n                .addOnSuccessListener", "barcodeScanner?.process(image)\n                ?.addOnSuccessListener")
replace("app/src/main/java/com/yourname/liquidalarm/alarm/ChallengeManager.kt",
"}\n                .addOnFailureListener", "}\n                ?.addOnFailureListener")
replace("app/src/main/java/com/yourname/liquidalarm/alarm/ChallengeManager.kt",
"val acceleration = Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH",
"val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()) - SensorManager.GRAVITY_EARTH")

# ChallengeQR
replace("app/src/main/java/com/yourname/liquidalarm/alarm/ChallengeQR.kt",
"import kotlinx.coroutines.Job", "import kotlinx.coroutines.Job\nimport kotlinx.coroutines.launch\nimport kotlinx.coroutines.guava.await")
replace("app/src/main/java/com/yourname/liquidalarm/alarm/ChallengeQR.kt",
"barcodeScanner?.process(image)\n                .addOnSuccessListener", "barcodeScanner?.process(image)\n                ?.addOnSuccessListener")
replace("app/src/main/java/com/yourname/liquidalarm/alarm/ChallengeQR.kt",
"}\n                .addOnFailureListener", "}\n                ?.addOnFailureListener")

# ChallengeShake
replace("app/src/main/java/com/yourname/liquidalarm/alarm/ChallengeShake.kt",
"val acceleration = Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH",
"val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()) - SensorManager.GRAVITY_EARTH")

# MediaPlayerManager
replace("app/src/main/java/com/yourname/liquidalarm/alarm/MediaPlayerManager.kt",
"import androidx.media3.exoplayer.upstream.DefaultDataSourceFactory", "import androidx.media3.common.Player\nimport androidx.media3.common.PlaybackException")
mp_old = """                val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "LiquidAlarm"))
                val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaItem(MediaItem.fromUri(uri))

                exoPlayer.setMediaSource(mediaSource)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
                exoPlayer.volume = 0f

                startVolumeFade(exoPlayer, onCompletion)

                exoPlayer.addListener(object : androidx.media3.exoplayer.Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == androidx.media3.exoplayer.Player.STATE_ENDED) {
                            fadeJob?.cancel()
                            onCompletion(true)
                        }
                    }
                    override fun onPlayerError(error: androidx.media3.exoplayer.ExoPlaybackException) {
                        fadeJob?.cancel()
                        onCompletion(false)
                    }
                })"""
mp_new = """                exoPlayer.setMediaItem(MediaItem.fromUri(uri))
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
                exoPlayer.volume = 0f

                startVolumeFade(exoPlayer, onCompletion)

                exoPlayer.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            fadeJob?.cancel()
                            onCompletion(true)
                        }
                    }
                    override fun onPlayerError(error: PlaybackException) {
                        fadeJob?.cancel()
                        onCompletion(false)
                    }
                })"""
replace("app/src/main/java/com/yourname/liquidalarm/alarm/MediaPlayerManager.kt", mp_old, mp_new)
replace("app/src/main/java/com/yourname/liquidalarm/alarm/MediaPlayerManager.kt", "import kotlinx.coroutines.Job", "import kotlinx.coroutines.Job\nimport kotlinx.coroutines.isActive")

# AlarmEntity
with open("app/src/main/java/com/yourname/liquidalarm/data/AlarmEntity.kt", "r") as f: ent = f.read()
ent = ent.replace("import kotlinx.datetime.MonthDay", "import com.yourname.liquidalarm.data.MonthDay")
ent = ent.replace("Instant.now()", "Clock.System.now()")
ent = ent.replace("import kotlinx.datetime.Instant", "import kotlinx.datetime.*\nimport kotlinx.datetime.Instant\nimport kotlinx.datetime.Clock")
ent = ent.replace("now.toInstant().toLocalDateTime(zone)", "now.toLocalDateTime(zone)")
ent = ent.replace("var candidate = zdt.with(targetTime).toInstant(zone)", "var candidate = kotlinx.datetime.LocalDateTime(zdt.year, zdt.month, zdt.dayOfMonth, targetTime.hour, targetTime.minute).let { kotlinx.datetime.toInstant(it, zone) }")
ent = ent.replace("candidate <= now", "candidate.toEpochMilliseconds() <= now.toEpochMilliseconds()")
ent = ent.replace("candidate > now", "candidate.toEpochMilliseconds() > now.toEpochMilliseconds()")
ent = ent.replace("next <= now", "next.toEpochMilliseconds() <= now.toEpochMilliseconds()")
ent = ent.replace("days.maxOrNull()?.ordinal?.minus(days.minOrNull()?.ordinal ?: 0) ?: 7", "((days.maxOrNull()?.ordinal ?: 0) - (days.minOrNull()?.ordinal ?: 0))")
ent = ent.replace("mainFireTime.minusSeconds(preAlarmConfig.minutesBefore * 60L)", "mainFireTime.minusSeconds(preAlarmConfig.minutesBefore * 60L)")
with open("app/src/main/java/com/yourname/liquidalarm/data/AlarmEntity.kt", "w") as f: f.write(ent)

# AppWidget
w_new = """package com.yourname.liquidalarm.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.text.Text

class AlarmWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { Text(text = "Liquid Alarm Widget") }
    }
    companion object { fun update(context: Context, glanceId: GlanceId, nextAlarm: Any?) {} }
}"""
with open("app/src/main/java/com/yourname/liquidalarm/widget/AlarmWidget.kt", "w") as f: f.write(w_new)

r_new = """package com.yourname.liquidalarm.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class AlarmWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AlarmWidget()
}"""
with open("app/src/main/java/com/yourname/liquidalarm/widget/AlarmWidgetReceiver.kt", "w") as f: f.write(r_new)

# Color and Type and Theme
replace("app/src/main/java/com/yourname/liquidalarm/ui/theme/Color.kt", "    shadow = Color(0xFF000000),\n", "")
replace("app/src/main/java/com/yourname/liquidalarm/ui/theme/Color.kt", "    shadow = Color(0xFF000000),", "")

replace("app/src/main/java/com/yourname/liquidalarm/ui/theme/Type.kt", "package com.yourname.liquidalarm.ui.theme", "package com.yourname.liquidalarm.ui.theme\nimport com.yourname.liquidalarm.R")
replace("app/src/main/java/com/yourname/liquidalarm/ui/theme/Theme.kt", "import android.graphics.Color", "")
replace("app/src/main/java/com/yourname/liquidalarm/ui/theme/Theme.kt", "insetsController.systemBarsAppearance = 0", "insetsController.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)")
replace("app/src/main/java/com/yourname/liquidalarm/ui/theme/Theme.kt", "insetsController.systemBarsAppearance = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS", "insetsController.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)")
replace("app/src/main/java/com/yourname/liquidalarm/ui/theme/Theme.kt", "Color.TRANSPARENT.toArgb()", "Color.Transparent.toArgb()")

# SettingsScreen and StopwatchScreen and TimerScreen and WorldClockScreen missing imports
with open("app/src/main/java/com/yourname/liquidalarm/ui/screens/WorldClockScreen.kt", "r") as f: wcs = f.read()
wcs = wcs.replace("androidx.compose.material.Icon", "androidx.compose.material3.Icon")
with open("app/src/main/java/com/yourname/liquidalarm/ui/screens/WorldClockScreen.kt", "w") as f: f.write(wcs)

