package com.sohaib.ratingbar.demo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sohaib.ratingbar.BaseRatingBar
import com.sohaib.ratingbar.demo.databinding.ActivityMainBinding
import com.sohaib.ratingbar.interfaces.OnRatingChangeListener

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.scaleRatingBar.setOnRatingChangeListener(rateChangeListener)
        binding.rotationRatingBar.setOnRatingChangeListener(rateChangeListener)
        binding.animationRatingBar.setOnRatingChangeListener(rateChangeListener)
    }

    private val rateChangeListener = object : OnRatingChangeListener {
        override fun onRatingChange(ratingBar: BaseRatingBar?, rating: Float, fromUser: Boolean) {
            Log.d("MyTag", "onRatingChange: Rating: $rating")
        }
    }
}