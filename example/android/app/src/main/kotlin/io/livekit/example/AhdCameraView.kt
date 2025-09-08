package io.livekit.example

import android.content.Context
import android.view.TextureView
import android.widget.FrameLayout
import io.flutter.plugin.platform.PlatformView

class AhdCameraView(private val context: Context) : PlatformView {
    private val container: FrameLayout = FrameLayout(context)
    private val textureView: TextureView = TextureView(context)

    init {
        container.addView(
            textureView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        CameraController.setPreviewTextureView(textureView)
    }

    override fun getView(): android.view.View = container

    override fun dispose() {
        CameraController.setPreviewTextureView(null)
    }
} 