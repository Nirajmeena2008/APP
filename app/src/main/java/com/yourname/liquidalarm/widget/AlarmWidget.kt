package com.yourname.liquidalarm.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideGlance
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.yourname.liquidalarm.data.AlarmEntity
import kotlinx.datetime.LocalTime

class AlarmWidget : GlanceAppWidget() {
    @Composable
    override fun Content() {
        // This will be provided by provideGlance
    }

    companion object {
        fun update(context: androidx.glance.Context, glanceId: GlanceId, nextAlarm: AlarmEntity?) {
            provideGlance(context, glanceId) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = androidx.glance.layout.Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Liquid Alarm",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    androidx.glance.layout.Spacer(modifier = Modifier.padding(top = 8.dp))

                    if (nextAlarm != null) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = formatTime(nextAlarm.time),
                                style = TextStyle(
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )

                            if (nextAlarm.label.isNotBlank()) {
                                Text(
                                    text = nextAlarm.label,
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                )
                            }

                            Text(
                                text = getRepeatText(nextAlarm),
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            )
                        }
                    } else {
                        Text(
                            text = "No alarms set",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    androidx.glance.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

                    // Quick actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = androidx.glance.layout.Arrangement.SpaceEvenly
                    ) {
                        ActionButton("Add Alarm", "add_alarm")
                        ActionButton("Timer", "timer")
                        ActionButton("Stopwatch", "stopwatch")
                    }
                }
            }
        }

        private fun formatTime(time: LocalTime): String {
            return String.format("%02d:%02d", time.hour, time.minute)
        }

        private fun getRepeatText(alarm: AlarmEntity): String {
            return when (alarm.repeatRule) {
                is com.yourname.liquidalarm.data.RepeatRule.Once -> "Once"
                is com.yourname.liquidalarm.data.RepeatRule.Daily -> "Daily"
                is com.yourname.liquidalarm.data.RepeatRule.Weekdays -> "Weekdays"
                is com.yourname.liquidalarm.data.RepeatRule.Weekends -> "Weekends"
                is com.yourname.liquidalarm.data.RepeatRule.Weekly -> "Weekly"
                is com.yourname.liquidalarm.data.RepeatRule.Monthly -> "Monthly"
                is com.yourname.liquidalarm.data.RepeatRule.Yearly -> "Yearly"
                is com.yourname.liquidalarm.data.RepeatRule.Interval -> "Every ${alarm.repeatRule.days} days"
            }
        }

        @Composable
        fun ActionButton(label: String, action: String) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .background(Color(0xFF1A1A2E))
            ) {
                Text(
                    text = label,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .alignment(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
