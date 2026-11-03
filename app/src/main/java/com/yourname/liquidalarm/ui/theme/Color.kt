package com.yourname.liquidalarm.ui.theme

import androidx.compose.ui.graphics.Color

// Dark Theme (OLED Optimized)
val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = Color(0xFFD97706),           // Amber
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF1A1A2E),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF6366F1),         // Indigo
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF1E1E2E),
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = Color(0xFF22C55E),          // Green
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFF166534),
    onTertiaryContainer = Color(0xFFFFFFFF),
    error = Color(0xFFDC2626),             // Red
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF991B1B),
    onErrorContainer = Color(0xFFFFFFFF),
    background = Color(0xFF000000),        // True black
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF121212),           // Elevated surface
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xCC1A1A2E),    // Glass (80%)
    onSurfaceVariant = Color(0xFFE0E0E0),
    outline = Color(0x26FFFFFF),           // 15% white
    outlineVariant = Color(0x14FFFFFF),    // 8% white
    scrim = Color(0xCC000000),
    inverseSurface = Color(0xFFE0E0E0),
    inverseOnSurface = Color(0xFF0F172A),
    inversePrimary = Color(0xFFB45309)
)

// Light Theme
val LightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = Color(0xFFB45309),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFF3E0),
    onPrimaryContainer = Color(0xFF431800),
    secondary = Color(0xFF4F46E5),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEEF2FF),
    onSecondaryContainer = Color(0xFF1E1B4B),
    tertiary = Color(0xFF16A34A),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFDCFCE7),
    onTertiaryContainer = Color(0xFF14532D),
    error = Color(0xFFB91C1C),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEF2F2),
    onErrorContainer = Color(0xFF991B1B),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xE6FFFFFF),    // Glass (90%)
    onSurfaceVariant = Color(0xFF334155),
    outline = Color(0x26000000),
    outlineVariant = Color(0x14000000),
    scrim = Color(0xCC000000),
    inverseSurface = Color(0xFF0F172A),
    inverseOnSurface = Color(0xFFE0E0E0),
    inversePrimary = Color(0xFFD97706)
)