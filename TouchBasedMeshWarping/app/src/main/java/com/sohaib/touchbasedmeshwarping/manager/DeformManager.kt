package com.sohaib.touchbasedmeshwarping.manager

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.sohaib.touchbasedmeshwarping.enums.DeformMode
import org.wysaid.common.Common
import org.wysaid.myUtils.MsgUtil
import org.wysaid.nativePort.CGEDeformFilterWrapper
import org.wysaid.texUtils.TextureRenderer
import org.wysaid.view.ImageGLSurfaceView

class DeformManager(
    private val imageGLSurfaceView: ImageGLSurfaceView,
    private var deformWrapper: CGEDeformFilterWrapper?,
    private val seekBar: SeekBar
) {
    var touchRadius: Float = 400.0f
        private set
    var touchIntensity: Float = 0.95f
        private set
    private var deformMode: DeformMode = DeformMode.Forward

    fun setDeformMode(mode: DeformMode) {
        deformMode = mode
    }

    fun onSeekBarChangeListener() = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            deformWrapper?.let {
                imageGLSurfaceView.lazyFlush(true) {
                    val intensity = progress / 100.0f
                    it.restoreWithIntensity(intensity)
                }
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    fun onTouchListener() = object : OnTouchListener {
        var mLastX: Float = 0f
        var mLastY: Float = 0f
        var mIsMoving: Boolean = false
        var mHasMotion: Boolean = false

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            if (deformWrapper == null) {
                return false
            }

            val viewport: TextureRenderer.Viewport = imageGLSurfaceView.getRenderViewport()
            val w = viewport.width.toFloat()
            val h = viewport.height.toFloat()
            val x = (event.x - viewport.x)
            val y = (event.y - viewport.y)

            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    mIsMoving = true
                    mLastX = x
                    mLastY = y
                    if (deformWrapper?.canUndo() == false) {
                        deformWrapper?.pushDeformStep()
                    }
                    return true
                }

                MotionEvent.ACTION_MOVE -> {}
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    mIsMoving = false
                    if (mHasMotion) {
                        imageGLSurfaceView.queueEvent {
                            deformWrapper?.pushDeformStep()
                            Log.i(Common.LOG_TAG, "Init undo status...")
                        }
                    }
                    return true
                }

                else -> return true
            }

            if (seekBar.progress != 0) {
                seekBar.setOnSeekBarChangeListener(null)
                seekBar.progress = 0
                seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener())
            }

            val lastX = mLastX
            val lastY = mLastY

            imageGLSurfaceView.lazyFlush(true) {
                if (deformWrapper == null) return@lazyFlush
                when (deformMode) {
                    DeformMode.Restore -> deformWrapper?.restoreWithPoint(x, y, w, h, touchRadius, touchIntensity)
                    DeformMode.Forward -> deformWrapper?.forwardDeform(lastX, lastY, x, y, w, h, touchRadius, touchIntensity)
                    DeformMode.Bloat -> deformWrapper?.bloatDeform(x, y, w, h, touchRadius, touchIntensity)
                    DeformMode.Wrinkle -> deformWrapper?.wrinkleDeform(x, y, w, h, touchRadius, touchIntensity)
                }
                mHasMotion = true
            }

            mLastX = x
            mLastY = y
            return true
        }
    }

    fun increaseRadius() {
        touchRadius += 10.0f
        checkRadius()
    }

    fun decreaseRadius() {
        touchRadius -= 10.0f
        checkRadius()
    }

    private fun checkRadius() {
        if (touchRadius < 10.0f) touchRadius = 10.0f
        else if (touchRadius > 400.0f) touchRadius = 400.0f
    }

    fun increaseIntensity() {
        touchIntensity += 0.05f
        checkIntensity()
    }

    fun decreaseIntensity() {
        touchIntensity -= 0.05f
        checkIntensity()
    }

    private fun checkIntensity() {
        if (touchIntensity < 0.02f) touchIntensity = 0.02f
        else if (touchIntensity > 0.99f) touchIntensity = 0.99f
    }

    fun undo() {
        imageGLSurfaceView.flush(true) {
            if (deformWrapper != null && deformWrapper?.undo() == false) {
                MsgUtil.toastMsg(imageGLSurfaceView.context, "Nothing to undo!")
            } else {
                imageGLSurfaceView.requestRender()
            }
        }
    }

    fun redo() {
        imageGLSurfaceView.flush(true) {
            if (deformWrapper != null && deformWrapper?.redo() == false) {
                MsgUtil.toastMsg(imageGLSurfaceView.context, "Nothing to redo!")
            } else {
                imageGLSurfaceView.requestRender()
            }
        }
    }

    fun restore() {
        imageGLSurfaceView.flush(true) {
            deformWrapper?.restore()
            imageGLSurfaceView.requestRender()
        }
    }

    fun release() {
        deformWrapper?.release(false)
        deformWrapper = null
    }
}
