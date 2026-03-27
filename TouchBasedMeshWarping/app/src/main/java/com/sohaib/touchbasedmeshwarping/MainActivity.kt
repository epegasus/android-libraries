package com.sohaib.touchbasedmeshwarping

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sohaib.touchbasedmeshwarping.databinding.ActivityMainBinding
import com.sohaib.touchbasedmeshwarping.enums.DeformMode
import com.sohaib.touchbasedmeshwarping.manager.DeformManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.wysaid.nativePort.CGEDeformFilterWrapper
import org.wysaid.view.ImageGLSurfaceView
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private var deformManager: DeformManager? = null
    private var currentModeIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.glSurfaceView.setSurfaceCreatedCallback { onSurfaceCreated() }
        binding.glSurfaceView.setDisplayMode(ImageGLSurfaceView.DisplayMode.DISPLAY_ASPECT_FIT)

        binding.mbMode.setOnClickListener { onModeClick() }
        binding.mbRestore.setOnClickListener { deformManager?.restore() }
        binding.mbRadiusAdd.setOnClickListener { radiusIncClicked() }
        binding.mbRadiusMinus.setOnClickListener { radiusDecClicked() }
        binding.mbIntensityAdd.setOnClickListener { intensityIncClicked() }
        binding.mbIntensityMinus.setOnClickListener { intensityDecClicked() }
    }

    private fun onSurfaceCreated() {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.img_girl)
        binding.glSurfaceView.setImageBitmap(bitmap)

        binding.glSurfaceView.queueEvent {
            var width = bitmap?.width ?: 0
            var height = bitmap?.height ?: 0
            val scaling = (1280.0f / width).coerceAtMost(1280.0f / height)
            if (scaling < 1.0f) {
                width = (width * scaling).toInt()
                height = (height * scaling).toInt()
            }
            val deformWrapper = CGEDeformFilterWrapper.create(width, height, 10.0f)
            deformWrapper.setUndoSteps(200)

            val handler = binding.glSurfaceView.imageHandler
            handler.setFilterWithAddres(deformWrapper.nativeAddress)
            handler.processFilters()

            initDeform(deformWrapper)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initDeform(deformWrapper: CGEDeformFilterWrapper) {
        deformManager = DeformManager(binding.glSurfaceView, deformWrapper, binding.seekBar).also {
            binding.glSurfaceView.setOnTouchListener(it.onTouchListener())
            binding.seekBar.setOnSeekBarChangeListener(it.onSeekBarChangeListener())

            updateView()
        }
    }

    private fun updateView() {
        CoroutineScope(Dispatchers.Main).launch {
            val radius = formatFloat(deformManager?.touchRadius ?: 0f)
            val intensity = formatFloat(deformManager?.touchIntensity ?: 0f)

            binding.mtvRadius.text = getString(R.string.radius_200, radius)
            binding.mtvIntensity.text = getString(R.string.intensity_0_3, intensity)
        }
    }

    private fun formatFloat(value: Float): String {
        // Format the float value to 2 decimal places
        val formatted = String.format(Locale.US, "%.2f", value)

        // If the formatted value ends with .00, remove the decimals entirely
        return if (formatted.endsWith(".00")) {
            formatted.substring(0, formatted.indexOf("."))
        } else {
            formatted
        }
    }

    override fun onPause() {
        super.onPause()
        deformManager?.release()
        binding.glSurfaceView.release()
        binding.glSurfaceView.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.glSurfaceView.onResume()
    }

    private fun radiusIncClicked() {
        deformManager?.increaseRadius()
        updateView()
    }

    private fun radiusDecClicked() {
        deformManager?.decreaseRadius()
        updateView()
    }

    private fun intensityIncClicked() {
        deformManager?.increaseIntensity()
        updateView()
    }

    private fun intensityDecClicked() {
        deformManager?.decreaseIntensity()
        updateView()
    }

    fun undoBtnClicked() {
        deformManager?.undo()
    }

    fun redoBtnClicked() {
        deformManager?.redo()
    }

    fun saveImageBtnClicked() {
        binding.glSurfaceView.getResultBitmap { }
    }

    private fun onModeClick() {
        // Get all DeformMode values
        val modes = DeformMode.entries.toTypedArray()

        // Update the current mode index, cycling back to 0 if at the end
        currentModeIndex = (currentModeIndex + 1) % modes.size

        // Set the next mode
        deformManager?.setDeformMode(modes[currentModeIndex])
        binding.mbMode.text = modes[currentModeIndex].name
    }
}