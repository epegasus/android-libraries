package com.sohaib.floatingoverlay

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sohaib.floatingoverlay.databinding.ActivityMainBinding
import com.sohaib.floatingoverlay.databinding.ViewFloatingBinding
import com.sohaib.floatingoverlay.manager.OverlayManager

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val overlayManager = OverlayManager(this, this, activityResultRegistry)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fullScreen()

        binding.mbRequestPermissionMain.setOnClickListener { requestOverlayPermission() }
    }

    private fun fullScreen() {
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun requestOverlayPermission() {
        overlayManager.requestOverlayPermission { isGranted ->
            when (isGranted) {
                true -> inflateView()
                false -> Toast.makeText(this, "Permission not Granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun inflateView() {
        overlayManager.showOverlayView(ViewFloatingBinding.inflate(layoutInflater))
    }
}