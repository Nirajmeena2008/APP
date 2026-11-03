package com.yourname.liquidalarm.widget

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
}