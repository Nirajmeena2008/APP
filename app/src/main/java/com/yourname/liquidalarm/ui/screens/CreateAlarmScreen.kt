package com.yourname.liquidalarm.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.yourname.liquidalarm.data.AlarmSound
import com.yourname.liquidalarm.data.RepeatRule
import com.yourname.liquidalarm.ui.components.GlassBottomSheet
import com.yourname.liquidalarm.ui.components.RepeatChips
import com.yourname.liquidalarm.ui.components.SoundPicker
import com.yourname.liquidalarm.ui.components.TimePickerDialog
import com.yourname.liquidalarm.ui.components.VibrationPicker
import com.yourname.liquidalarm.ui.components.VolumeRampSlider

@Composable
fun CreateAlarmScreen(
    initialAlarm: AlarmEntity?,
    onSave: (AlarmEntity) -> Unit,
    onDismiss: () -> Unit,
    onPickSoundFile: () -> Unit,
    onPreviewSound: (AlarmSound) -> Unit,
    onPreviewVibration: (com.yourname.liquidalarm.data.VibrationPattern) -> Unit
) {
    val alarm = initialAlarm ?: AlarmEntity(time = kotlinx.datetime.LocalTime.now().plusMinutes(10))
    
    var editingAlarm by remember { mutableStateOf(alarm) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showRepeatSheet by remember { mutableStateOf(false) }
    var showSoundPicker by remember { mutableStateOf(false) }
    var showVibrationPicker by remember { mutableStateOf(false) }
    var showMainSoundPicker by remember { mutableStateOf(false) }
    var showPreAlarmSoundPicker by remember { mutableStateOf(false) }
    var label by remember { mutableStateOf(alarm.label) }
    var selectedChallenge by remember { mutableStateOf(alarm.challenge) }

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top app bar
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text(text = if (initialAlarm == null) "New Alarm" else "Edit Alarm") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onDismiss) {
                        androidx.compose.material.Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            onSave(editingAlarm.copyWithModified(label = label))
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Save", style = MaterialTheme.typography.labelLarge)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )

            // Scrollable content
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Time picker
                GlassBottomSheet(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column {
                        Text("Time", style = MaterialTheme.typography.titleMedium)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
                        TimePickerDialog(
                            initialTime = editingAlarm.time,
                            is24Hour = true,
                            onConfirm = { time ->
                                editingAlarm = editingAlarm.copyWithModified(time = time)
                                showTimePicker = false
                            },
                            onDismiss = { showTimePicker = false }
                        )
                    }
                }

                // Label
                GlassBottomSheet(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column {
                        Text("Label", style = MaterialTheme.typography.titleMedium)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
                        FilledTextField(
                            value = label,
                            onValueChange = { label = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Alarm name (optional)") },
                            singleLine = true,
                            maxLines = 1
                        )
                    }
                }

                // Repeat
                GlassBottomSheet(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Repeat", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = getRepeatSummary(editingAlarm.repeatRule),
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
                        RepeatChips(
                            currentRule = editingAlarm.repeatRule,
                            onRuleChange = { rule ->
                                editingAlarm = editingAlarm.copyWithModified(repeatRule = rule)
                            }
                        )
                    }
                }

                // Main Alarm Sound
                GlassBottomSheet(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Main Alarm Sound", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = getSoundName(editingAlarm.mainSound),
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
                        SoundPicker(
                            currentSound = editingAlarm.mainSound,
                            onSoundChange = { sound ->
                                editingAlarm = editingAlarm.copyWithModified(mainSound = sound)
                            },
                            onPickCustomFile = onPickSoundFile,
                            onPreviewSound = onPreviewSound
                        )
                    }
                }

                // Main Volume Fade
                GlassBottomSheet(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    VolumeRampSlider(
                        currentFade = editingAlarm.mainSoundVolumeFade,
                        onFadeChange = { fade ->
                            editingAlarm = editingAlarm.copyWithModified(mainSoundVolumeFade = fade)
                        },
                        label = "Volume Fade (Main Alarm)"
                    )
                }

                // Pre-Alarm Config
                GlassBottomSheet(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Pre-Alarm (Gentle Wake)", style = MaterialTheme.typography.titleMedium)
                            androidx.compose.material3.Switch(
                                checked = editingAlarm.preAlarmConfig.enabled,
                                onCheckedChange = { enabled ->
                                    editingAlarm = editingAlarm.copyWithModified(
                                        preAlarmConfig = editingAlarm.preAlarmConfig.copy(enabled = enabled)
                                    )
                                }
                            )
                        }

                        if (editingAlarm.preAlarmConfig.enabled) {
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))

                            // Minutes before
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                            ) {
                                Text("Minutes Before", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = "${editingAlarm.preAlarmConfig.minutesBefore} min",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
                                )
                            }
                            androidx.compose.material3.Slider(
                                value = editingAlarm.preAlarmConfig.minutesBefore.toFloat(),
                                onValueChange = { value ->
                                    editingAlarm = editingAlarm.copyWithModified(
                                        preAlarmConfig = editingAlarm.preAlarmConfig.copy(
                                            minutesBefore = value.roundToInt().coerceIn(5, 30)
                                        )
                                    )
                                },
                                valueRange = 5f..30f,
                                steps = 5,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Pre-alarm sound
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                            ) {
                                Text("Pre-Alarm Sound", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = getSoundName(editingAlarm.preAlarmConfig.sound),
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            )
                            SoundPicker(
                                currentSound = editingAlarm.preAlarmConfig.sound,
                                onSoundChange = { sound ->
                                    editingAlarm = editingAlarm.copyWithModified(
                                        preAlarmConfig = editingAlarm.preAlarmConfig.copy(sound = sound)
                                    )
                                },
                                onPickCustomFile = onPickSoundFile,
                                onPreviewSound = onPreviewSound
                            )

                            // Pre-alarm volume fade
                            VolumeRampSlider(
                                currentFade = editingAlarm.preAlarmConfig.volumeFade,
                                onFadeChange = { fade ->
                                    editingAlarm = editingAlarm.copyWithModified(
                                        preAlarmConfig = editingAlarm.preAlarmConfig.copy(volumeFade = fade)
                                    )
                                },
                                label = "Volume Fade (Pre-Alarm)"
                            )
                        }
                    }
                }

                // Vibration
                GlassBottomSheet(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    VibrationPicker(
                        currentPattern = editingAlarm.vibration,
                        onPatternChange = { pattern ->
                            editingAlarm = editingAlarm.copyWithModified(vibration = pattern)
                        },
                        onPreview = onPreviewVibration
                    )
                }

                // Challenge
                GlassBottomSheet(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column {
                        Text("Wake Challenge", style = MaterialTheme.typography.titleMedium)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
                        ChallengeSelector(
                            currentChallenge = selectedChallenge,
                            onChallengeChange = { challenge ->
                                selectedChallenge = challenge
                                editingAlarm = editingAlarm.copyWithModified(challenge = challenge)
                            }
                        )
                    }
                }

                // Snooze
                GlassBottomSheet(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column {
                        Text("Snooze", style = MaterialTheme.typography.titleMedium)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))

                        // Duration
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                        ) {
                            Text("Duration", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${editingAlarm.snooze.durationMinutes} min",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                        androidx.compose.material3.Slider(
                            value = editingAlarm.snooze.durationMinutes.toFloat(),
                            onValueChange = { value ->
                                editingAlarm = editingAlarm.copyWithModified(
                                    snooze = editingAlarm.snooze.copy(durationMinutes = value.roundToInt().coerceIn(1, 30))
                                )
                            },
                            valueRange = 1f..30f,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Max count
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                        ) {
                            Text("Max Snoozes", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${editingAlarm.snooze.maxCount}",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                        androidx.compose.material3.Slider(
                            value = editingAlarm.snooze.maxCount.toFloat(),
                            onValueChange = { value ->
                                editingAlarm = editingAlarm.copyWithModified(
                                    snooze = editingAlarm.snooze.copy(maxCount = value.roundToInt().coerceIn(1, 10))
                                )
                            },
                            valueRange = 1f..10f,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Auto snooze
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                        ) {
                            Text("Auto Snooze", style = MaterialTheme.typography.bodyMedium)
                            androidx.compose.material3.Switch(
                                checked = editingAlarm.snooze.autoSnooze,
                                onCheckedChange = { enabled ->
                                    editingAlarm = editingAlarm.copyWithModified(
                                        snooze = editingAlarm.snooze.copy(autoSnooze = enabled)
                                    )
                                }
                            )
                        }

                        // Decreasing interval
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                        ) {
                            Text("Decrease Interval", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${editingAlarm.snooze.decreasingIntervalPercent}%",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                        androidx.compose.material3.Slider(
                            value = editingAlarm.snooze.decreasingIntervalPercent.toFloat(),
                            onValueChange = { value ->
                                editingAlarm = editingAlarm.copyWithModified(
                                    snooze = editingAlarm.snooze.copy(decreasingIntervalPercent = value.roundToInt().coerceIn(0, 50))
                                )
                            },
                            valueRange = 0f..50f,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Auto-dismiss
                GlassBottomSheet(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column {
                        Text("Auto Dismiss", style = MaterialTheme.typography.titleMedium)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                        ) {
                            Text("Enable Auto Dismiss", style = MaterialTheme.typography.bodyMedium)
                            androidx.compose.material3.Switch(
                                checked = editingAlarm.autoDismiss.enabled,
                                onCheckedChange = { enabled ->
                                    editingAlarm = editingAlarm.copyWithModified(
                                        autoDismiss = editingAlarm.autoDismiss.copy(enabled = enabled)
                                    )
                                }
                            )
                        )

                        if (editingAlarm.autoDismiss.enabled) {
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                            ) {
                                Text("Timeout", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = "${editingAlarm.autoDismiss.timeoutMinutes} min",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
                                )
                            }
                            androidx.compose.material3.Slider(
                                value = editingAlarm.autoDismiss.timeoutMinutes.toFloat(),
                                onValueChange = { value ->
                                    editingAlarm = editingAlarm.copyWithModified(
                                        autoDismiss = editingAlarm.autoDismiss.copy(timeoutMinutes = value.roundToInt().coerceIn(1, 30))
                                    )
                                },
                                valueRange = 1f..30f,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChallengeSelector(
    currentChallenge: com.yourname.liquidalarm.data.ChallengeType,
    onChallengeChange: (com.yourname.liquidalarm.data.ChallengeType) -> Unit
) {
    val challenges = listOf(
        com.yourname.liquidalarm.data.ChallengeType.Math,
        com.yourname.liquidalarm.data.ChallengeType.QR,
        com.yourname.liquidalarm.data.ChallengeType.Shake,
        com.yourname.liquidalarm.data.ChallengeType.None
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        challenges.forEach { challenge ->
            val isSelected = currentChallenge == challenge
            androidx.compose.material3.Chip(
                onClick = { onChallengeChange(challenge) },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ChipDefaults.chipColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (challenge) {
                            com.yourname.liquidalarm.data.ChallengeType.Math -> "Math Problem"
                            com.yourname.liquidalarm.data.ChallengeType.QR -> "QR Code Scan"
                            com.yourname.liquidalarm.data.ChallengeType.Shake -> "Shake Phone"
                            com.yourname.liquidalarm.data.ChallengeType.None -> "Simple Dismiss"
                        },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                    )
                    if (isSelected) {
                        androidx.compose.material.icons.filled.Check(
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

private fun getRepeatSummary(rule: RepeatRule): String {
    return when (rule) {
        is RepeatRule.Once -> "Once"
        is RepeatRule.Daily -> "Daily"
        is RepeatRule.Weekdays -> "Weekdays"
        is RepeatRule.Weekends -> "Weekends"
        is RepeatRule.Weekly -> "Custom (${rule.days.size} days)"
        is RepeatRule.Monthly -> "Monthly (day ${rule.dayOfMonth})"
        is RepeatRule.Yearly -> "Yearly"
        is RepeatRule.Interval -> "Every ${rule.days} days"
    }
}

private fun getSoundName(sound: AlarmSound): String {
    return when (sound) {
        is AlarmSound.SystemDefault -> "Default"
        is AlarmSound.Silent -> "Silent"
        is AlarmSound.CustomFile -> sound.displayName
        is AlarmSound.SystemRingtone -> "System"
    }
}
