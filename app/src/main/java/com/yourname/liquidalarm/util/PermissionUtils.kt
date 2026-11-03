package com.yourname.liquidalarm.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest

object PermissionUtils {
    private const val REQUEST_EXACT_ALARM = 1001
    private const val REQUEST_DISPLAY_OVER_APPS = 1002
    private const val REQUEST_BATTERY_OPTIMIZATION = 1003

    fun requestExactAlarmPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                activity.startActivityForResult(intent, REQUEST_EXACT_ALARM)
            }
        }
    }

    fun hasExactAlarmPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            return alarmManager.canScheduleExactAlarms()
        }
        return true
    }

    fun openDisplayOverAppsSettings(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${activity.packageName}"))
            activity.startActivityForResult(intent, REQUEST_DISPLAY_OVER_APPS)
        }
    }

    fun hasDisplayOverAppsPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context)
        }
        return true
    }

    fun openBatteryOptimizationSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        activity.startActivityForResult(intent, REQUEST_BATTERY_OPTIMIZATION)
    }

    fun isBatteryOptimizationIgnored(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            return powerManager.isIgnoringBatteryOptimizations(context.packageName)
        }
        return true
    }

    fun requestIgnoreBatteryOptimization(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse("package:${activity.packageName}")
            activity.startActivityForResult(intent, REQUEST_BATTERY_OPTIMIZATION)
        }
    }

    fun checkAllCriticalPermissions(context: Context): Boolean {
        return hasExactAlarmPermission(context) &&
               hasDisplayOverAppsPermission(context) &&
               isBatteryOptimizationIgnored(context)
    }

    fun requestAllPermissions(activity: Activity) {
        requestExactAlarmPermission(activity)
        if (!hasDisplayOverAppsPermission(activity)) {
            openDisplayOverAppsSettings(activity)
        }
        if (!isBatteryOptimizationIgnored(activity)) {
            requestIgnoreBatteryOptimization(activity)
        }
    }
}