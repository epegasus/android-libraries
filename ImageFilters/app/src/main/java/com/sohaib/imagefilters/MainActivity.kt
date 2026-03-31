package com.sohaib.imagefilters

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sohaib.imagefilters.activities.MultipleFilters
import com.sohaib.imagefilters.activities.SingleFilter
import com.sohaib.imagefilters.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.mbSingleMain.setOnClickListener { startActivity(Intent(this, SingleFilter::class.java)) }
        binding.mbMultipleMain.setOnClickListener { startActivity(Intent(this, MultipleFilters::class.java)) }
    }
}