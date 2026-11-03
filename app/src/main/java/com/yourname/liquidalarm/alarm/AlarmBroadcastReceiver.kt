package com.yourname.liquidalarm.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import com.yourname.liquidalarm.data.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when (action) {
            "com.yourname.liquidalarm.ALARM_FIRE" -> {
                val alarmId = intent.getStringExtra("alarm_id") ?: return
                val isPreAlarm = intent.getBooleanExtra("is_pre_alarm", false)
                handleAlarmFire(context, alarmId, isPreAlarm)
            }
            "com.yourname.liquidalarm.PRE_ALARM_FIRE" -> {
                val alarmId = intent.getStringExtra("alarm_id") ?: return
                handleAlarmFire(context, alarmId, true)
            }
            "android.intent.action.BOOT_COMPLETED",
            "android.intent.action.MY_PACKAGE_REPLACED",
            "android.intent.action.TIMEZONE_CHANGED",
            "android.intent.action.TIME_SET" -> {
                AlarmScheduler.rescheduleAll(context)
            }
        }
    }

    private fun handleAlarmFire(context: Context, alarmId: String, isPreAlarm: Boolean) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "LiquidAlarm::AlarmFire::$alarmId"
        ).apply {
            acquire(10 * 60 * 1000L)
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = AlarmRepository(context)
                val alarm = repository.getAlarms().firstOrNull { it.id == alarmId }
                if (alarm != null && alarm.isEnabled) {
                    val serviceIntent = Intent(context, AlarmForegroundService::class.java).apply {
                        action = if (isPreAlarm) AlarmForegroundService.ACTION_PRE_ALARM
                        else AlarmForegroundService.ACTION_MAIN_ALARM
                        putExtra("alarm_id", alarmId)
                        putExtra("is_pre_alarm", isPreAlarm)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                }
            } finally {
                wakeLock.release()
            }
        }
    }
}