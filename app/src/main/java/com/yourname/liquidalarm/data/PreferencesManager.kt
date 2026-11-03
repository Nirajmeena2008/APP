package com.yourname.liquidalarm.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.rxjava3.preferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx3.asFlowable
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.tasks.await

class PreferencesManager(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("liquid_alarm_prefs")
    private val dataStore = context.dataStore

    private val KEY_THEME = intPreferencesKey("theme")
    private val KEY_TIME_FORMAT_24H = booleanPreferencesKey("time_format_24h")
    private val KEY_DEFAULT_SNOOZE_MINUTES = intPreferencesKey("default_snooze_minutes")
    private val KEY_DEFAULT_FADE_SECONDS = intPreferencesKey("default_fade_seconds")
    private val KEY_DEFAULT_CHALLENGE = intPreferencesKey("default_challenge")
    private val KEY_DEFAULT_PRE_ALARM_MINUTES = intPreferencesKey("default_pre_alarm_minutes")
    private val KEY_DEFAULT_PRE_ALARM_ENABLED = booleanPreferencesKey("default_pre_alarm_enabled")
    private val KEY_VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
    private val KEY_AUTO_DISMISS_MINUTES = intPreferencesKey("auto_dismiss_minutes")
    private val KEY_SHOW_SECONDS = booleanPreferencesKey("show_seconds")
    private val KEY_KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
    private val KEY_BATTERY_OPTIMIZATION_CHECKED = booleanPreferencesKey("battery_optimization_checked")
    private val KEY_FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    private val KEY_CUSTOM_SOUNDS_DIR = stringPreferencesKey("custom_sounds_dir")

    val theme: Flowable<Int> = dataStore.data.map { it[KEY_THEME] ?: 0 }.asFlowable()
    suspend fun setTheme(value: Int) { dataStore.edit { it[KEY_THEME] = value } }

    val timeFormat24h: Flowable<Boolean> = dataStore.data.map { it[KEY_TIME_FORMAT_24H] ?: true }.asFlowable()
    suspend fun setTimeFormat24h(value: Boolean) { dataStore.edit { it[KEY_TIME_FORMAT_24H] = value } }

    val defaultSnoozeMinutes: Flowable<Int> = dataStore.data.map { it[KEY_DEFAULT_SNOOZE_MINUTES] ?: 10 }.asFlowable()
    suspend fun setDefaultSnoozeMinutes(value: Int) { dataStore.edit { it[KEY_DEFAULT_SNOOZE_MINUTES] = value } }

    val defaultFadeSeconds: Flowable<Int> = dataStore.data.map { it[KEY_DEFAULT_FADE_SECONDS] ?: 60 }.asFlowable()
    suspend fun setDefaultFadeSeconds(value: Int) { dataStore.edit { it[KEY_DEFAULT_FADE_SECONDS] = value } }

    val defaultChallenge: Flowable<Int> = dataStore.data.map { it[KEY_DEFAULT_CHALLENGE] ?: 0 }.asFlowable()
    suspend fun setDefaultChallenge(value: Int) { dataStore.edit { it[KEY_DEFAULT_CHALLENGE] = value } }

    val defaultPreAlarmMinutes: Flowable<Int> = dataStore.data.map { it[KEY_DEFAULT_PRE_ALARM_MINUTES] ?: 10 }.asFlowable()
    suspend fun setDefaultPreAlarmMinutes(value: Int) { dataStore.edit { it[KEY_DEFAULT_PRE_ALARM_MINUTES] = value } }

    val defaultPreAlarmEnabled: Flowable<Boolean> = dataStore.data.map { it[KEY_DEFAULT_PRE_ALARM_ENABLED] ?: false }.asFlowable()
    suspend fun setDefaultPreAlarmEnabled(value: Boolean) { dataStore.edit { it[KEY_DEFAULT_PRE_ALARM_ENABLED] = value } }

    val vibrationEnabled: Flowable<Boolean> = dataStore.data.map { it[KEY_VIBRATION_ENABLED] ?: true }.asFlowable()
    suspend fun setVibrationEnabled(value: Boolean) { dataStore.edit { it[KEY_VIBRATION_ENABLED] = value } }

    val autoDismissMinutes: Flowable<Int> = dataStore.data.map { it[KEY_AUTO_DISMISS_MINUTES] ?: 10 }.asFlowable()
    suspend fun setAutoDismissMinutes(value: Int) { dataStore.edit { it[KEY_AUTO_DISMISS_MINUTES] = value } }

    val showSeconds: Flowable<Boolean> = dataStore.data.map { it[KEY_SHOW_SECONDS] ?: false }.asFlowable()
    suspend fun setShowSeconds(value: Boolean) { dataStore.edit { it[KEY_SHOW_SECONDS] = value } }

    val keepScreenOn: Flowable<Boolean> = dataStore.data.map { it[KEY_KEEP_SCREEN_ON] ?: true }.asFlowable()
    suspend fun setKeepScreenOn(value: Boolean) { dataStore.edit { it[KEY_KEEP_SCREEN_ON] = value } }

    val batteryOptimizationChecked: Flowable<Boolean> = dataStore.data.map { it[KEY_BATTERY_OPTIMIZATION_CHECKED] ?: false }.asFlowable()
    suspend fun setBatteryOptimizationChecked(value: Boolean) { dataStore.edit { it[KEY_BATTERY_OPTIMIZATION_CHECKED] = value } }

    val isFirstLaunch: Flowable<Boolean> = dataStore.data.map { it[KEY_FIRST_LAUNCH] ?: true }.asFlowable()
    suspend fun setFirstLaunchComplete() { dataStore.edit { it[KEY_FIRST_LAUNCH] = false } }

    val customSoundsDir: Flowable<String> = dataStore.data.map { it[KEY_CUSTOM_SOUNDS_DIR] ?: "" }.asFlowable()
    suspend fun setCustomSoundsDir(value: String) { dataStore.edit { it[KEY_CUSTOM_SOUNDS_DIR] = value } }

    fun themeSingle(): Single<Int> = theme.firstOrError()
    fun timeFormat24hSingle(): Single<Boolean> = timeFormat24h.firstOrError()
    fun defaultSnoozeMinutesSingle(): Single<Int> = defaultSnoozeMinutes.firstOrError()
}
