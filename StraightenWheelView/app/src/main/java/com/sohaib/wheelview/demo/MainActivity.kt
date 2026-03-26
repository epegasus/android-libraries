package com.sohaib.wheelview.demo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.sohaib.wheelview.StraightenWheelView
import com.sohaib.wheelview.demo.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        initWheelView()

        binding.wheelView.setListener(wheelListener)
    }

    /**
     * One full internal turn (2π rad from end lock range) maps to this many degrees on the readout.
     * With default end lock, travel is ±2π rad → here 22.5° per π rad, so about -45° … +45°.
     */
    private fun initWheelView() {
        binding.wheelView.totalSpinnerRotation = 30f
    }

    /** Receives wheel position after each change; degreesAngle is the scaled value you show in UI. */
    private val wheelListener = object : StraightenWheelView.Listener() {
        override fun onRotationChanged(radians: Double, degreesAngle: Double) {
            updateUi(degreesAngle)
        }
    }

    /** Updates the angle label and demo image rotation to match the wheel’s current degrees. */
    private fun updateUi(degreesAngle: Double) {
        val text = String.Companion.format(Locale.US, "%.0f°", degreesAngle)
        binding.mtvAngle.text = text

        val angle: Float = degreesAngle.toFloat()
        binding.sivImage.rotation = angle
    }
}