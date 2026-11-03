package com.yourname.liquidalarm

import android.app.Application
import android.content.Context
import com.yourname.liquidalarm.data.PreferencesManager
import com.yourname.liquidalarm.alarm.AlarmScheduler
import com.yourname.liquidalarm.util.PermissionUtils

class LiquidAlarmApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PreferencesManager(this)
        AlarmScheduler.rescheduleAll(this)
        PermissionUtils.requestExactAlarmPermission(this)
    }

    companion object {
        lateinit var instance: LiquidAlarmApplication
            private set
    }
}