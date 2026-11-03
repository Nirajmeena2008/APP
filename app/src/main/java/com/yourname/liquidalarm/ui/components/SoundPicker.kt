package com.yourname.liquidalarm.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filed.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filed.Folder
import androidx.compose.material3.Button
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
import com.yourname.liquidalarm.data.AlarmSound

@Composable
fun SoundPicker(
    currentSound: AlarmSound,
    onSoundChange: (AlarmSound) -> Unit,
    onPickCustomFile: () -> Unit,
    onPreviewSound: (AlarmSound) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val isPreviewing by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Alarm Sound", style = MaterialTheme.typography.titleMedium)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))

        // System sounds
        Text("System Sounds", style = MaterialTheme.typography.labelMedium.copy(color = colorScheme.onSurfaceVariant))
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
        ) {
            SystemSoundOption(
                sound = AlarmSound.SystemDefault,
                label = "Default Alarm",
                isSelected = currentSound == AlarmSound.SystemDefault,
                onClick = { onSoundChange(AlarmSound.SystemDefault) },
                onPreview = { onPreviewSound(AlarmSound.SystemDefault) }
            )
            SystemSoundOption(
                sound = AlarmSound.Silent,
                label = "Silent (Vibration Only)",
                isSelected = currentSound == AlarmSound.Silent,
                onClick = { onSoundChange(AlarmSound.Silent) },
                onPreview = { onPreviewSound(AlarmSound.Silent) }
            )
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

        // Custom file picker
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Custom Sound", style = MaterialTheme.typography.labelMedium.copy(color = colorScheme.onSurfaceVariant))
            Button(onClick = onPickCustomFile) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material.Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 8.dp))
                    Text("Browse Files")
                }
            }
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))

        // Currently selected custom sound
        when (currentSound) {
            is AlarmSound.CustomFile -> {
                Chip(
                    onClick = { onPreviewSound(currentSound) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ChipDefaults.chipColors(
                        containerColor = colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.material.Icon(Icons.Default.MusicNote, contentDescription = null, tint = colorScheme.primary)
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 12.dp))
                            Column {
                                Text(
                                    text = currentSound.displayName,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = colorScheme.onPrimaryContainer)
                                )
                                Text(
                                    text = "Custom file",
                                    style = MaterialTheme.typography.labelSmall.copy(color = colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                )
                            }
                        }
                        androidx.compose.material.Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Preview",
                            tint = colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SystemSoundOption(
    sound: AlarmSound,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onPreview: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Chip(
        onClick = onClick,
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
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSurface
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSelected) {
                    androidx.compose.material.icons.filed.Check(
                        contentDescription = null,
                        tint = colorScheme.primary
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 8.dp))
                }
                androidx.compose.material3.IconButton(onClick = onPreview) {
                    androidx.compose.material.Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Preview",
                        tint = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
