package com.sohaib.storyview.progress

import android.content.Context
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.Transformation
import android.widget.FrameLayout
import com.sohaib.storyview.R
import com.sohaib.storyview.databinding.PausableProgressBinding
import com.sohaib.storyview.listeners.ProgressListener

/**
 * Created by Sohaib Ahmed on 02/04/2023.
 * github -> https://github.com/epegasus
 * linked-in -> https://www.linkedin.com/in/epegasus
 */

class PausableProgressBar(context: Context) : FrameLayout(context) {

    private val defaultProgressDuration = 2000
    private var duration = defaultProgressDuration.toLong()

    private val binding: PausableProgressBinding
    private var animation: PausableScaleAnimation? = null
    private var progressListener: ProgressListener? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.pausable_progress, this)
        binding = PausableProgressBinding.bind(view)
    }

    fun setDuration(duration: Long) {
        this.duration = duration
    }

    fun setCallback(progressListener: ProgressListener) {
        this.progressListener = progressListener
    }

    fun setMax() {
        finishProgress(true)
    }

    fun setMin() {
        finishProgress(false)
    }

    fun setMinWithoutCallback() {
        binding.maxProgress.setBackgroundResource(R.color.progress_secondary)
        binding.maxProgress.visibility = VISIBLE
        animation?.let {
            it.setAnimationListener(null)
            it.cancel()
        }
    }

    fun setMaxWithoutCallback() {
        binding.maxProgress.setBackgroundResource(R.color.progress_max_active)
        binding.maxProgress.visibility = VISIBLE
        animation?.let {
            it.setAnimationListener(null)
            it.cancel()
        }
    }

    private fun finishProgress(isMax: Boolean) {
        if (isMax) binding.maxProgress.setBackgroundResource(R.color.progress_max_active)
        binding.maxProgress.visibility = if (isMax) VISIBLE else GONE
        animation?.let {
            it.setAnimationListener(null)
            it.cancel()
            progressListener?.onFinishProgress()
        }
    }

    fun startProgress() {
        binding.maxProgress.visibility = GONE
        animation = PausableScaleAnimation(0f, 1f, 1f, 1f, Animation.ABSOLUTE, 0f, Animation.RELATIVE_TO_SELF, 0f)
        animation?.duration = duration
        animation?.interpolator = LinearInterpolator()
        animation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationStart(animation: Animation) {
                binding.frontProgress.visibility = VISIBLE
                progressListener?.onStartProgress()
            }

            override fun onAnimationEnd(animation: Animation) {
                progressListener?.onFinishProgress()
            }
        })
        animation?.fillAfter = true
        binding.frontProgress.startAnimation(animation)
    }

    fun pauseProgress() = animation?.pause()

    fun resumeProgress() = animation?.resume()

    fun clear() {
        animation?.let {
            it.setAnimationListener(null)
            it.cancel()
            null
        }
    }

    private class PausableScaleAnimation(
        fromX: Float, toX: Float, fromY: Float, toY: Float, pivotXType: Int, pivotXValue: Float, pivotYType: Int, pivotYValue: Float
    ) : ScaleAnimation(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType, pivotYValue) {

        private var elapsedAtPause: Long = 0
        private var isPaused = false

        override fun getTransformation(currentTime: Long, outTransformation: Transformation, scale: Float): Boolean {
            if (isPaused && elapsedAtPause == 0L) {
                elapsedAtPause = currentTime - startTime
            }
            if (isPaused) {
                startTime = currentTime - elapsedAtPause
            }
            return super.getTransformation(currentTime, outTransformation, scale)
        }

        /***
         * pause animation
         */
        fun pause() {
            if (isPaused) return
            elapsedAtPause = 0
            isPaused = true
        }

        /***
         * resume animation
         */
        fun resume() {
            isPaused = false
        }
    }
}