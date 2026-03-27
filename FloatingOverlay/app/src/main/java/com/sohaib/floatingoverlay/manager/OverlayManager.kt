package com.sohaib.floatingoverlay.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import com.sohaib.floatingoverlay.databinding.ViewFloatingBinding

/**
 * Created by: Sohaib Ahmed
 * Date: 6/2/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

class OverlayManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val activityResultRegistry: ActivityResultRegistry,
) {

    private lateinit var permissionLauncher: ActivityResultLauncher<Unit>
    private var onPermissionResult: ((Boolean) -> Unit)? = null
    private var isViewAdded = false

    init {
        registerPermissionLauncher()
    }

    private fun registerPermissionLauncher() {
        val resultContract = object : ActivityResultContract<Unit, Boolean>() {
            override fun createIntent(context: Context, input: Unit): Intent {
                return Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, "package:${context.packageName}".toUri())
            }

            override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
                return Settings.canDrawOverlays(context)
            }
        }

        permissionLauncher = activityResultRegistry
            .register("overlay-permission", lifecycleOwner, resultContract)
            { granted ->
                onPermissionResult?.invoke(granted)
            }
    }

    fun hasOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun requestOverlayPermission(callback: (Boolean) -> Unit) {
        if (hasOverlayPermission()) {
            callback(true)
        } else {
            onPermissionResult = callback
            permissionLauncher.launch(Unit)
        }
    }

    fun showOverlayView(viewBinding: ViewFloatingBinding) {
        if (!hasOverlayPermission() || isViewAdded) return

        val view = viewBinding.root
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 50
            y = 100
        }

        viewBinding.mbClose.setOnClickListener {
            windowManager.removeView(view)
            isViewAdded = false
        }

        // Drag support
        @SuppressLint("ClickableViewAccessibility")
        view.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = layoutParams.x
                        initialY = layoutParams.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        layoutParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        layoutParams.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(view, layoutParams)
                        return true
                    }
                }
                return false
            }
        })

        windowManager.addView(view, layoutParams)
        isViewAdded = true

        // Animate the view in
        view.animatePopIn()
    }

    private fun View.animatePopIn() {
        alpha = 0f
        scaleX = 0.8f
        scaleY = 0.8f
        animate()
            .alpha(1f)
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(300)
            .setInterpolator(OvershootInterpolator())
            .withEndAction {
                animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }
            .start()
    }
}