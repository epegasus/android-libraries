package com.sohaib.cropview.demo.ui.result

import com.sohaib.cropview.demo.ui.base.BaseActivity
import com.sohaib.cropview.demo.ui.crop.ActivityCrop
import com.sohaib.cropview.demo.databinding.ActivityResultBinding

class ActivityResult : BaseActivity<ActivityResultBinding>(ActivityResultBinding::inflate) {

    override fun onCreated() {
        setUI()

        binding.mbBackCrop.setOnClickListener { finish() }
    }

    private fun setUI() {
        binding.sivImageResult.setImageBitmap(ActivityCrop.Companion.bitmap)
    }
}