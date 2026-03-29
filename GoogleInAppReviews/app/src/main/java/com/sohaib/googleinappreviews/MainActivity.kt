package com.sohaib.googleinappreviews

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.sohaib.googleinappreviews.databinding.ActivityMainBinding

const val TAG = "MyTag"

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val reviewManager by lazy {
        ReviewManagerFactory.create(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.mbFlowMain.setOnClickListener { initFlow() }
    }

    private fun initFlow() {
        reviewManager.requestReviewFlow().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                launchFlow(task.result)
            } else {
                val message = task.exception?.message
                Log.d(TAG, "initFlow: $message")
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchFlow(reviewInfo: ReviewInfo) {
        val flow = reviewManager.launchReviewFlow(this, reviewInfo)
        flow.addOnCompleteListener {
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.
            Toast.makeText(this, "Flow Completed", Toast.LENGTH_SHORT).show()
        }
    }
}