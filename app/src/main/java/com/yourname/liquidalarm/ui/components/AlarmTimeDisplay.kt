package com.yourname.liquidalarm.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourname.liquidalarm.data.AlarmEntity
import com.yourname.liquidalarm.ui.theme.LiquidColors
import kotlinx.datetime.LocalTime

@Composable
fun AlarmTimeDisplay(
    time: LocalTime,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    showSeconds: Boolean = false,
    is24Hour: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme
    val textColor = animateColorAsState(
        targetValue = if (isActive) colorScheme.primary else colorScheme.onSurface,
        animationSpec = tween(300)
    )

    val scale = animateFloatAsState(
        targetValue = if (isActive) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
    )

    val pulseAlpha = remember { mutableStateOf(0.3f) }
    if (isActive) {
        androidx.compose.runtime.LaunchedEffect(Unit) {
            while (true) {
                androidx.compose.animation.animateTo(pulseAlpha, 1f, animationSpec = tween(1500))
                androidx.compose.animation.animateTo(pulseAlpha, 0.3f, animationSpec = tween(1500))
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatTime(time, showSeconds, is24Hour),
                style = MaterialTheme.typography.displayLarge.copy(
                    color = textColor.value,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            if (isActive) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(
                            color = colorScheme.primary.copy(alpha = pulseAlpha.value),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun NextAlarmDisplay(
    alarm: AlarmEntity?,
    modifier: Modifier = Modifier,
    is24Hour: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme

    alarm?.let { alarm ->
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Next Alarm",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = colorScheme.onSurfaceVariant
                )
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 4.dp))
            Text(
                text = formatTime(alarm.time, false, is24Hour),
                style = MaterialTheme.typography.displayMedium.copy(
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            )
            if (alarm.label.isNotBlank()) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 4.dp))
                Text(
                    text = alarm.label,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = colorScheme.onSurfaceVariant
                    )
                )
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 4.dp))
            Text(
                text = getRepeatText(alarm),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = colorScheme.onSurfaceVariant
                )
            )
        }
    } ?: Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No alarms set",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = colorScheme.onSurfaceVariant
            )
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
        Text(
            text = "Tap + to create your first alarm",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = colorScheme.onSurfaceVariant
            )
        )
    }
}

private fun formatTime(time: LocalTime, showSeconds: Boolean, is24Hour: Boolean): String {
    val hour = time.hour
    val minute = time.minute
    val second = time.second

    if (is24Hour) {
        return if (showSeconds) {
            String.format("%02d:%02d:%02d", hour, minute, second)
        } else {
            String.format("%02d:%02d", hour, minute)
        }
    } else {
        val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        val amPm = if (hour >= 12) "PM" else "AM"
        return if (showSeconds) {
            String.format("%02d:%02d:%02d %s", displayHour, minute, second, amPm)
        } else {
            String.format("%02d:%02d %s", displayHour, minute, amPm)
        }
    }
}

private fun getRepeatText(alarm: AlarmEntity): String {
    return when (alarm.repeatRule) {
        is com.yourname.liquidalarm.data.RepeatRule.Once -> "Once"
        is com.yourname.liquidalarm.data.RepeatRule.Daily -> "Daily"
        is com.yourname.liquidalarm.data.RepeatRule.Weekdays -> "Weekdays"
        is com.yourname.liquidalarm.data.RepeatRule.Weekends -> "Weekends"
        is com.yourname.liquidalarm.data.RepeatRule.Weekly -> {
            val days = (alarm.repeatRule as com.yourname.liquidalarm.data.RepeatRule.Weekly).days
                .map { it.shortName }.joinToString(", ")
            "Weekly ($days)"
        }
        is com.yourname.liquidalarm.data.RepeatRule.Monthly -> "Monthly (day ${(alarm.repeatRule as com.yourname.liquidalarm.data.RepeatRule.Monthly).dayOfMonth})"
        is com.yourname.liquidalarm.data.RepeatRule.Yearly -> "Yearly"
        is com.yourname.liquidalarm.data.RepeatRule.Interval -> "Every ${(alarm.repeatRule as com.yourname.liquidalarm.data.RepeatRule.Interval).days} days"
    }
}

private val kotlinx.datetime.DayOfWeek.shortName: String
    get() = when (this) {
        kotlinx.datetime.DayOfWeek.MONDAY -> "Mon"
        kotlinx.datetime.DayOfWeek.TUESDAY -> "Tue"
        kotlinx.datetime.DayOfWeek.WEDNESDAY -> "Wed"
        kotlinx.datetime.DayOfWeek.THURSDAY -> "Thu"
        kotlinx.datetime.DayOfWeek.FRIDAY -> "Fri"
        kotlinx.datetime.DayOfWeek.SATURDAY -> "Sat"
        kotlinx.datetime.DayOfWeek.SUNDAY -> "Sun"
    }
