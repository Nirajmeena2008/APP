package com.yourname.liquidalarm.ui.components

import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Shake
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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

@Composable
fun ChallengeButtons(
    challengeType: com.yourname.liquidalarm.data.ChallengeType,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit,
    onMathChallenge: () -> Unit,
    onQRChallenge: () -> Unit,
    onShakeChallenge: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            // Snooze button
            Button(
                onClick = onSnooze,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = colorScheme.surfaceVariant.copy(alpha = 0.9f),
                    contentColor = colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Snooze 10m",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            // Dismiss button (requires challenge)
            ChallengeDismissButton(
                challengeType = challengeType,
                onClick = {
                    when (challengeType) {
                        com.yourname.liquidalarm.data.ChallengeType.Math -> onMathChallenge()
                        com.yourname.liquidalarm.data.ChallengeType.QR -> onQRChallenge()
                        com.yourname.liquidalarm.data.ChallengeType.Shake -> onShakeChallenge()
                        com.yourname.liquidalarm.data.ChallengeType.None -> onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }

        // Challenge indicator
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
        ChallengeIndicator(challengeType = challengeType)
    }
}

@Composable
fun ChallengeDismissButton(
    challengeType: com.yourname.liquidalarm.data.ChallengeType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val pressScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = pressScale; scaleY = pressScale }
            .pointerInput(Unit) {
                androidx.compose.foundation.gestures.detectTapGestures(
                    onPress = { pressScale = 0.98f },
                    onRelease = { pressScale = 1f; onClick() },
                    onCancel = { pressScale = 1f }
                )
            }
    ) {
        Button(
            onClick = {},
            modifier = Modifier.fillMaxSize(),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = colorScheme.errorContainer,
                contentColor = colorScheme.onErrorContainer
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (challengeType) {
                    com.yourname.liquidalarm.data.ChallengeType.Math -> {
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = colorScheme.onErrorContainer)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 8.dp))
                    }
                    com.yourname.liquidalarm.data.ChallengeType.QR -> {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = colorScheme.onErrorContainer)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 8.dp))
                    }
                    com.yourname.liquidalarm.data.ChallengeType.Shake -> {
                        Icon(Icons.Default.Shake, contentDescription = null, tint = colorScheme.onErrorContainer)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 8.dp))
                    }
                    com.yourname.liquidalarm.data.ChallengeType.None -> {
                        // No icon for simple dismiss
                    }
                }
                Text(
                    text = when (challengeType) {
                        com.yourname.liquidalarm.data.ChallengeType.Math -> "Solve Math"
                        com.yourname.liquidalarm.data.ChallengeType.QR -> "Scan QR Code"
                        com.yourname.liquidalarm.data.ChallengeType.Shake -> "Shake Phone"
                        com.yourname.liquidalarm.data.ChallengeType.None -> "Dismiss"
                    },
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun ChallengeIndicator(challengeType: com.yourname.liquidalarm.data.ChallengeType) {
    val colorScheme = MaterialTheme.colorScheme

    when (challengeType) {
        com.yourname.liquidalarm.data.ChallengeType.Math -> {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Icon(Icons.Default.Calculate, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(20.dp))
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(
                    text = "Solve the math problem to dismiss",
                    style = MaterialTheme.typography.bodySmall.copy(color = colorScheme.onSurfaceVariant)
                )
            }
        }
        com.yourname.liquidalarm.data.ChallengeType.QR -> {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(20.dp))
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(
                    text = "Scan the QR code to dismiss",
                    style = MaterialTheme.typography.bodySmall.copy(color = colorScheme.onSurfaceVariant)
                )
            }
        }
        com.yourname.liquidalarm.data.ChallengeType.Shake -> {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Icon(Icons.Default.Shake, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(20.dp))
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(
                    text = "Shake the phone 10 times to dismiss",
                    style = MaterialTheme.typography.bodySmall.copy(color = colorScheme.onSurfaceVariant)
                )
            }
        }
        com.yourname.liquidalarm.data.ChallengeType.None -> {
            // No indicator for simple dismiss
        }
    }
}