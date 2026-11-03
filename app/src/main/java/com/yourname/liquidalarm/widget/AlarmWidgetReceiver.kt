package com.yourname.liquidalarm.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.update
import com.yourname.liquidalarm.data.AlarmEntity
import com.yourname.liquidalarm.data.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmWidgetReceiver : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        CoroutineScope(Dispatchers.IO).launch {
            val repository = AlarmRepository(context)
            val alarms = repository.getAlarms()
            updateWidgets(context, alarms)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "com.yourname.liquidalarm.WIDGET_UPDATE_ALARMS") {
            CoroutineScope(Dispatchers.IO).launch {
                val repository = AlarmRepository(context)
                val alarms = repository.getAlarms()
                updateWidgets(context, alarms)
            }
        }
    }

    private fun updateWidgets(context: Context, alarms: List<AlarmEntity>) {
        val nextAlarm = alarms
            .filter { it.isEnabled }
            .minByOrNull { it.nextFireTime?.toEpochMilliseconds() ?: Long.MAX_VALUE }

        val glanceIds = GlanceAppWidget.getGlanceIds(
            ComponentName(context, AlarmWidgetReceiver::class.java)
        )
        glanceIds.forEach { glanceId ->
            AlarmWidget.update(context, glanceId, nextAlarm)
        }
    }
}
