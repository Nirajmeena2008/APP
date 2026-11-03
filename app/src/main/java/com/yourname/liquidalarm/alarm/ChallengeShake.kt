package com.yourname.liquidalarm.alarm

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper

class ChallengeShake(private val context: Context) {
    private var shakeCount = 0
    private var lastShakeTime = 0L
    private var sensorManager: SensorManager? = null
    private var sensorListener: SensorEventListener? = null
    private var requiredShakes = 10
    private var callback: ((Boolean) -> Unit)? = null

    fun startChallenge(requiredShakes: Int = 10, onResult: (Boolean) -> Unit) {
        this.requiredShakes = requiredShakes
        this.callback = onResult
        this.shakeCount = 0
        this.lastShakeTime = 0

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let { e ->
                    val x = e.values[0]
                    val y = e.values[1]
                    val z = e.values[2]
                    val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()) - SensorManager.GRAVITY_EARTH

                    if (acceleration > 15) {
                        val now = System.currentTimeMillis()
                        if (now - lastShakeTime > 500) {
                            lastShakeTime = now
                            shakeCount++
                            if (shakeCount >= requiredShakes) {
                                stop()
                                Handler(Looper.getMainLooper()).post {
                                    callback?.invoke(true)
                                    callback = null
                                }
                            }
                        }
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accelerometer?.let { sensorManager?.registerListener(sensorListener!!, it, SensorManager.SENSOR_DELAY_UI) }
    }

    fun getCurrentCount(): Int = shakeCount

    fun getRequiredCount(): Int = requiredShakes

    fun stop() {
        sensorListener?.let { sensorManager?.unregisterListener(it) }
    }
}
