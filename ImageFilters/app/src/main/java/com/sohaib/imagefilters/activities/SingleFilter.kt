package com.sohaib.imagefilters.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider
import com.sohaib.imagefilters.R
import com.sohaib.imagefilters.databinding.ActivitySingleFilterBinding
import com.sohaib.imagefilters.utils.BitmapUtils
import com.sohaib.imagefilters.utils.Extensions.showToast

class SingleFilter : AppCompatActivity() {

    private val binding by lazy { ActivitySingleFilterBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()

        binding.slider.addOnChangeListener(sliderChangeListener)
        binding.mbExport.setOnClickListener { onSubmitClick() }
    }

    private fun initView() {
        val ruleString = "@adjust brightness -0.54 @adjust contrast 1.12"
        val bitmap = BitmapUtils.getBitmap(this, R.drawable.img_dummy_2)
        bitmap?.let {
            binding.svProcessing.setSurfaceCreatedCallback {
                binding.svProcessing.setImageBitmap(it)
                binding.svProcessing.setFilterWithConfig(ruleString)
                binding.svProcessing.setFilterIntensity(0.5f)
            }
        } ?: showToast("Bitmap is null")
    }

    private val sliderChangeListener = Slider.OnChangeListener { _, value, _ ->
        binding.svProcessing.setFilterIntensity(value)
    }

    private fun onSubmitClick() {
        binding.svProcessing.getResultBitmap {
            runOnUiThread {
                binding.sivResult.setImageBitmap(it)
            }
        }
    }
}