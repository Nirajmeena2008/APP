package com.yourname.liquidalarm.alarm

import android.content.Context
import android.hardware.camera2.CameraManager
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.guava.await

class ChallengeQR(
    private val context: Context,
    private val targetCode: String,
    private val previewView: PreviewView
) {
    private var cameraProvider: ProcessCameraProvider? = null
    private var barcodeScanner: BarcodeScanner? = null
    private var isScanning = false
    private var callback: ((Boolean) -> Unit)? = null

    fun startChallenge(onResult: (Boolean) -> Unit) {
        callback = onResult
        isScanning = true
        barcodeScanner = BarcodeScanning.getClient()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                cameraProvider = ProcessCameraProvider.getInstance(context).await()
                startCamera()
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post { onResult(false) }
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
                            Handler(Looper.getMainLooper()).post {
                                callback?.invoke(true)
                                callback = null
                            }
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
