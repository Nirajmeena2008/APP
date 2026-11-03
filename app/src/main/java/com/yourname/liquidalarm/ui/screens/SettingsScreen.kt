package com.yourname.liquidalarm.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import com.yourname.liquidalarm.data.PreferencesManager
import com.yourname.liquidalarm.util.PermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    prefs: PreferencesManager,
    onNavigateBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    var theme by remember { mutableStateOf(0) }
    var timeFormat24h by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var showSeconds by remember { mutableStateOf(false) }
    var keepScreenOn by remember { mutableStateOf(true) }

    // Load preferences
    androidx.compose.runtime.LaunchedEffect(Unit) {
        prefs.themeSingle().subscribe { theme = it }
        prefs.timeFormat24hSingle().subscribe { timeFormat24h = it }
        prefs.vibrationEnabled.subscribe { vibrationEnabled = it }
        prefs.showSeconds.subscribe { showSeconds = it }
        prefs.keepScreenOn.subscribe { keepScreenOn = it }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text(text = "Settings") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                        androidx.compose.material.Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                // Appearance Section
                SettingsSection(title = "Appearance") {
                    SettingRow(
                        title = "Theme",
                        subtitle = "Choose color scheme",
                        trailing = {
                            androidx.compose.material3.DropdownMenu(
                                expanded = false,
                                onDismissRequest = {},
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Simplified - just show options
                                Column {
                                    listOf("System", "Dark (OLED)", "Light").forEachIndexed { index, name ->
                                        androidx.compose.material3.DropdownMenuItem(
                                            onClick = { theme = index },
                                            text = { Text(text = name) }
                                        )
                                    }
                                }
                            }
                        }
                    ) {
                        Text(text = when (theme) { 0 -> "System"; 1 -> "Dark (OLED)"; else -> "Light" })
                    }

                    SettingRow(
                        title = "Time Format",
                        subtitle = "24-hour or 12-hour clock",
                        trailing = { Switch(checked = timeFormat24h, onCheckedChange = { timeFormat24h = it; prefs.setTimeFormat24h(it) }) }
                    )

                    SettingRow(
                        title = "Show Seconds",
                        subtitle = "Display seconds in alarm time",
                        trailing = { Switch(checked = showSeconds, onCheckedChange = { showSeconds = it; prefs.setShowSeconds(it) }) }
                    )

                    SettingRow(
                        title = "Keep Screen On",
                        subtitle = "Prevent screen timeout during alarms",
                        trailing = { Switch(checked = keepScreenOn, onCheckedChange = { keepScreenOn = it; prefs.setKeepScreenOn(it) }) }
                    )
                }

                // Alarm Defaults Section
                SettingsSection(title = "Alarm Defaults") {
                    SettingRow(
                        title = "Default Snooze",
                        subtitle = "Minutes before snooze",
                        trailing = {
                            // Number picker would go here
                            Text("10 min")
                        }
                    )

                    SettingRow(
                        title = "Default Volume Fade",
                        subtitle = "Seconds for volume ramp",
                        trailing = { Text("60s") }
                    )

                    SettingRow(
                        title = "Default Challenge",
                        subtitle = "Wake challenge type",
                        trailing = { Text("Math") }
                    )

                    SettingRow(
                        title = "Default Pre-Alarm",
                        subtitle = "Minutes before main alarm",
                        trailing = { Text("10 min") }
                    )

                    SettingRow(
                        title = "Enable Pre-Alarm by Default",
                        subtitle = "Gentle wake before main alarm",
                        trailing = { Switch(checked = false, onCheckedChange = { }) }
                    )
                }

                // Sound & Vibration Section
                SettingsSection(title = "Sound & Vibration") {
                    SettingRow(
                        title = "Vibration",
                        subtitle = "Enable vibration for alarms",
                        trailing = { Switch(checked = vibrationEnabled, onCheckedChange = { vibrationEnabled = it; prefs.setVibrationEnabled(it) }) }
                    )

                    SettingRow(
                        title = "Custom Sounds Folder",
                        subtitle = "Manage imported alarm sounds",
                        trailing = { /* Open folder */ }
                    )
                }

                // Data Section
                SettingsSection(title = "Data") {
                    SettingRow(
                        title = "Backup Alarms",
                        subtitle = "Export to JSON file",
                        icon = Icons.Default.CloudDownload,
                        onClick = { /* Export backup */ }
                    )

                    SettingRow(
                        title = "Restore Alarms",
                        subtitle = "Import from JSON file",
                        icon = Icons.Default.CloudUpload,
                        onClick = { /* Import backup */ }
                    )

                    SettingRow(
                        title = "Clear All Alarms",
                        subtitle = "Delete all alarms permanently",
                        icon = Icons.Default.Delete,
                        textColor = MaterialTheme.colorScheme.error,
                        onClick = { /* Confirm and clear */ }
                    )
                }

                // Permissions Section
                SettingsSection(title = "Permissions") {
                    SettingRow(
                        title = "Exact Alarm Permission",
                        subtitle = "Required for reliable alarms",
                        icon = Icons.Default.AccessTime,
                        trailing = {
                            androidx.compose.material3.Button(
                                onClick = { PermissionUtils.requestExactAlarmPermission(context) },
                                modifier = Modifier.padding(end = 8.dp)
                            ) { Text("Grant") }
                        }
                    )

                    SettingRow(
                        title = "Battery Optimization",
                        subtitle = "Disable for reliable alarms",
                        icon = Icons.Default.BatteryAlert,
                        trailing = {
                            androidx.compose.material3.Button(
                                onClick = { PermissionUtils.openBatteryOptimizationSettings(context) },
                                modifier = Modifier.padding(end = 8.dp)
                            ) { Text("Open Settings") }
                        }
                    )

                    SettingRow(
                        title = "Display Over Apps",
                        subtitle = "Required for lock screen alarms",
                        trailing = {
                            androidx.compose.material3.Button(
                                onClick = { PermissionUtils.openDisplayOverAppsSettings(context) },
                                modifier = Modifier.padding(end = 8.dp)
                            ) { Text("Open Settings") }
                        }
                    )
                }

                // About Section
                SettingsSection(title = "About") {
                    SettingRow(
                        title = "Version",
                        subtitle = "1.0.0",
                        trailing = { Text("1.0.0") }
                    )

                    SettingRow(
                        title = "Open Source Licenses",
                        subtitle = "View third-party licenses",
                        icon = Icons.Default.Info,
                        onClick = { /* Show licenses */ }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, top = 16.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = 0.dp
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingRow(
    title: String,
    subtitle: String? = null,
    icon: androidx.compose.material.icons.filled.Icon? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp, horizontal = 16.dp)
        .background(Color.Transparent)
        .pointerInput(Unit) {
            onClick?.let { androidx.compose.foundation.gestures.detectTapGestures(onTap = it) }
        }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    androidx.compose.material.Icon(it, contentDescription = null, tint = textColor.copy(alpha = 0.7f), modifier = Modifier.size(24.dp).padding(end = 12.dp))
                }
                Column {
                    Text(text = title, color = textColor, style = MaterialTheme.typography.bodyLarge)
                    subtitle?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            trailing?.invoke()
        }
    }
}