package com.yourname.liquidalarm.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourname.liquidalarm.data.FadeCurve
import com.yourname.liquidalarm.data.VolumeFade

@Composable
fun VolumeRampSlider(
    currentFade: VolumeFade,
    onFadeChange: (VolumeFade) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    var enabled by remember { mutableStateOf(currentFade.enabled) }
    var duration by remember { mutableStateOf(currentFade.durationSeconds) }
    var curve by remember { mutableStateOf(currentFade.curve) }

    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.titleMedium)
            androidx.compose.material3.Switch(
                checked = enabled,
                onCheckedChange = { newEnabled ->
                    enabled = newEnabled
                    onFadeChange(currentFade.copy(enabled = newEnabled))
                }
            )
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))

        if (enabled) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
            ) {
                // Duration slider
                VStack(spacing = 8.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Fade Duration: ${duration}s",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = formatDuration(duration),
                            style = MaterialTheme.typography.bodyMedium.copy(color = colorScheme.primary)
                        )
                    }
                    Slider(
                        value = duration.toFloat(),
                        onValueChange = { newDuration ->
                            duration = newDuration.roundToInt()
                            onFadeChange(currentFade.copy(durationSeconds = duration))
                        },
                        valueRange = 30f..300f,
                        steps = 27, // 30, 40, 50... 300
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = colorScheme.primary,
                            activeTrackColor = colorScheme.primary,
                            inactiveTrackColor = colorScheme.outlineVariant
                        )
                    )
                }

                // Curve selector
                Text("Fade Curve", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    FadeCurve.values().forEach { c ->
                        val isSelected = curve == c
                        androidx.compose.material3.Chip(
                            onClick = {
                                curve = c
                                onFadeChange(currentFade.copy(curve = c))
                            },
                            modifier = Modifier.weight(1f),
                            colors = androidx.compose.material3.ChipDefaults.chipColors(
                                containerColor = if (isSelected) colorScheme.primaryContainer else colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = c.name,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VStack(
    modifier: Modifier = Modifier,
    spacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(spacing),
        content = content
    )
}

private fun formatDuration(seconds: Int): String {
    return when {
        seconds < 60 -> "${seconds}s"
        seconds < 3600 -> "${seconds / 60}m ${seconds % 60}s"
        else -> "${seconds / 3600}h ${(seconds % 3600) / 60}m"
    }
}
