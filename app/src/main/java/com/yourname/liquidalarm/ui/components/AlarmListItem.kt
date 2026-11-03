package com.yourname.liquidalarm.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.fillMaxWidth
import androidx.compose.ui.layout.preferredWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourname.liquidalarm.data.AlarmEntity
import com.yourname.liquidalarm.ui.components.GlassCard
import com.yourname.liquidalarm.ui.theme.LiquidColors

@Composable
fun AlarmListItem(
    alarm: AlarmEntity,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onDrag: () -> Unit,
    isDragging: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val elevation by animateFloatAsState(
        targetValue = if (isDragging) 8f else 0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
    )

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.shadowElevation = elevation
                translationZ = elevation
            }
            .pointerInput(Unit) {
                detectTapGestures(onTap = onClick)
            }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Drag handle
            IconButton(onClick = onDrag) {
                Icon(
                    imageVector = Icons.Default.DragIndicator,
                    contentDescription = "Drag to reorder",
                    tint = colorScheme.onSurfaceVariant
                )
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 8.dp))

            // Time and label
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(alarm.time),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = if (alarm.isEnabled) colorScheme.onSurface else colorScheme.onSurfaceVariant,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    )
                    Switch(
                        checked = alarm.isEnabled,
                        onCheckedChange = onToggle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .preferredWidth(60.dp)
                    )
                }

                if (alarm.label.isNotBlank()) {
                    Text(
                        text = alarm.label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                    )
                }

                // Info chips row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    InfoChip(
                        icon = Icons.Default.MusicNote,
                        text = getSoundName(alarm.mainSound),
                        colorScheme = colorScheme
                    )
                    if (alarm.preAlarmConfig.enabled) {
                        InfoChip(
                            icon = Icons.Default.PlayArrow,
                            text = "Pre: ${getSoundName(alarm.preAlarmConfig.sound)}",
                            colorScheme = colorScheme
                        )
                    }
                    InfoChip(
                        icon = Icons.Default.Vibration,
                        text = getVibrationName(alarm.vibration),
                        colorScheme = colorScheme
                    )
                    InfoChip(
                        icon = Icons.Default.PlayArrow,
                        text = getChallengeName(alarm.challenge),
                        colorScheme = colorScheme
                    )
                }
            }

            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete alarm",
                    tint = colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.material.icons.filled.Icon,
    text: String,
    colorScheme: androidx.compose.material3.ColorScheme
) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp)
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            ),
            maxLines = 1,
            overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
        )
    }
}

private fun formatTime(time: kotlinx.datetime.LocalTime): String {
    return String.format("%02d:%02d", time.hour, time.minute)
}

private fun getSoundName(sound: com.yourname.liquidalarm.data.AlarmSound): String {
    return when (sound) {
        is com.yourname.liquidalarm.data.AlarmSound.SystemDefault -> "Default"
        is com.yourname.liquidalarm.data.AlarmSound.Silent -> "Silent"
        is com.yourname.liquidalarm.data.AlarmSound.CustomFile -> sound.displayName
        is com.yourname.liquidalarm.data.AlarmSound.SystemRingtone -> "System"
    }
}

private fun getVibrationName(pattern: com.yourname.liquidalarm.data.VibrationPattern): String {
    return when (pattern) {
        is com.yourname.liquidalarm.data.VibrationPattern.Default -> "Default"
        is com.yourname.liquidalarm.data.VibrationPattern.None -> "None"
        is com.yourname.liquidalarm.data.VibrationPattern.Custom -> "Custom"
        is com.yourname.liquidalarm.data.VibrationPattern.System -> pattern.patternName
    }
}

private fun getChallengeName(challenge: com.yourname.liquidalarm.data.ChallengeType): String {
    return when (challenge) {
        com.yourname.liquidalarm.data.ChallengeType.Math -> "Math"
        com.yourname.liquidalarm.data.ChallengeType.QR -> "QR Code"
        com.yourname.liquidalarm.data.ChallengeType.Shake -> "Shake"
        com.yourname.liquidalarm.data.ChallengeType.None -> "None"
    }
}