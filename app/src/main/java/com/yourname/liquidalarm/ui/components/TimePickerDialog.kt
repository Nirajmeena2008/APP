package com.yourname.liquidalarm.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import kotlinx.datetime.LocalTime

@Composable
fun TimePickerDialog(
    initialTime: LocalTime,
    is24Hour: Boolean,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    var hour by remember { mutableStateOf(initialTime.hour) }
    var minute by remember { mutableStateOf(initialTime.minute) }

    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Set Alarm Time",
            style = MaterialTheme.typography.headlineMedium
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
        ) {
            // Hour picker
            TimePickerColumn(
                label = "Hour",
                value = hour,
                maxValue = if (is24Hour) 23 else 12,
                minValue = if (is24Hour) 0 else 1,
                onValueChange = { hour = it }
            )

            // Separator
            Text(
                text = ":",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            // Minute picker
            TimePickerColumn(
                label = "Minute",
                value = minute,
                maxValue = 59,
                minValue = 0,
                onValueChange = { minute = it }
            )

            // AM/PM for 12-hour
            if (!is24Hour) {
                AmPmPicker(
                    isAm = hour < 12,
                    onToggle = { hour = if (hour < 12) hour + 12 else hour - 12 }
                )
            }
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    val validatedHour = hour.coerceIn(
                        if (is24Hour) 0 else 1,
                        if (is24Hour) 23 else 12
                    )
                    onConfirm(LocalTime(validatedHour, minute.coerceIn(0, 59)))
                },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Set")
            }
        }
    }
}

@Composable
fun TimePickerColumn(
    label: String,
    value: Int,
    maxValue: Int,
    minValue: Int,
    onValueChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Text(
                    text = String.format("%02d", value),
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
        ) {
            androidx.compose.material3.IconButton(
                onClick = { onValueChange((value - 1).coerceIn(minValue, maxValue)) }
            ) {
                androidx.compose.material.icons.filled.ExpandLess(
                    contentDescription = "Decrease",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            androidx.compose.material3.IconButton(
                onClick = { onValueChange((value + 1).coerceIn(minValue, maxValue)) }
            ) {
                androidx.compose.material.icons.filled.ExpandMore(
                    contentDescription = "Increase",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun AmPmPicker(
    isAm: Boolean,
    onToggle: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "AM/PM", style = MaterialTheme.typography.labelMedium)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Text(
                    text = if (isAm) "AM" else "PM",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                )
            }
        }

        androidx.compose.material3.IconButton(onClick = onToggle) {
            androidx.compose.material.icons.filled.SwapHoriz(
                contentDescription = "Toggle AM/PM",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}