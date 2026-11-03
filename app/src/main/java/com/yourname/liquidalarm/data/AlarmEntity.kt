package com.yourname.liquidalarm.data

import kotlinx.datetime.LocalTime
import kotlinx.datetime.MonthDay
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AlarmEntity(
    val id: String = UUID.randomUUID().toString(),
    val time: LocalTime,
    val label: String = "",
    val isEnabled: Boolean = true,
    val repeatRule: RepeatRule = RepeatRule.Daily,
    val mainSound: AlarmSound = AlarmSound.SystemDefault,
    val mainSoundVolumeFade: VolumeFade = VolumeFade(true, 60),
    val preAlarmConfig: PreAlarmConfig = PreAlarmConfig(false, 10, AlarmSound.SystemDefault, VolumeFade(true, 120), 50),
    val vibration: VibrationPattern = VibrationPattern.Default,
    val snooze: SnoozeConfig = SnoozeConfig(10, 3, false),
    val challenge: ChallengeType = ChallengeType.Math,
    val autoDismiss: AutoDismissConfig = AutoDismissConfig(false, 10),
    val createdAt: Instant = Instant.now(),
    val lastModified: Instant = Instant.now(),
    val nextFireTime: Instant? = null
) {
    fun copyWithModified(
        time: LocalTime? = null,
        label: String? = null,
        isEnabled: Boolean? = null,
        repeatRule: RepeatRule? = null,
        mainSound: AlarmSound? = null,
        mainSoundVolumeFade: VolumeFade? = null,
        preAlarmConfig: PreAlarmConfig? = null,
        vibration: VibrationPattern? = null,
        snooze: SnoozeConfig? = null,
        challenge: ChallengeType? = null,
        autoDismiss: AutoDismissConfig? = null
    ): AlarmEntity = copy(
        time = time ?: this.time,
        label = label ?: this.label,
        isEnabled = isEnabled ?: this.isEnabled,
        repeatRule = repeatRule ?: this.repeatRule,
        mainSound = mainSound ?: this.mainSound,
        mainSoundVolumeFade = mainSoundVolumeFade ?: this.mainSoundVolumeFade,
        preAlarmConfig = preAlarmConfig ?: this.preAlarmConfig,
        vibration = vibration ?: this.vibration,
        snooze = snooze ?: this.snooze,
        challenge = challenge ?: this.challenge,
        autoDismiss = autoDismiss ?: this.autoDismiss,
        lastModified = Instant.now()
    )

    fun computeNextFireTime(now: Instant = Instant.now(), zone: TimeZone = TimeZone.currentSystemDefault()): Instant? {
        if (!isEnabled) return null
        val zdt = now.toInstant().toLocalDateTime(zone)
        val targetTime = this.time
        var candidate = zdt.with(targetTime).toInstant(zone)

        if (candidate <= now) {
            candidate = repeatRule.addPeriod(candidate, 1, zone)
        }

        return when (repeatRule) {
            is RepeatRule.Once -> if (candidate > now) candidate else null
            is RepeatRule.Daily -> candidate
            is RepeatRule.Weekdays -> {
                while (candidate.dayOfWeek == DayOfWeek.SATURDAY || candidate.dayOfWeek == DayOfWeek.SUNDAY) {
                    candidate = candidate.plusDays(1)
                }
                candidate
            }
            is RepeatRule.Weekends -> {
                while (candidate.dayOfWeek != DayOfWeek.SATURDAY && candidate.dayOfWeek != DayOfWeek.SUNDAY) {
                    candidate = candidate.plusDays(1)
                }
                candidate
            }
            is RepeatRule.Weekly -> {
                val days = (repeatRule as RepeatRule.Weekly).days.toSortedSet()
                var next = candidate
                while (!days.contains(next.dayOfWeek)) {
                    next = next.plusDays(1)
                }
                if (next <= now) {
                    next = next.plusDays(7 - days.maxOrNull()?.ordinal?.minus(days.minOrNull()?.ordinal ?: 0) ?: 7)
                }
                next
            }
            is RepeatRule.Monthly -> {
                val day = (repeatRule as RepeatRule.Monthly).dayOfMonth
                var next = candidate
                while (next.dayOfMonth != day) {
                    next = next.plusDays(1)
                }
                if (next <= now) {
                    next = next.plusMonths(1)
                    while (next.dayOfMonth != day) {
                        next = next.plusDays(1)
                    }
                }
                next
            }
            is RepeatRule.Yearly -> {
                val md = (repeatRule as RepeatRule.Yearly).monthDay
                var next = candidate
                while (MonthDay(next.month, next.dayOfMonth) != md) {
                    next = next.plusDays(1)
                }
                if (next <= now) {
                    next = next.plusYears(1)
                    while (MonthDay(next.month, next.dayOfMonth) != md) {
                        next = next.plusDays(1)
                    }
                }
                next
            }
            is RepeatRule.Interval -> {
                val interval = (repeatRule as RepeatRule.Interval).days
                var next = candidate
                while (next <= now) {
                    next = next.plusDays(interval)
                }
                next
            }
        }
    }

    fun computePreAlarmFireTime(mainFireTime: Instant): Instant? {
        return if (preAlarmConfig.enabled) {
            mainFireTime.minusSeconds(preAlarmConfig.minutesBefore * 60L)
        } else null
    }
}

@Serializable
sealed interface RepeatRule {
    data object Once : RepeatRule
    data object Daily : RepeatRule
    data object Weekdays : RepeatRule
    data object Weekends : RepeatRule
    @Serializable
    data class Weekly(val days: Set<DayOfWeek>) : RepeatRule
    @Serializable
    data class Monthly(val dayOfMonth: Int) : RepeatRule
    @Serializable
    data class Yearly(val monthDay: MonthDay) : RepeatRule
    @Serializable
    data class Interval(val days: Int) : RepeatRule

    fun addPeriod(instant: Instant, periods: Int, zone: TimeZone): Instant {
        return when (this) {
            is Once -> instant
            is Daily -> instant.plusDays(periods)
            is Weekdays -> {
                var next = instant
                var added = 0
                while (added < periods) {
                    next = next.plusDays(1)
                    if (next.toLocalDateTime(zone).dayOfWeek != DayOfWeek.SATURDAY &&
                        next.toLocalDateTime(zone).dayOfWeek != DayOfWeek.SUNDAY) {
                        added++
                    }
                }
                next
            }
            is Weekends -> {
                var next = instant
                var added = 0
                while (added < periods) {
                    next = next.plusDays(1)
                    if (next.toLocalDateTime(zone).dayOfWeek == DayOfWeek.SATURDAY ||
                        next.toLocalDateTime(zone).dayOfWeek == DayOfWeek.SUNDAY) {
                        added++
                    }
                }
                next
            }
            is Weekly -> instant.plusDays(periods * 7)
            is Monthly -> instant.plusMonths(periods)
            is Yearly -> instant.plusYears(periods)
            is Interval -> instant.plusDays(periods * days)
        }
    }
}

@Serializable
sealed interface AlarmSound {
    data object SystemDefault : AlarmSound
    data object Silent : AlarmSound
    @Serializable
    data class CustomFile(
        val fileName: String,
        val originalPath: String,
        val displayName: String,
        val durationSeconds: Int? = null
    ) : AlarmSound
    @Serializable
    data class SystemRingtone(val uri: String) : AlarmSound
}

@Serializable
data class PreAlarmConfig(
    val enabled: Boolean,
    val minutesBefore: Int,
    val sound: AlarmSound,
    val volumeFade: VolumeFade,
    val targetVolumePercent: Int = 50
)

@Serializable
data class VolumeFade(
    val enabled: Boolean,
    val durationSeconds: Int,
    val curve: FadeCurve = FadeCurve.Linear
)

@Serializable
enum class FadeCurve { Linear, EaseIn, EaseOut, Sigmoid }

@Serializable
data class SnoozeConfig(
    val durationMinutes: Int,
    val maxCount: Int,
    val autoSnooze: Boolean,
    val decreasingIntervalPercent: Int = 0
)

@Serializable
data class AutoDismissConfig(
    val enabled: Boolean,
    val timeoutMinutes: Int
)

@Serializable
enum class ChallengeType { Math, QR, Shake, None }

@Serializable
sealed interface VibrationPattern {
    data object Default : VibrationPattern
    data object None : VibrationPattern
    @Serializable
    data class Custom(val pattern: List<Long>) : VibrationPattern
    @Serializable
    data class System(val patternName: String) : VibrationPattern
}