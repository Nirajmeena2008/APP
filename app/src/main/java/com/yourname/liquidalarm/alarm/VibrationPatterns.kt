package com.yourname.liquidalarm.alarm

import android.os.VibrationEffect
import android.os.Vibrator
import com.yourname.liquidalarm.data.VibrationPattern

object VibrationPatterns {
    fun getPattern(pattern: VibrationPattern): VibrationEffect {
        return when (pattern) {
            is VibrationPattern.Default -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
            is VibrationPattern.None -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
            is VibrationPattern.System -> VibrationEffect.createPredefined(getSystemPattern(pattern.patternName))
            is VibrationPattern.Custom -> {
                if (pattern.pattern.size >= 2) {
                    VibrationEffect.createWaveform(pattern.pattern.toLongArray(), -1)
                } else {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                }
            }
        }
    }

    private fun getSystemPattern(name: String): Int {
        return when (name) {
            "double_click" -> VibrationEffect.EFFECT_DOUBLE_CLICK
            "tick" -> VibrationEffect.EFFECT_TICK
            "click" -> VibrationEffect.EFFECT_CLICK
            "heavy_click" -> VibrationEffect.EFFECT_HEAVY_CLICK
            else -> VibrationEffect.EFFECT_DOUBLE_CLICK
        }
    }

    val PREDEFINED_PATTERNS = mapOf(
        "Default" to VibrationPattern.Default,
        "None" to VibrationPattern.None,
        "Short" to VibrationPattern.Custom(listOf(0L, 100L, 50L, 100L)),
        "Long" to VibrationPattern.Custom(listOf(0L, 500L, 100L, 500L)),
        "Double" to VibrationPattern.Custom(listOf(0L, 200L, 100L, 200L, 100L, 200L)),
        "Triple" to VibrationPattern.Custom(listOf(0L, 150L, 100L, 150L, 100L, 150L, 100L, 150L)),
        "SOS" to VibrationPattern.Custom(listOf(
            0L, 200L, 200L, 200L, 200L, 200L, 200L, 200L,
            500L, 500L, 500L, 500L, 500L,
            200L, 200L, 200L, 200L, 200L, 200L
        )),
        "Heartbeat" to VibrationPattern.Custom(listOf(0L, 100L, 100L, 100L, 300L, 100L, 100L, 100L, 700L)),
        "Ramp Up" to VibrationPattern.Custom(listOf(
            0L, 50L, 100L, 50L, 100L, 50L, 150L, 50L, 200L, 50L, 250L, 50L, 300L, 50L, 350L, 50L, 400L
        )),
        "Ramp Down" to VibrationPattern.Custom(listOf(
            0L, 400L, 50L, 350L, 50L, 300L, 50L, 250L, 50L, 200L, 50L, 150L, 50L, 100L, 50L, 50L
        ))
    )
}
