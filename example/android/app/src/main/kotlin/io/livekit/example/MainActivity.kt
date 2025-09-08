package io.livekit.example

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val CHANNEL = "ahd_camera_sdk"
    private lateinit var cameraController: CameraController

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        // Register PlatformView
        flutterEngine.platformViewsController.registry.registerViewFactory(
            "ahd_camera_view",
            AhdCameraViewFactory()
        )
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "initializeCamera" -> initializeCamera(result)
                "startPreview" -> startPreview(result)
                "stopPreview" -> stopPreview(result)
                "captureImage" -> captureImage(result)
                else -> result.notImplemented()
            }
        }
    }

    private fun initializeCamera(result: MethodChannel.Result) {
        try {
            cameraController = CameraController()
            cameraController.initialize()
            result.success(true)
        } catch (e: Exception) {
            result.error("INIT_ERROR", e.message, null)
        }
    }

    private fun startPreview(result: MethodChannel.Result) {
        try {
            cameraController.startPreview(this)
            result.success(true)
        } catch (e: Exception) {
            result.error("PREVIEW_ERROR", e.message, null)
        }
    }

    private fun stopPreview(result: MethodChannel.Result) {
        try {
            cameraController.stopPreview()
            result.success(true)
        } catch (e: Exception) {
            result.error("STOP_ERROR", e.message, null)
        }
    }

    private fun captureImage(result: MethodChannel.Result) {
        try {
            val bytes = cameraController.captureImage()
            result.success(bytes)
        } catch (e: Exception) {
            result.error("CAPTURE_ERROR", e.message, null)
        }
    }
}