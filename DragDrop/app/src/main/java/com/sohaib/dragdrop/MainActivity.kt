package com.sohaib.dragdrop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sohaib.dragdrop.databinding.ActivityMainBinding
import com.sohaib.dragdrop.helper.DADHelper

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val dadHelper by lazy { DADHelper(this, "Done") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.sivImage.setOnLongClickListener { dadHelper.startDraggingView(it) }
        binding.tvText.setOnLongClickListener { dadHelper.startDraggingView(it) }
        binding.flSrc.setOnDragListener(dadHelper.dragListener)
        binding.flDst.setOnDragListener(dadHelper.dragListener)
    }
}