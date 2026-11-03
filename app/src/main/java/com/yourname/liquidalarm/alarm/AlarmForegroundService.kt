package com.yourname.liquidalarm.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import com.yourname.liquidalarm.MainActivity
import com.yourname.liquidalarm.data.AlarmEntity
import com.yourname.liquidalarm.data.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class AlarmForegroundService : Service() {
    companion object {
        const val ACTION_MAIN_ALARM = "com.yourname.liquidalarm.ACTION_MAIN_ALARM"
        const val ACTION_PRE_ALARM = "com.yourname.liquidalarm.ACTION_PRE_ALARM"
        const val ACTION_DISMISS = "com.yourname.liquidalarm.ACTION_DISMISS"
        const val ACTION_SNOOZE = "com.yourname.liquidalarm.ACTION_SNOOZE"
        const val EXTRA_ALARM_ID = "alarm_id"
        const val EXTRA_IS_PRE_ALARM = "is_pre_alarm"
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "alarm_channel"
    }

    private lateinit var lifecycleRegistry: LifecycleRegistry
    private var mediaPlayerManager: MediaPlayerManager? = null
    private var alarm: AlarmEntity? = null
    private var isPreAlarm = false
    private var wakeLock: android.os.PowerManager.WakeLock? = null
    private var snoozeCount = 0
    private var maxSnoozeCount = 3
    private var snoozeDurationMs = 10 * 60 * 1000L
    private var autoDismissJob: Job? = null
    private var challengeCompleted = false

    override fun onCreate() {
        super.onCreate()
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        createNotificationChannel()

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "LiquidAlarm::ForegroundService"
        ).apply { acquire() }

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
            0
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmId = intent?.getStringExtra(EXTRA_ALARM_ID) ?: return START_NOT_STICKY
        isPreAlarm = intent.getBooleanExtra(EXTRA_IS_PRE_ALARM, false)

        CoroutineScope(Dispatchers.IO).launch {
            val repository = AlarmRepository(this@AlarmForegroundService)
            val currentAlarm = repository.getAlarms().firstOrNull { it.id == alarmId }

            if (currentAlarm != null) {
                alarm = currentAlarm
                maxSnoozeCount = currentAlarm.snooze.maxCount
                snoozeDurationMs = currentAlarm.snooze.durationMinutes * 60 * 1000L

                showNotification()

                mediaPlayerManager = MediaPlayerManager(this@AlarmForegroundService)
                mediaPlayerManager?.playAlarm(currentAlarm, isPreAlarm) { success ->
                    if (!success) {
                        stopSelf()
                    }
                }

                if (currentAlarm.autoDismiss.enabled) {
                    autoDismissJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(currentAlarm.autoDismiss.timeoutMinutes * 60 * 1000L)
                        if (!challengeCompleted) {
                            dismissAlarm()
                        }
                    }
                }
            } else {
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Active alarm notifications"
                setShowBadge(true)
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        val currentAlarm = alarm ?: return

        val dismissIntent = Intent(this, AlarmForegroundService::class.java).apply {
            action = ACTION_DISMISS
            putExtra(EXTRA_ALARM_ID, currentAlarm.id)
        }
        val snoozeIntent = Intent(this, AlarmForegroundService::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_ALARM_ID, currentAlarm.id)
        }

        val dismissPendingIntent = PendingIntent.getService(
            this, currentAlarm.id.hashCode(), dismissIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val snoozePendingIntent = PendingIntent.getService(
            this, currentAlarm.id.hashCode() + 1, snoozeIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(if (isPreAlarm) "Pre-Alarm: ${currentAlarm.label}" else "Alarm: ${currentAlarm.label}")
            .setContentText(currentAlarm.time.toString())
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(
                PendingIntent.getActivity(
                    this, 0,
                    Intent(this, MainActivity::class.java).apply {
                        putExtra("alarm_id", currentAlarm.id)
                        putExtra("is_pre_alarm", isPreAlarm)
                    },
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                ),
                true
            )
            .addAction(NotificationCompat.Action.Builder(
                android.R.drawable.ic_media_play,
                "Snooze",
                snoozePendingIntent
            ).build())
            .addAction(NotificationCompat.Action.Builder(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Dismiss",
                dismissPendingIntent
            ).build())
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    fun dismissAlarm() {
        challengeCompleted = true
        autoDismissJob?.cancel()
        mediaPlayerManager?.stop()
        stopForeground(true)
        stopSelf()
    }

    fun snoozeAlarm() {
        if (snoozeCount >= maxSnoozeCount) {
            dismissAlarm()
            return
        }

        snoozeCount++
        challengeCompleted = true
        mediaPlayerManager?.stop()

        CoroutineScope(Dispatchers.IO).launch {
            delay(snoozeDurationMs)
            if (!challengeCompleted) return@launch

            val repository = AlarmRepository(this@AlarmForegroundService)
            val currentAlarm = repository.getAlarms().firstOrNull { it.id == alarm?.id }
            if (currentAlarm != null && currentAlarm.isEnabled) {
                val decreasePercent = currentAlarm.snooze.decreasingIntervalPercent
                if (decreasePercent > 0) {
                    snoozeDurationMs = (snoozeDurationMs * (100 - decreasePercent) / 100).toLong()
                }

                val intent = Intent(this@AlarmForegroundService, AlarmForegroundService::class.java).apply {
                    action = ACTION_MAIN_ALARM
                    putExtra(EXTRA_ALARM_ID, alarm?.id)
                    putExtra(EXTRA_IS_PRE_ALARM, false)
                }
                startForegroundService(intent)
            }
        }

        dismissAlarm()
    }

    fun onChallengeCompleted() {
        challengeCompleted = true
        dismissAlarm()
    }

    override fun onDestroy() {
        autoDismissJob?.cancel()
        mediaPlayerManager?.stop()
        wakeLock?.release()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
