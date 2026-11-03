package com.yourname.liquidalarm.ui.screens

import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
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
import androidx.compose.material.icons.filled.Close
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
import com.yourname.liquidalarm.alarm.AlarmForegroundService
import com.yourname.liquidalarm.data.AlarmEntity
import com.yourname.liquidalarm.ui.components.AlarmTimeDisplay
import com.yourname.liquidalarm.ui.components.ChallengeButtons
import com.yourname.liquidalarm.ui.theme.LiquidColors

@Composable
fun ActiveAlarmScreen(
    alarm: AlarmEntity,
    isPreAlarm: Boolean,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit,
    onMathChallenge: () -> Unit,
    onQRChallenge: () -> Unit,
    onShakeChallenge: () -> Unit,
    onChallengeCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    val pulseScale = animateFloatAsState(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, delayMillis = 0, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        )
    )

    val progress = remember { mutableStateOf(0f) }
    val fadeDuration = if (isPreAlarm) alarm.preAlarmConfig.volumeFade.durationSeconds else alarm.mainSoundVolumeFade.durationSeconds
    val targetVol = if (isPreAlarm) alarm.preAlarmConfig.targetVolumePercent / 100f else 1f

    // Simulate volume progress
    androidx.compose.runtime.LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        while (progress.value < 1f) {
            val elapsed = (System.currentTimeMillis() - startTime) / 1000f
            progress.value = (elapsed / fadeDuration).coerceIn(0f, 1f)
            if (progress.value >= 1f) break
            kotlinx.coroutines.delay(100)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isPreAlarm) colorScheme.surface.copy(alpha = 0.95f) else colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            // Time display
            AlarmTimeDisplay(
                time = alarm.time,
                isActive = true,
                showSeconds = true,
                is24Hour = true
            )

            // Label
            if (alarm.label.isNotBlank()) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                Text(
                    text = if (isPreAlarm) "Pre-Alarm: ${alarm.label}" else alarm.label,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = colorScheme.onSurfaceVariant
                    )
                )
            }

            // Pulse ring
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 32.dp))
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        scaleX = pulseScale.value
                        scaleY = pulseScale.value
                    }
            ) {
                // Outer pulse ring
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = colorScheme.primary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(100.dp)
                        )
                        .graphicsLayer {
                            scaleX = pulseScale.value
                            scaleY = pulseScale.value
                        }
                )

                // Inner progress ring
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    // Circular progress indicator
                    androidx.compose.foundation.Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val strokeWidth = 8f
                        val radius = (size.width - strokeWidth) / 2
                        val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)

                        // Background circle
                        drawCircle(
                            color = colorScheme.outlineVariant,
                            radius = radius,
                            style = androidx.compose.ui.graphics.Stroke(width = strokeWidth)
                        )

                        // Progress arc
                        val sweepAngle = 360f * progress.value
                        drawArc(
                            color = colorScheme.primary,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            radius = radius,
                            style = androidx.compose.ui.graphics.Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        )
                    }

                    // Center content
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                    ) {
                        Text(
                            text = if (isPreAlarm) "Gentle Wake" else "Wake Up!",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 4.dp))
                        Text(
                            text = "${(progress.value * 100).toInt()}%",
                            style = MaterialTheme.typography.displaySmall.copy(
                                color = colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Volume progress bar
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 32.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .padding(horizontal = 48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorScheme.outlineVariant, shape = RoundedCornerShape(3.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .width(progress.value * 100)
                            .fillMaxHeight()
                            .background(colorScheme.primary, shape = RoundedCornerShape(3.dp))
                    )
                }
            }

            // Challenge buttons
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 48.dp))
            ChallengeButtons(
                challengeType = alarm.challenge,
                onDismiss = onDismiss,
                onSnooze = onSnooze,
                onMathChallenge = onMathChallenge,
                onQRChallenge = onQRChallenge,
                onShakeChallenge = onShakeChallenge,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}