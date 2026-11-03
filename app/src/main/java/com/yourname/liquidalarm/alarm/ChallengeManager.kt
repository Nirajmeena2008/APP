package com.yourname.liquidalarm.alarm

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.guava.await

interface ChallengeCallback {
    fun onChallengeCompleted(success: Boolean)
}

class ChallengeManager(private val context: Context) {

    fun startMathChallenge(
        difficulty: Int = 1,
        callback: ChallengeCallback
    ): MathChallenge {
        return MathChallenge(difficulty, callback)
    }

    fun startQRChallenge(
        targetCode: String,
        previewView: PreviewView,
        callback: ChallengeCallback
    ): QRChallenge {
        return QRChallenge(context, targetCode, previewView, callback)
    }

    fun startShakeChallenge(
        requiredShakes: Int = 10,
        callback: ChallengeCallback
    ): ShakeChallenge {
        return ShakeChallenge(context, requiredShakes, callback)
    }
}

class MathChallenge(
    private val difficulty: Int,
    private val callback: ChallengeCallback
) {
    private var currentAnswer = 0
    private var question = ""

    fun generateQuestion(): String {
        val max = when (difficulty) {
            1 -> 10
            2 -> 20
            3 -> 50
            else -> 100
        }
        val a = (1..max).random()
        val b = (1..max).random()
        val op = (0..2).random()

        return when (op) {
            0 -> { currentAnswer = a + b; "$a + $b = ?" }
            1 -> { currentAnswer = a - b; "$a - $b = ?" }
            else -> { currentAnswer = a * b; "$a × $b = ?" }
        }
    }

    fun checkAnswer(input: String): Boolean {
        return try {
            input.trim().toInt() == currentAnswer
        } catch (e: Exception) {
            false
        }
    }

    fun submitAnswer(input: String) {
        val success = checkAnswer(input)
        callback.onChallengeCompleted(success)
    }
}

class QRChallenge(
    private val context: Context,
    private val targetCode: String,
    private val previewView: PreviewView,
    private val callback: ChallengeCallback
) {
    private var cameraProvider: ProcessCameraProvider? = null
    private var barcodeScanner: BarcodeScanner? = null
    private var isScanning = false

    fun start() {
        isScanning = true
        barcodeScanner = BarcodeScanning.getClient()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                cameraProvider = ProcessCameraProvider.getInstance(context).await()
                startCamera()
            } catch (e: Exception) {
                callback.onChallengeCompleted(false)
            }
        }
    }

    private fun startCamera() {
        val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().also { analyzer ->
                analyzer.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                    scanBarcode(imageProxy)
                }
            }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProvider?.bindToLifecycle(
            (context as? androidx.lifecycle.LifecycleOwner)!!,
            cameraSelector, preview, imageAnalysis
        )
    }

    private fun scanBarcode(imageProxy: androidx.camera.core.ImageProxy) {
        if (!isScanning) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            barcodeScanner?.process(image)
                ?.addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        if (barcode.rawValue?.equals(targetCode, ignoreCase = true) == true) {
                            isScanning = false
                            stop()
                            Handler(Looper.getMainLooper()).post { callback.onChallengeCompleted(true) }
                            break
                        }
                    }
                    imageProxy.close()
                }
                ?.addOnFailureListener { _ ->
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    fun stop() {
        isScanning = false
        cameraProvider?.unbindAll()
        barcodeScanner?.close()
    }
}

class ShakeChallenge(
    private val context: Context,
    private val requiredShakes: Int,
    private val callback: ChallengeCallback
) {
    private var shakeCount = 0
    private var lastShakeTime = 0L
    private var sensorManager: SensorManager? = null
    private var sensorListener: SensorEventListener? = null

    fun start() {
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
                                Handler(Looper.getMainLooper()).post { callback.onChallengeCompleted(true) }
                            }
                        }
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accelerometer?.let { sensorManager?.registerListener(sensorListener!!, it, SensorManager.SENSOR_DELAY_UI) }
    }

    fun stop() {
        sensorListener?.let { sensorManager?.unregisterListener(it) }
    }
}
