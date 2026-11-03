package com.yourname.liquidalarm.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.yourname.liquidalarm.data.AlarmEntity
import com.yourname.liquidalarm.data.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlin.math.abs
import kotlinx.datetime.TimeZone

object AlarmScheduler {
    private const val ACTION_ALARM_FIRE = "com.yourname.liquidalarm.ALARM_FIRE"
    private const val ACTION_PRE_ALARM_FIRE = "com.yourname.liquidalarm.PRE_ALARM_FIRE"
    private const val EXTRA_ALARM_ID = "alarm_id"
    private const val EXTRA_IS_PRE_ALARM = "is_pre_alarm"

    fun scheduleAlarm(context: Context, alarm: AlarmEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val repository = AlarmRepository(context)

        cancelAlarm(context, alarm.id)

        if (!alarm.isEnabled) return

        val now = Clock.System.now()
        val zone = TimeZone.currentSystemDefault()

        val mainFireTime = alarm.computeNextFireTime(now, zone)
        mainFireTime?.let { fireTime ->
            val triggerMillis = fireTime.toEpochMilliseconds()
            if (triggerMillis > System.currentTimeMillis()) {
                setExactAlarm(context, alarmManager, alarm.id, triggerMillis, false)
            }
        }

        if (alarm.preAlarmConfig.enabled) {
            val mainFire = alarm.computeNextFireTime(now, zone)
            mainFire?.let { mainTime ->
                val preFireTime = alarm.computePreAlarmFireTime(mainTime)
                preFireTime?.let { preTime ->
                    val triggerMillis = preTime.toEpochMilliseconds()
                    if (triggerMillis > System.currentTimeMillis()) {
                        setExactAlarm(context, alarmManager, alarm.id, triggerMillis, true)
                    }
                }
            }
        }
    }

    private fun setExactAlarm(
        context: Context,
        alarmManager: AlarmManager,
        alarmId: String,
        triggerMillis: Long,
        isPreAlarm: Boolean
    ) {
        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            action = if (isPreAlarm) ACTION_PRE_ALARM_FIRE else ACTION_ALARM_FIRE
            putExtra(EXTRA_ALARM_ID, alarmId)
            putExtra(EXTRA_IS_PRE_ALARM, isPreAlarm)
        }

        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val requestCode = abs(alarmId.hashCode() * 31 + (if (isPreAlarm) 1 else 0))

        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent, flags
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
        }
    }

    fun cancelAlarm(context: Context, alarmId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        val mainRequestCode = abs(alarmId.hashCode() * 31)
        val mainPendingIntent = PendingIntent.getBroadcast(context, mainRequestCode, intent, flags)
        alarmManager.cancel(mainPendingIntent)

        val preRequestCode = abs(alarmId.hashCode() * 31 + 1)
        val prePendingIntent = PendingIntent.getBroadcast(context, preRequestCode, intent, flags)
        alarmManager.cancel(prePendingIntent)
    }

    fun rescheduleAll(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val repository = AlarmRepository(context)
            val alarms = repository.getAlarms()
            alarms.forEach { scheduleAlarm(context, it) }
        }
    }

    fun enableBootReceiver(context: Context, enable: Boolean) {
        val component = ComponentName(context, "com.yourname.liquidalarm.alarm.AlarmBroadcastReceiver")
        val newState = if (enable) PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        context.packageManager.setComponentEnabledSetting(component, newState, PackageManager.DONT_KILL_APP)
    }
}