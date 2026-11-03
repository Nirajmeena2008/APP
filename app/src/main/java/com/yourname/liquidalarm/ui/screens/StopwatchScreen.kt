package com.yourname.liquidalarm.ui.screens

import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Replay
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StopwatchScreen(
    onNavigateBack: () -> Unit
) {
    var isRunning by remember { mutableStateOf(false) }
    var elapsedMs by remember { mutableStateOf(0L) }
    var laps by remember { mutableStateOf(emptyList<Long>()) }

    val pulseScale = animateFloatAsState(
        targetValue = if (isRunning) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
    )

    // Stopwatch logic
    androidx.compose.runtime.LaunchedEffect(isRunning) {
        if (isRunning) {
            while (isRunning) {
                kotlinx.coroutines.delay(10) // 10ms precision
                elapsedMs += 10
            }
        }
    }

    val formattedTime = String.format("%02d:%02d.%02d",
        (elapsedMs / 1000 / 60) % 60,
        (elapsedMs / 1000) % 60,
        (elapsedMs / 10) % 100
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            // Time display
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .graphicsLayer {
                        scaleX = pulseScale.value
                        scaleY = pulseScale.value
                    }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                ) {
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            // Laps list
            if (laps.isNotEmpty()) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 24.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp),
                        reverseLayout = true
                    ) {
                        items(laps.reversed().withIndex()) { index, lapTime ->
                            val lapNumber = laps.size - index
                            val prevTime = if (index < laps.size - 1) laps[laps.size - 2 - index] else 0L
                            val lapDiff = lapTime - prevTime

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Lap $lapNumber",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = formatLapTime(lapDiff),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Controls
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
            ) {
                if (isRunning) {
                    Button(
                        onClick = { isRunning = false },
                        modifier = Modifier.fillMaxWidth().weight(1f).height(56.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("Pause", style = MaterialTheme.typography.labelLarge)
                    }
                    Button(
                        onClick = {
                            val currentLap = elapsedMs
                            laps = laps + currentLap
                        },
                        modifier = Modifier.fillMaxWidth().weight(1f).height(56.dp),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("Lap", style = MaterialTheme.typography.labelLarge)
                    }
                } else {
                    if (elapsedMs > 0) {
                        Button(
                            onClick = {
                                elapsedMs = 0
                                laps = emptyList()
                            },
                            modifier = Modifier.fillMaxWidth().weight(1f).height(56.dp),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text("Reset", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    Button(
                        onClick = { isRunning = true },
                        modifier = Modifier.fillMaxWidth().weight(1f).height(56.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(if (elapsedMs == 0) "Start" else "Resume", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }

    private fun formatLapTime(ms: Long): String {
        return String.format("%02d:%02d.%02d",
            (ms / 1000 / 60) % 60,
            (ms / 1000) % 60,
            (ms / 10) % 100
        )
    }
}
