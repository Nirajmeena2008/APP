package com.yourname.liquidalarm.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.FloatingActionButton
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
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun WorldClockScreen(
    onNavigateBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    var timeZones by remember { mutableStateOf(listOf(
        TimeZoneInfo("Local", ZoneId.systemDefault()),
        TimeZoneInfo("UTC", ZoneId.of("UTC")),
        TimeZoneInfo("New York", ZoneId.of("America/New_York")),
        TimeZoneInfo("London", ZoneId.of("Europe/London")),
        TimeZoneInfo("Tokyo", ZoneId.of("Asia/Tokyo")),
        TimeZoneInfo("Sydney", ZoneId.of("Australia/Sydney")),
        TimeZoneInfo("Los Angeles", ZoneId.of("America/Los_Angeles"))
    )) }

    // Update time every second
    val trigger by remember { mutableStateOf(0) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            trigger++
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(colorScheme.surfaceContainerHighest)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "World Clock",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    FloatingActionButton(
                        onClick = { /* Add timezone */ },
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    ) {
                        androidx.compose.material.Icon(Icons.Default.Add, contentDescription = "Add timezone")
                    }
                }
            }

            // Clock list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
            ) {
                items(timeZones) { tz ->
                    WorldClockItem(timeZone = tz, trigger = trigger)
                }
            }
        }
    }
}

@Composable
fun WorldClockItem(
    timeZone: TimeZoneInfo,
    trigger: Int
) {
    val colorScheme = MaterialTheme.colorScheme
    val now = ZonedDateTime.now(timeZone.zoneId)
    val timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss")
    val dateFormat = DateTimeFormatter.ofPattern("EEEE, MMM d")
    val offsetFormat = DateTimeFormatter.ofPattern("XXX")

    val currentTime = now.format(timeFormat)
    val currentDate = now.format(dateFormat)
    val offset = now.format(offsetFormat)
    val isLocal = timeZone.name == "Local"

    val color = if (isLocal) colorScheme.primary else colorScheme.onSurface

    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLocal) {
                            androidx.compose.material.Icon(
                                Icons.Default.Language,
                                contentDescription = null,
                                tint = colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 8.dp))
                        }
                        Text(
                            text = timeZone.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = color,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        text = currentDate,
                        style = MaterialTheme.typography.bodyMedium.copy(color = colorScheme.onSurfaceVariant)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = currentTime,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                    )
                    Text(
                        text = "UTC$offset",
                        style = MaterialTheme.typography.bodySmall.copy(color = colorScheme.onSurfaceVariant)
                    )
                }
            }
        }
    }
}

data class TimeZoneInfo(
    val name: String,
    val zoneId: ZoneId
)
