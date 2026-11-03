package com.yourname.liquidalarm.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourname.liquidalarm.data.AlarmEntity
import com.yourname.liquidalarm.data.AlarmRepository
import com.yourname.liquidalarm.data.PreferencesManager
import com.yourname.liquidalarm.ui.components.AlarmListItem
import com.yourname.liquidalarm.ui.components.GlassCard
import com.yourname.liquidalarm.ui.components.NextAlarmDisplay
import com.yourname.liquidalarm.ui.components.BottomNavBar
import com.yourname.liquidalarm.util.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AlarmListScreen(
    repository: AlarmRepository,
    prefs: PreferencesManager,
    onCreateAlarm: () -> Unit,
    onEditAlarm: (com.yourname.liquidalarm.data.AlarmEntity) -> Unit,
    onNavigateToTimer: () -> Unit,
    onNavigateToStopwatch: () -> Unit,
    onNavigateToWorldClock: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val alarms by remember {
        mutableStateOf(emptyList<com.yourname.liquidalarm.data.AlarmEntity>())
    }
    val showEmpty by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var selectedTab by remember { mutableStateOf(0) }
    var is24Hour by remember { mutableStateOf(true) }

    // Load alarms
    androidx.compose.runtime.LaunchedEffect(Unit) {
        repository.alarmsFlow.subscribeOn(Dispatchers.IO).collect { newAlarms ->
            alarms = newAlarms
            showEmpty = newAlarms.isEmpty()
        }
    }

    // Load preferences
    androidx.compose.runtime.LaunchedEffect(Unit) {
        prefs.timeFormat24h.subscribeOn(Dispatchers.IO).collect { format ->
            is24Hour = format
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            // Next alarm display
            val nextAlarm = alarms
                .filter { it.isEnabled }
                .minByOrNull { it.nextFireTime?.toEpochMilliseconds() ?: Long.MAX_VALUE }

            NextAlarmDisplay(
                alarm = nextAlarm,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                is24Hour = is24Hour
            )

            // Alarm list
            if (alarms.isNotEmpty()) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, bottom = 100.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
                    state = scrollState
                ) {
                    items(alarms) { alarm ->
                        AlarmListItem(
                            alarm = alarm,
                            onToggle = { enabled ->
                                repository.toggleAlarm(alarm.id, enabled)
                            },
                            onClick = { onEditAlarm(alarm) },
                            onDelete = {
                                repository.deleteAlarm(alarm.id)
                            },
                            onDrag = { /* Handle drag */ }
                        )
                    }
                }
            } else {
                // Empty state
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.fillMaxSize())
            }
        }

        // Bottom navigation
        BottomNavBar(
            selectedIndex = selectedTab,
            onItemClick = { index ->
                selectedTab = index
                when (index) {
                    0 -> { /* Already on alarms */ }
                    1 -> onNavigateToTimer()
                    2 -> onNavigateToStopwatch()
                    3 -> onNavigateToWorldClock()
                }
            }
        )

        // FAB
        FloatingActionButton(
            onClick = onCreateAlarm,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            androidx.compose.material.Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Add,
                contentDescription = "Add alarm",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
