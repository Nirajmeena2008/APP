package com.yourname.liquidalarm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    elevation: Float = 0f,
    content: @Composable () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val isApi34Plus = android.os.Build.VERSION.SDK_INT >= 34

    Box(
        modifier = modifier
            .background(
                color = colorScheme.surfaceVariant.copy(alpha = 0.85f),
                shape = MaterialTheme.shapes.extraLarge
            )
            .graphicsLayer {
                if (isApi34Plus) {
                    this.renderEffect = RenderEffect.createBlurEffect(20f, 20f, android.graphics.RenderEffect.TILE_MODE_CLAMP)
                }
            }
            .padding(16.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.extraLarge,
            color = Color.Transparent,
            borderWidth = 1.dp,
            borderColor = colorScheme.outlineVariant,
            content = content
        )
    }
}

@Composable
fun GlassBottomSheet(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val isApi34Plus = android.os.Build.VERSION.SDK_INT >= 34

    Box(
        modifier = modifier
            .background(
                color = colorScheme.surfaceVariant.copy(alpha = 0.9f),
                shape = MaterialTheme.shapes.large
            )
            .graphicsLayer {
                if (isApi34Plus) {
                    this.renderEffect = RenderEffect.createBlurEffect(30f, 30f, android.graphics.RenderEffect.TILE_MODE_CLAMP)
                }
            }
            .padding(16.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.large,
            color = Color.Transparent,
            borderWidth = 1.dp,
            borderColor = colorScheme.outlineVariant,
            content = content
        )
    }
}

@Composable
fun GlassNavBar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val isApi34Plus = android.os.Build.VERSION.SDK_INT >= 34

    Box(
        modifier = modifier
            .background(
                color = colorScheme.surfaceVariant.copy(alpha = 0.85f),
                shape = MaterialTheme.shapes.extraLarge
            )
            .graphicsLayer {
                if (isApi34Plus) {
                    this.renderEffect = RenderEffect.createBlurEffect(20f, 20f, android.graphics.RenderEffect.TILE_MODE_CLAMP)
                }
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.extraLarge,
            color = Color.Transparent,
            borderWidth = 1.dp,
            borderColor = colorScheme.outlineVariant,
            content = content
        )
    }
}
