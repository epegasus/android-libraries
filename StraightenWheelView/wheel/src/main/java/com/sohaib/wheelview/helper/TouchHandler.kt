package com.sohaib.wheelview.helper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import com.sohaib.wheelview.StraightenWheelView
import kotlin.math.abs
import kotlin.math.roundToInt

internal class TouchHandler(private val view: StraightenWheelView) : SimpleOnGestureListener() {

    private var settlingFlingAnimator: ValueAnimator? = null

    private val gestureDetector: GestureDetector = GestureDetector(view.context, this)
    private var scrollState = StraightenWheelView.SCROLL_STATE_IDLE
    private var listener: StraightenWheelView.Listener? = null
    private var snapToMarks = false

    fun setListener(listener: StraightenWheelView.Listener?) {
        this.listener = listener
    }

    fun setSnapToMarks(snapToMarks: Boolean) {
        this.snapToMarks = snapToMarks
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        val action = event.actionMasked
        if (scrollState != StraightenWheelView.SCROLL_STATE_SETTLING &&
            (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)
        ) {
            if (snapToMarks) {
                val snapped = view.clampRadiansToTravelIfLocked(findNearestMarkAngle(view.radiansAngle))
                playSettlingAnimation(snapped.toFloat())
            } else {
                view.applyUserSnapToZeroAfterGesture()
                updateScrollStateIfRequired(StraightenWheelView.SCROLL_STATE_IDLE)
            }
        }
        return true
    }

    override fun onDown(e: MotionEvent): Boolean {
        cancelFling()
        return true
    }

    fun cancelFling() {
        settlingFlingAnimator?.cancel()
        settlingFlingAnimator = null
        if (scrollState == StraightenWheelView.SCROLL_STATE_SETTLING) {
            updateScrollStateIfRequired(StraightenWheelView.SCROLL_STATE_IDLE)
        }
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        val newAngle = view.radiansAngle + distanceX * SCROLL_ANGLE_MULTIPLIER
        view.setRadiansFromUserInteraction(newAngle)
        updateScrollStateIfRequired(StraightenWheelView.SCROLL_STATE_DRAGGING)
        return true
    }

    private fun updateScrollStateIfRequired(newState: Int) {
        if (listener != null && scrollState != newState) {
            scrollState = newState
        }
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        val rawEnd = view.radiansAngle - velocityX * FLING_ANGLE_MULTIPLIER
        val endRadians = if (snapToMarks) {
            findNearestMarkAngle(rawEnd)
        } else {
            rawEnd
        }
        val endAngle = view.clampRadiansToTravelIfLocked(endRadians).toFloat()
        playSettlingAnimation(endAngle)
        return true
    }

    private fun findNearestMarkAngle(angle: Double): Double {
        val step = 2 * Math.PI / view.marksCount
        return (angle / step).roundToInt() * step
    }

    private fun playSettlingAnimation(endAngle: Float) {
        updateScrollStateIfRequired(StraightenWheelView.SCROLL_STATE_SETTLING)
        settlingFlingAnimator?.cancel()
        val startAngle = view.radiansAngle.toFloat()
        val delta = abs(startAngle - endAngle)
        val durationMs = (delta * SETTLING_DURATION_MULTIPLIER).toLong()
            .coerceIn(MIN_SETTLING_DURATION_MS, MAX_SETTLING_DURATION_MS)
        val animator = ValueAnimator.ofFloat(startAngle, endAngle).setDuration(durationMs)
        animator.interpolator = INTERPOLATOR
        animator.addUpdateListener(flingAnimatorListener)
        animator.addListener(animatorListener)
        settlingFlingAnimator = animator
        animator.start()
    }

    private val flingAnimatorListener = AnimatorUpdateListener { animation ->
        val v = (animation.animatedValue as Float).toDouble()
        view.setRadiansFromUserInteraction(v)
    }

    private val animatorListener: Animator.AnimatorListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            settlingFlingAnimator = null
            view.applyUserSnapToZeroAfterGesture()
            updateScrollStateIfRequired(StraightenWheelView.SCROLL_STATE_IDLE)
        }

        override fun onAnimationCancel(animation: Animator) {
            settlingFlingAnimator = null
            view.applyUserSnapToZeroAfterGesture()
            updateScrollStateIfRequired(StraightenWheelView.SCROLL_STATE_IDLE)
        }
    }

    companion object {
        private const val SCROLL_ANGLE_MULTIPLIER = 0.008f
        private const val FLING_ANGLE_MULTIPLIER = 0.0002f
        private const val SETTLING_DURATION_MULTIPLIER = 1000
        private const val MIN_SETTLING_DURATION_MS = 50L
        private const val MAX_SETTLING_DURATION_MS = 1200L
        private val INTERPOLATOR: Interpolator = DecelerateInterpolator(2.5f)
    }
}
