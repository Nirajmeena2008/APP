package com.yourname.liquidalarm.util

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {
    fun copyFileToAppDir(context: Context, sourceUri: Uri, fileName: String): File? {
        val destDir = File(context.filesDir, "alarms")
        if (!destDir.exists()) destDir.mkdirs()

        val destFile = File(destDir, fileName)
        try {
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            return destFile
        } catch (e: IOException) {
            return null
        }
    }

    fun getAudioFileName(uri: Uri, context: Context): String {
        var fileName = "alarm_${System.currentTimeMillis()}"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    fileName = it.getString(nameIndex) ?: fileName
                }
            }
        }
        // Ensure extension
        if (!fileName.contains(".")) {
            fileName += ".ogg"
        }
        return fileName
    }

    fun getAudioDuration(uri: Uri, context: Context): Int? {
        try {
            val mediaMetadataRetriever = android.media.MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(context, uri)
            val durationStr = mediaMetadataRetriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)
            mediaMetadataRetriever.release()
            return durationStr?.toIntOrNull()
        } catch (e: Exception) {
            return null
        }
    }

    fun deleteFile(context: Context, fileName: String): Boolean {
        val file = File(context.filesDir, "alarms/$fileName")
        return file.delete()
    }

    fun getAlarmsDir(context: Context): File {
        val dir = File(context.filesDir, "alarms")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun listCustomSounds(context: Context): List<File> {
        val dir = getAlarmsDir(context)
        return dir.listFiles()?.filter { it.extension in setOf("ogg", "mp3", "wav", "m4a", "flac") }?.toList() ?: emptyList()
    }
}