package com.yourname.liquidalarm.tile

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.yourname.liquidalarm.alarm.AlarmScheduler
import com.yourname.liquidalarm.data.AlarmEntity
import com.yourname.liquidalarm.data.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone

class QuickAlarmTileService : TileService() {
    override fun onTileAdded() {
        super.onTileAdded()
        updateTileState()
    }

    override fun onTileRemoved() {
        super.onTileRemoved()
    }

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        CoroutineScope(Dispatchers.IO).launch {
            val repository = AlarmRepository(this@QuickAlarmTileService)
            val now = Instant.now()
            val zone = TimeZone.currentSystemDefault()

            // Create a quick alarm for 30 minutes from now
            val alarmTime = now.plusSeconds(30 * 60).toLocalTime(zone)
            val quickAlarm = AlarmEntity(
                time = alarmTime,
                label = "Quick Alarm",
                isEnabled = true,
                repeatRule = com.yourname.liquidalarm.data.RepeatRule.Once,
                snooze = com.yourname.liquidalarm.data.SnoozeConfig(10, 1, false),
                challenge = com.yourname.liquidalarm.data.ChallengeType.None
            )

            repository.addAlarm(quickAlarm)
            AlarmScheduler.scheduleAlarm(this@QuickAlarmTileService, quickAlarm)

            runOnUiThread {
                Toast.makeText(this@QuickAlarmTileService, "Alarm set for 30 minutes", Toast.LENGTH_SHORT).show()
            }

            updateTileState()
        }
    }

    private fun updateTileState() {
        val tile = getQsTile()
        val state = if (hasActiveQuickAlarm()) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.state = state
        tile.updateTile()
    }

    private fun hasActiveQuickAlarm(): Boolean {
        // Check if there's a quick alarm (simplified)
        return false
    }
}
