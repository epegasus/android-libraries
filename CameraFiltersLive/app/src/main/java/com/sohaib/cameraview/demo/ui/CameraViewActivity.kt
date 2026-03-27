package com.sohaib.cameraview.demo.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.sohaib.cameraview.demo.camera.CameraCaptureManager
import com.sohaib.cameraview.demo.camera.CaptureResult
import com.sohaib.cameraview.demo.camera.InitResult
import com.sohaib.cameraview.demo.databinding.ActivityCameraViewBinding
import com.sohaib.cameraview.demo.helper.showToast
import com.sohaib.cameraview.dev_controls.Facing
import com.sohaib.cameraview.dev_controls.Mode
import com.sohaib.cameraview.dev_controls.Preview
import com.sohaib.cameraview.dev_filter.Filters
import com.sohaib.cameraview.helper.CameraException
import com.sohaib.cameraview.helper.CameraListener
import kotlinx.coroutines.launch

class CameraViewActivity : AppCompatActivity() {

    private val binding by lazy { ActivityCameraViewBinding.inflate(layoutInflater) }
    private val captureManager by lazy { CameraCaptureManager(this, binding.cameraView) }

    private val errorListener = object : CameraListener() {
        override fun onCameraError(exception: CameraException) {
            super.onCameraError(exception)
            setCaptureUiBusy(false)
            isCapturing = false
            showToast("Camera error (${exception.reason}).", longDuration = true)
        }
    }

    private val allFilters = Filters.entries.toTypedArray()
    private var isCapturing = false
    private var currentFilter = 0

    private val permissionResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (isCameraPermissionReady()) initCameraView()
        else showToast("Camera and microphone permissions are required.", longDuration = true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupInsets()
        checkPermission()

        binding.ifvRotate.setOnClickListener { toggleCamera() }
        binding.ifvFilters.setOnClickListener { changeFilter() }
        binding.ifvCapture.setOnClickListener { onCaptureClick() }
    }

    private fun setupInsets() {
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            insets.getInsets(WindowInsetsCompat.Type.systemBars())
            insets
        }
    }

    private fun checkPermission() {
        if (isCameraPermissionReady()) initCameraView()
        else permissionResultLauncher.launch(requiredPermissions())
    }

    private fun isCameraPermissionReady(): Boolean =
        requiredPermissions().all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }

    private fun requiredPermissions(): Array<String> = buildList {
        add(Manifest.permission.CAMERA)
        add(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }.toTypedArray()

    private fun initCameraView() {
        binding.cameraView.setLifecycleOwner(this)
        binding.cameraView.removeCameraListener(errorListener)
        binding.cameraView.addCameraListener(errorListener)

        when (val initResult = captureManager.init()) {
            InitResult.Success -> Unit
            is InitResult.Failure -> {
                Log.e(TAG, "initCameraView", initResult.throwable)
                showToast(initResult.message, longDuration = true)
            }
        }
    }

    private fun onCaptureClick() {
        if (binding.cameraView.mode == Mode.VIDEO) {
            showToast("Switch to picture mode to capture still photos.", longDuration = true)
            return
        }
        if (isCapturing || binding.cameraView.isTakingPicture) return

        isCapturing = true
        setCaptureUiBusy(true)
        lifecycleScope.launch {
            when (val result = captureManager.captureAndSaveFilteredImage()) {
                is CaptureResult.Success -> showToast("Photo saved to gallery.", longDuration = true)
                is CaptureResult.Failure -> {
                    Log.e(TAG, "capture", result.throwable)
                    showToast(result.message, longDuration = true)
                }
            }
            isCapturing = false
            setCaptureUiBusy(false)
        }
    }

    private fun setCaptureUiBusy(busy: Boolean) {
        val alphaValue = if (busy) 0.45f else 1f
        binding.ifvCapture.alpha = alphaValue
        binding.ifvRotate.alpha = alphaValue
        binding.ifvFilters.alpha = alphaValue

        binding.ifvCapture.isEnabled = !busy
        binding.ifvRotate.isEnabled = !busy
        binding.ifvFilters.isEnabled = !busy
    }

    private fun toggleCamera() {
        if (binding.cameraView.isTakingPicture) return
        when (binding.cameraView.toggleFacing()) {
            Facing.BACK -> showToast("Switched to back camera!")
            Facing.FRONT -> showToast("Switched to front camera!")
        }
    }

    private fun changeFilter() {
        if (binding.cameraView.preview != Preview.GL_SURFACE) {
            showToast("Filters are supported only when preview is Preview.GL_SURFACE.")
            return
        }
        currentFilter = if (currentFilter < allFilters.size - 1) currentFilter + 1 else 0
        val filter = allFilters[currentFilter]
        binding.mtvFilterName.text = filter.toString()
        binding.cameraView.filter = filter.newInstance()
    }

    override fun onDestroy() {
        binding.cameraView.removeCameraListener(errorListener)
        super.onDestroy()
    }

    companion object {
        private const val TAG = "CameraViewActivity"
    }
}