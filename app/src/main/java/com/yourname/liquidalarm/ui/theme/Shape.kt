package com.yourname.liquidalarm.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val LiquidShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp),  // Glass cards, nav bar
)

object LiquidShapeTokens {
    val CornerExtraSmall = 8.dp
    val CornerSmall = 12.dp
    val CornerMedium = 16.dp
    val CornerLarge = 24.dp
    val CornerExtraLarge = 28.dp
    val CornerFull = RoundedCornerShape(9999.dp)
}
