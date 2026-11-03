package com.yourname.liquidalarm.ui.screens

import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
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
fun TimerScreen(
    onNavigateBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    var hours by remember { mutableStateOf(0) }
    var minutes by remember { mutableStateOf(0) }
    var seconds by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var remainingSeconds by remember { mutableStateOf(0) }
    var totalSeconds by remember { mutableStateOf(0) }

    val progress = remember { mutableStateOf(0f) }
    val pulseScale = animateFloatAsState(
        targetValue = if (isRunning) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
    )

    // Timer logic
    androidx.compose.runtime.LaunchedEffect(isRunning, remainingSeconds) {
        if (isRunning && remainingSeconds > 0) {
            while (remainingSeconds > 0 && isRunning) {
                kotlinx.coroutines.delay(1000)
                remainingSeconds--
                progress.value = 1f - (remainingSeconds.toFloat() / totalSeconds.toFloat().coerceAtLeast(1f))
            }
            if (remainingSeconds <= 0 && isRunning) {
                isRunning = false
                // Play timer completion sound
            }
        }
    }

    val formattedTime = String.format("%02d:%02d:%02d",
        remainingSeconds / 3600,
        (remainingSeconds % 3600) / 60,
        remainingSeconds % 60
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
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 12f
                    val radius = (size.width - strokeWidth) / 2
                    val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)

                    // Background circle
                    drawCircle(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        radius = radius,
                        style = androidx.compose.ui.graphics.Stroke(width = strokeWidth)
                    )

                    // Progress arc
                    if (totalSeconds > 0) {
                        val sweepAngle = 360f * progress.value
                        drawArc(
                            color = MaterialTheme.colorScheme.primary,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            radius = radius,
                            style = androidx.compose.ui.graphics.Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        )
                    }
                }

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

            // Progress bar
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .padding(horizontal = 48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.outlineVariant, shape = RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .width(progress.value * 100)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(4.dp))
                    )
                }
            }

            // Time pickers (when not running)
            if (!isRunning) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
                ) {
                    TimePickerColumn(
                        label = "Hours",
                        value = hours,
                        maxValue = 23,
                        onValueChange = { hours = it }
                    )
                    TimePickerColumn(
                        label = "Minutes",
                        value = minutes,
                        maxValue = 59,
                        onValueChange = { minutes = it }
                    )
                    TimePickerColumn(
                        label = "Seconds",
                        value = seconds,
                        maxValue = 59,
                        onValueChange = { seconds = it }
                    )
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
                            isRunning = false
                            remainingSeconds = 0
                            progress.value = 0f
                        },
                        modifier = Modifier.fillMaxWidth().weight(1f).height(56.dp),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("Reset", style = MaterialTheme.typography.labelLarge)
                    }
                } else {
                    if (remainingSeconds > 0) {
                        Button(
                            onClick = {
                                remainingSeconds = 0
                                progress.value = 0f
                            },
                            modifier = Modifier.fillMaxWidth().weight(1f).height(56.dp),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text("Reset", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    Button(
                        onClick = {
                            val total = hours * 3600 + minutes * 60 + seconds
                            if (total > 0) {
                                totalSeconds = total
                                remainingSeconds = total
                                progress.value = 0f
                                isRunning = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().weight(1f).height(56.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("Start", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            // Quick timers
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                Text("Quick Timers", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    QuickTimerButton("1 min", 60)
                    QuickTimerButton("5 min", 300)
                    QuickTimerButton("10 min", 600)
                    QuickTimerButton("25 min", 1500) // Pomodoro
                }
            }
        }
    }

    @Composable
    fun TimePickerColumn(
        label: String,
        value: Int,
        maxValue: Int,
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
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp))
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
                    onClick = { onValueChange((value - 1).coerceIn(0, maxValue)) }
                ) {
                    androidx.compose.material.icons.filled.ExpandLess(
                        contentDescription = "Decrease",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                androidx.compose.material3.IconButton(
                    onClick = { onValueChange((value + 1).coerceIn(0, maxValue)) }
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
    fun QuickTimerButton(label: String, seconds: Int) {
        Button(
            onClick = {
                hours = seconds / 3600
                minutes = (seconds % 3600) / 60
                this.seconds = seconds % 60
                remainingSeconds = seconds
                totalSeconds = seconds
                progress.value = 0f
            },
            modifier = Modifier.fillMaxWidth().weight(1f).height(40.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}