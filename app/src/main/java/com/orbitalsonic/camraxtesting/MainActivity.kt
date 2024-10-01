package com.orbitalsonic.camraxtesting

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.video.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.Toast
import androidx.camera.lifecycle.ProcessCameraProvider
import android.util.Log
import android.widget.TextView
import androidx.camera.video.VideoCapture
import androidx.core.content.PermissionChecker
import com.orbitalsonic.camraxtesting.databinding.ActivityMainBinding
import heartRateCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import respiratoryRateCalculator
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity(), SensorEventListener {
    // Heart Rate Variables
    private lateinit var viewBinding: ActivityMainBinding

    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService
    private var lastRecordedVideoUri: Uri? = null

    // Resp Rate Variables
    private lateinit var sensorManager: SensorManager
    private var isRecording = false
    private val accelValuesX = mutableListOf<Float>()
    private val accelValuesY = mutableListOf<Float>()
    private val accelValuesZ = mutableListOf<Float>()
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Measure heart rate
        viewBinding.recordBtn.setOnClickListener { captureVideo() }
        cameraExecutor = Executors.newSingleThreadExecutor()
        viewBinding.heartRateBtn.setOnClickListener {
            val heartRateTextView: TextView = findViewById(R.id.heartRateText)
            val videoUri = lastRecordedVideoUri
            if (videoUri != null) {
                heartRateTextView.text = "Calculating..."
                CoroutineScope(Dispatchers.Main).launch {
                    val heartRate = withContext(Dispatchers.IO) {
                        heartRateCalculator(videoUri, contentResolver)
                    }
                    // Use the calculated heart rate value
                    heartRateTextView.text = "Heart Rate: $heartRate"
                }
            } else {
                // Handle the case when no video has been recorded yet
                Toast.makeText(this, "No video recorded yet", Toast.LENGTH_SHORT).show()
                heartRateTextView.text = "No video recorded yet"
            }
        }

        // Measure respiratory rate
        viewBinding.respRateBtn.setOnClickListener {
            if (!isRecording) {
                startRecording()
            }
        }
    }

    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return

        viewBinding.recordBtn.isEnabled = false

        val curRecording = recording
        if (curRecording != null) {
            curRecording.stop()
            recording = null
            return
        }

        // Turn on flash (torch) before starting recording
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val camera = cameraProvider.bindToLifecycle(
                this, CameraSelector.DEFAULT_BACK_CAMERA, videoCapture
            )
            camera.cameraControl.enableTorch(true) // Enable flash

            val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis())
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
                }
            }

            val mediaStoreOutputOptions = MediaStoreOutputOptions
                .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                .setContentValues(contentValues)
                .build()

            recording = videoCapture.output
                .prepareRecording(this, mediaStoreOutputOptions)
                .apply {
                    if (PermissionChecker.checkSelfPermission(this@MainActivity,
                            Manifest.permission.RECORD_AUDIO) ==
                        PermissionChecker.PERMISSION_GRANTED)
                    {
                        withAudioEnabled()
                    }
                }
                .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                    when(recordEvent) {
                        is VideoRecordEvent.Start -> {
                            viewBinding.recordBtn.apply {
                                text = getString(R.string.stop_capture)
                                isEnabled = true
                            }
                        }
                        is VideoRecordEvent.Finalize -> {
                            camera.cameraControl.enableTorch(false) // Disable flash
                            if (!recordEvent.hasError()) {
                                val msg = "Video capture succeeded: ${recordEvent.outputResults.outputUri}"
                                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                                Log.d(TAG, msg)
                                lastRecordedVideoUri = recordEvent.outputResults.outputUri // Store the video URI
                            } else {
                                recording?.close()
                                recording = null
                                Log.e(TAG, "Video capture ends with error: ${recordEvent.error}")
                            }
                            viewBinding.recordBtn.apply {
                                text = getString(R.string.start_capture)
                                isEnabled = true
                            }
                        }
                    }
                }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST,
                    FallbackStrategy.higherQualityOrLowerThan(Quality.SD)))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, videoCapture)

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    private fun startRecording() {
        isRecording = true
        accelValuesX.clear()
        accelValuesY.clear()
        accelValuesZ.clear()
        val respRateTextView: TextView = findViewById(R.id.respRateText)
        respRateTextView.text = "Measuring..."
        setUpSensor()

        // Stop recording after a certain duration (e.g., 45 seconds)
        scope.launch {
            delay(45000) // Delay for 45 seconds (adjust as needed)
            stopRecording()
        }
    }

    private fun stopRecording() {
        isRecording = false
        sensorManager.unregisterListener(this)
        scope.launch(Dispatchers.Default) {
            val respRate = calculateRespiratoryRate()
            withContext(Dispatchers.Main) {
                val respRateTextView: TextView = findViewById(R.id.respRateText)
                respRateTextView.text = "Respiratory Rate: $respRate"
            }
        }
    }

    private fun setUpSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            accelValuesX.add(event.values[0])
            accelValuesY.add(event.values[1])
            accelValuesZ.add(event.values[2])
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    private fun calculateRespiratoryRate(): Int {
        val respiratoryRate = respiratoryRateCalculator(accelValuesX,accelValuesY,accelValuesZ)
        return respiratoryRate
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        sensorManager.unregisterListener(this)
    }
}