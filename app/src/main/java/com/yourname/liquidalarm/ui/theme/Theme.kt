package com.yourname.liquidalarm.ui.theme

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.Window
import android.view.WindowInsetsController
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

@Composable
fun LiquidAlarmTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    SideEffect {
        val activity = (context as? Activity) ?: return@SideEffect
        val window = activity.window
        val isDark = darkTheme

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val insetsController = window.insetsController
            if (insetsController != null) {
                if (isDark) {
                    insetsController.systemBarsAppearance = 0
                } else {
                    insetsController.systemBarsAppearance = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                }
            }
            window.statusBarColor = Color.TRANSPARENT.toArgb()
            window.navigationBarColor = Color.TRANSPARENT.toArgb()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT.toArgb()
            window.navigationBarColor = Color.TRANSPARENT.toArgb()
            if (!isDark) {
                window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LiquidTypography,
        shapes = LiquidShapes,
        content = content
    )
}

@Composable
fun LiquidAlarmTheme(
    content: @Composable () -> Unit
) {
    LiquidAlarmTheme(darkTheme = true, content = content)
}
