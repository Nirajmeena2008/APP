package com.yourname.liquidalarm.alarm

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.upstream.DefaultDataSourceFactory
import androidx.media3.common.util.Util
import com.yourname.liquidalarm.data.AlarmEntity
import com.yourname.liquidalarm.data.AlarmSound
import com.yourname.liquidalarm.data.VolumeFade
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MediaPlayerManager(private val context: Context) {
    private var exoPlayer: ExoPlayer? = null
    private var fadeJob: Job? = null
    private var currentVolume = 0f
    private var targetVolume = 1f
    private var fadeDurationMs = 60000L
    private var isPreAlarm = false

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ALARM)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
        .build()

    fun playAlarm(
        alarm: AlarmEntity,
        isPreAlarm: Boolean,
        onCompletion: (Boolean) -> Unit
    ) {
        this.isPreAlarm = isPreAlarm
        val sound = if (isPreAlarm) alarm.preAlarmConfig.sound else alarm.mainSound
        val volumeFade = if (isPreAlarm) alarm.preAlarmConfig.volumeFade else alarm.mainSoundVolumeFade

        fadeDurationMs = volumeFade.durationSeconds * 1000L
        targetVolume = if (isPreAlarm) alarm.preAlarmConfig.targetVolumePercent / 100f else 1f

        stop()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (sound) {
                    is AlarmSound.SystemDefault -> playSystemDefault(onCompletion)
                    is AlarmSound.Silent -> onCompletion(false)
                    is AlarmSound.CustomFile -> playCustomFile(sound.fileName, onCompletion)
                    is AlarmSound.SystemRingtone -> playSystemRingtone(sound.uri, onCompletion)
                }
            } catch (e: Exception) {
                onCompletion(false)
            }
        }
    }

    private fun playSystemDefault(onCompletion: (Boolean) -> Unit) {
        val uri = android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI
            ?: android.provider.Settings.System.DEFAULT_RINGTONE_URI
        playUri(uri!!, onCompletion)
    }

    private fun playSystemRingtone(uriString: String, onCompletion: (Boolean) -> Unit) {
        playUri(Uri.parse(uriString), onCompletion)
    }

    private fun playCustomFile(fileName: String, onCompletion: (Boolean) -> Unit) {
        val file = File(context.filesDir, "alarms/$fileName")
        if (file.exists()) {
            playUri(Uri.fromFile(file), onCompletion)
        } else {
            playSystemDefault(onCompletion)
        }
    }

    private fun playUri(uri: Uri, onCompletion: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val exoPlayer = ExoPlayer.Builder(context).build()
                this@MediaPlayerManager.exoPlayer = exoPlayer

                val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "LiquidAlarm"))
                val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaItem(MediaItem.fromUri(uri))

                exoPlayer.setMediaSource(mediaSource)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
                exoPlayer.volume = 0f

                startVolumeFade(exoPlayer, onCompletion)

                exoPlayer.addListener(object : androidx.media3.exoplayer.Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == androidx.media3.exoplayer.Player.STATE_ENDED) {
                            fadeJob?.cancel()
                            onCompletion(true)
                        }
                    }
                    override fun onPlayerError(error: androidx.media3.exoplayer.ExoPlaybackException) {
                        fadeJob?.cancel()
                        onCompletion(false)
                    }
                })
            } catch (e: Exception) {
                onCompletion(false)
            }
        }
    }

    private fun startVolumeFade(player: ExoPlayer, onCompletion: (Boolean) -> Unit) {
        fadeJob?.cancel()
        currentVolume = 0f
        player.volume = 0f

        val steps = 60
        val stepDuration = fadeDurationMs / steps
        val volumeStep = targetVolume / steps

        fadeJob = CoroutineScope(Dispatchers.Main).launch {
            repeat(steps) {
                if (!isActive) return@launch
                currentVolume = (currentVolume + volumeStep).coerceAtMost(targetVolume)
                player.volume = currentVolume
                delay(stepDuration)
            }
            player.volume = targetVolume
        }
    }

    fun setVolume(volume: Float) {
        exoPlayer?.volume = volume.coerceIn(0f, 1f)
    }

    fun stop() {
        fadeJob?.cancel()
        fadeJob = null

        exoPlayer?.run {
            stop()
            release()
        }
        exoPlayer = null
    }

    fun pause() {
        exoPlayer?.playWhenReady = false
    }

    fun resume() {
        exoPlayer?.playWhenReady = true
    }

    fun isPlaying(): Boolean {
        return exoPlayer?.playWhenReady == true
    }
}