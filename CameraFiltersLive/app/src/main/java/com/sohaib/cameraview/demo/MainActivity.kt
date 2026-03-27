package com.sohaib.cameraview.demo

import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sohaib.cameraview.demo.databinding.ActivityMainBinding
import com.sohaib.cameraview.demo.helper.showToast
import com.sohaib.cameraview.demo.ui.CameraViewActivity

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setPadding()
        checkForFlash()

        binding.mbContinue.setOnClickListener { onContinueClick() }
    }

    private fun setPadding() {
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun checkForFlash() {
        binding.mtvFlashStatus.setText(
            when (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                true -> R.string.flash_detected
                false -> R.string.no_flash_detected
            }
        )
    }

    private fun onContinueClick() {
        if (checkCameraHardware().not()) {
            showToast("No Camera Found")
            return
        }
        navigateScreen()
    }

    private fun checkCameraHardware(): Boolean {
        val cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        return cameraManager.cameraIdList.isNotEmpty()
    }

    private fun navigateScreen() {
        startActivity(Intent(this, CameraViewActivity::class.java))
        finish()
    }
}