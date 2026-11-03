package com.yourname.liquidalarm.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Chip
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourname.liquidalarm.alarm.VibrationPatterns
import com.yourname.liquidalarm.data.VibrationPattern

@Composable
fun VibrationPicker(
    currentPattern: VibrationPattern,
    onPatternChange: (VibrationPattern) -> Unit,
    onPreview: (VibrationPattern) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Vibration Pattern", style = MaterialTheme.typography.titleMedium)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
        ) {
            VibrationPatterns.PREDEFINED_PATTERNS.forEach { (name, pattern) ->
                val isSelected = isPatternEqual(currentPattern, pattern)
                Chip(
                    onClick = { onPatternChange(pattern) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ChipDefaults.chipColors(
                        containerColor = if (isSelected) colorScheme.primaryContainer else colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSurface
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isSelected) {
                                androidx.compose.material.icons.filled.Check(
                                    contentDescription = null,
                                    tint = colorScheme.primary
                                )
                                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 8.dp))
                            }
                            androidx.compose.material3.IconButton(onClick = { onPreview(pattern) }) {
                                androidx.compose.material.Icon(
                                    androidx.compose.material.icons.filled.Vibration,
                                    contentDescription = "Preview",
                                    tint = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Custom pattern option
            val isCustomSelected = isCustomPattern(currentPattern)
            Chip(
                onClick = { /* Show custom pattern editor */ },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ChipDefaults.chipColors(
                    containerColor = if (isCustomSelected) colorScheme.primaryContainer else colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Custom Pattern",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (isCustomSelected) colorScheme.onPrimaryContainer else colorScheme.onSurface
                        )
                    )
                    if (isCustomSelected) {
                        androidx.compose.material.icons.filled.Check(
                            contentDescription = null,
                            tint = colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

private fun isPatternEqual(a: VibrationPattern, b: VibrationPattern): Boolean {
    return when {
        a is VibrationPattern.Default && b is VibrationPattern.Default -> true
        a is VibrationPattern.None && b is VibrationPattern.None -> true
        a is VibrationPattern.System && b is VibrationPattern.System -> a.patternName == b.patternName
        a is VibrationPattern.Custom && b is VibrationPattern.Custom -> a.pattern == b.pattern
        else -> false
    }
}

private fun isCustomPattern(pattern: VibrationPattern): Boolean {
    return pattern is VibrationPattern.Custom
}
