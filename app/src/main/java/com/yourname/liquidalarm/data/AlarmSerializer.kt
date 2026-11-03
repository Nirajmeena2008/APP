package com.yourname.liquidalarm.data

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.Serializable

object AlarmSerializer {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun serialize(alarms: List<AlarmEntity>): String {
        return json.encodeToString(alarms)
    }

    fun deserialize(jsonString: String): List<AlarmEntity> {
        return try {
            json.decodeFromString(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun serializeForBackup(alarms: List<AlarmEntity>, version: Int = 1): String {
        val backup = BackupData(
            version = version,
            exportedAt = kotlinx.datetime.Clock.System.now().toString(),
            alarms = alarms
        )
        return json.encodeToString(backup)
    }

    fun deserializeFromBackup(jsonString: String): List<AlarmEntity> {
        return try {
            json.decodeFromString(jsonString).alarms
        } catch (e: Exception) {
            emptyList()
        }
    }

    @Serializable
    private data class BackupData(
        val version: Int,
        val exportedAt: String,
        val alarms: List<AlarmEntity>
    )
}
