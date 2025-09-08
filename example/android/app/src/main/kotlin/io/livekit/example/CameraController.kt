package io.livekit.example

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.core.content.ContextCompat

class CameraController {
    companion object {
        private var previewTextureView: TextureView? = null
        fun setPreviewTextureView(view: TextureView?) {
            previewTextureView = view
        }
    }

    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null
    private var selectedCameraId: String? = null
    private var previewSize: Size = Size(1280, 720)

    fun initialize() {
        ensureBackgroundThread()
        val view = previewTextureView
        if (view != null) {
            if (view.isAvailable) {
                previewSize = Size(view.width.coerceAtLeast(640), view.height.coerceAtLeast(480))
            } else {
                view.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                        previewSize = Size(width.coerceAtLeast(640), height.coerceAtLeast(480))
                    }
                    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                        previewSize = Size(width.coerceAtLeast(640), height.coerceAtLeast(480))
                    }
                    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true
                    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
                }
            }
        }
    }

    private fun ensureBackgroundThread() {
        if (backgroundThread == null) {
            backgroundThread = HandlerThread("CameraBackground").also { it.start() }
            backgroundHandler = Handler(backgroundThread!!.looper)
        }
    }

    @SuppressLint("MissingPermission")
    fun startPreview(context: Context) {
        ensureBackgroundThread()
        val view = previewTextureView ?: return
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            throw SecurityException("CAMERA permission not granted")
        }
        if (view.isAvailable) {
            // Texture is ready, open camera immediately
            previewSize = Size(view.width.coerceAtLeast(640), view.height.coerceAtLeast(480))
            openCameraAndStartSession(context)
        } else {
            // Wait for TextureView to become available, then open camera
            view.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                    previewSize = Size(width.coerceAtLeast(640), height.coerceAtLeast(480))
                    openCameraAndStartSession(context)
                }
                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                    previewSize = Size(width.coerceAtLeast(640), height.coerceAtLeast(480))
                }
                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true
                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun openCameraAndStartSession(context: Context) {
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        selectedCameraId = manager.cameraIdList.firstOrNull { id ->
            val chars = manager.getCameraCharacteristics(id)
            val facing = chars.get(CameraCharacteristics.LENS_FACING)
            facing == CameraCharacteristics.LENS_FACING_BACK
        } ?: manager.cameraIdList.first()

        manager.openCamera(selectedCameraId!!, object : CameraDevice.StateCallback() {
            override fun onOpened(device: CameraDevice) {
                cameraDevice = device
                createSession()
            }
            override fun onDisconnected(device: CameraDevice) {
                device.close()
                cameraDevice = null
            }
            override fun onError(device: CameraDevice, error: Int) {
                device.close()
                cameraDevice = null
            }
        }, backgroundHandler)
    }

    private fun createSession() {
        val texture = previewTextureView?.surfaceTexture ?: return
        texture.setDefaultBufferSize(previewSize.width, previewSize.height)
        val previewSurface = Surface(texture)

        imageReader = ImageReader.newInstance(previewSize.width, previewSize.height, ImageFormat.JPEG, 1)
        val captureSurface = imageReader!!.surface

        cameraDevice?.createCaptureSession(listOf(previewSurface, captureSurface), object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                captureSession = session
                val requestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                    addTarget(previewSurface)
                    set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
                }
                session.setRepeatingRequest(requestBuilder.build(), null, backgroundHandler)
            }
            override fun onConfigureFailed(session: CameraCaptureSession) { }
        }, backgroundHandler)
    }

    fun stopPreview() {
        try {
            captureSession?.close()
            captureSession = null
            cameraDevice?.close()
            cameraDevice = null
            imageReader?.close()
            imageReader = null
        } finally {
            backgroundThread?.quitSafely()
            backgroundThread = null
            backgroundHandler = null
        }
    }

    fun captureImage(): ByteArray? {
        val session = captureSession ?: return null
        val device = cameraDevice ?: return null
        val reader = imageReader ?: return null
        var resultBytes: ByteArray? = null
        val listener = ImageReader.OnImageAvailableListener { r ->
            val image: Image = r.acquireNextImage() ?: return@OnImageAvailableListener
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            image.close()
            resultBytes = bytes
        }
        reader.setOnImageAvailableListener(listener, backgroundHandler)

        val request = device.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE).apply {
            addTarget(reader.surface)
            set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
        }.build()
        session.capture(request, null, backgroundHandler)

        Thread.sleep(200)
        return resultBytes
    }
} 