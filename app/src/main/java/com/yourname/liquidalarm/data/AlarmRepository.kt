package com.yourname.liquidalarm.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.rxjava3.preferences
import androidx.datastore.rxjava3.RxDataStore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

class AlarmRepository(private val context: Context) {
    private val dataStore: RxDataStore<Preferences> = context.createDataStore("liquid_alarm_alarms")
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true; isLenient = true }
    private val ALARMS_KEY = stringPreferencesKey("alarms_json")
    private val customSoundsDir: File = File(context.filesDir, "alarms").apply { mkdirs() }

    val alarmsFlow: Flowable<List<AlarmEntity>> = dataStore.data
        .map { prefs ->
            val jsonString = prefs[ALARMS_KEY] ?: "[]"
            AlarmSerializer.deserialize(jsonString)
        }
        .onErrorReturn { emptyList() }
        .subscribeOn(Schedulers.io())

    suspend fun getAlarms(): List<AlarmEntity> = alarmsFlow.firstElement().blockingGet()

    suspend fun saveAlarms(alarms: List<AlarmEntity>): Boolean = withContext(Dispatchers.IO) {
        try {
            val jsonString = AlarmSerializer.serialize(alarms)
            dataStore.updateData { it[ALARMS_KEY] = jsonString }.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addAlarm(alarm: AlarmEntity): Boolean = withContext(Dispatchers.IO) {
        val current = getAlarms()
        return@withContext saveAlarms(current + alarm)
    }

    suspend fun updateAlarm(alarm: AlarmEntity): Boolean = withContext(Dispatchers.IO) {
        val current = getAlarms()
        val index = current.indexOfFirst { it.id == alarm.id }
        if (index >= 0) {
            val updated = current.toMutableList().apply { set(index, alarm) }
            return@withContext saveAlarms(updated)
        }
        false
    }

    suspend fun deleteAlarm(alarmId: String): Boolean = withContext(Dispatchers.IO) {
        val current = getAlarms()
        val filtered = current.filter { it.id != alarmId }
        return@withContext saveAlarms(filtered)
    }

    suspend fun toggleAlarm(alarmId: String, enabled: Boolean): Boolean = withContext(Dispatchers.IO) {
        val current = getAlarms()
        val index = current.indexOfFirst { it.id == alarmId }
        if (index >= 0) {
            val updated = current.toMutableList().apply {
                set(index, current[index].copyWithModified(isEnabled = enabled))
            }
            return@withContext saveAlarms(updated)
        }
        false
    }

    fun getCustomSoundsDir(): File = customSoundsDir

    suspend fun copySoundFile(sourcePath: String, fileName: String): String = withContext(Dispatchers.IO) {
        val sourceFile = File(sourcePath)
        val destFile = File(customSoundsDir, fileName)
        sourceFile.copyTo(destFile, overwrite = true)
        fileName
    }

    fun getSoundFile(fileName: String): File = File(customSoundsDir, fileName)

    fun soundFileExists(fileName: String): Boolean = File(customSoundsDir, fileName).exists()

    suspend fun deleteUnusedSoundFiles(usedFileNames: Set<String>) = withContext(Dispatchers.IO) {
        customSoundsDir.listFiles()?.forEach { file ->
            if (file.name !in usedFileNames && (file.name.endsWith(".ogg") || file.name.endsWith(".mp3") || file.name.endsWith(".wav") || file.name.endsWith(".m4a"))) {
                file.delete()
            }
        }
    }

    suspend fun exportBackup(): String = withContext(Dispatchers.IO) {
        val alarms = getAlarms()
        return json.encodeToString(
            BackupData(
                version = 1,
                exportedAt = java.time.Instant.now().toString(),
                alarms = alarms
            )
        )
    }

    suspend fun importBackup(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val backup = json.decodeFromString<BackupData>(jsonString)
            return saveAlarms(backup.alarms)
        } catch (e: Exception) {
            false
        }
    }

    @Serializable
    private data class BackupData(
        val version: Int,
        val exportedAt: String,
        val alarms: List<AlarmEntity>
    )
}