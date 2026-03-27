package com.sohaib.storyview.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.sohaib.storyview.listeners.OnTouchCallback
import com.sohaib.storyview.listeners.pull_dismiss_listener.OnPullDismissListener
import kotlin.math.abs

/**
 * Created by Sohaib Ahmed on 02/04/2023.
 * github -> https://github.com/epegasus
 * linked-in -> https://www.linkedin.com/in/epegasus
 */

class PullDismissLayout : FrameLayout {

    private var onPullDismissListener: OnPullDismissListener? = null
    private var onTouchCallback: OnTouchCallback? = null
    private var viewDragHelper: ViewDragHelper? = null

    private var verticalTouchSlop = 0f
    private var minFlingVelocity = 0f
    private var animateAlpha = false

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    private fun init(context: Context) {
        //if (!isInEditMode) {
            val viewConfiguration = ViewConfiguration.get(context)
            minFlingVelocity = viewConfiguration.scaledMinimumFlingVelocity.toFloat()
            viewDragHelper = ViewDragHelper.create(this, ViewDragCallback(this))
        //}
    }

    override fun computeScroll() {
        super.computeScroll()
        if (viewDragHelper != null && viewDragHelper!!.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val action = MotionEventCompat.getActionMasked(event)
        var pullingDown = false

        viewDragHelper?.let { viewDragHelper ->
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    verticalTouchSlop = event.y
                    val dy = event.y - verticalTouchSlop
                    if (dy > viewDragHelper.touchSlop) {
                        pullingDown = true
                        onTouchCallback?.touchPull()
                    } else {
                        onTouchCallback?.touchDown(event.x, event.y)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val dy = event.y - verticalTouchSlop
                    if (dy > viewDragHelper.touchSlop) {
                        pullingDown = true
                        onTouchCallback?.touchPull()
                    } else {
                        onTouchCallback?.touchDown(event.x, event.y)
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    verticalTouchSlop = 0.0f
                    onTouchCallback?.touchUp()
                }
            }
            onPullDismissListener?.let { pullDismissListener ->
                if (!viewDragHelper.shouldInterceptTouchEvent(event) && pullingDown) {
                    if (viewDragHelper.viewDragState == ViewDragHelper.STATE_IDLE &&
                        viewDragHelper.checkTouchSlop(ViewDragHelper.DIRECTION_VERTICAL)
                    ) {
                        val child = getChildAt(0)
                        if (child != null && !pullDismissListener.onShouldInterceptTouchEvent()) {
                            viewDragHelper.captureChildView(child, event.getPointerId(0))
                            return viewDragHelper.viewDragState == ViewDragHelper.STATE_DRAGGING
                        }
                    }
                }
            }
        }
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewDragHelper?.processTouchEvent(event)
        return viewDragHelper?.capturedView != null
    }

    fun setMinFlingVelocity(velocity: Float) {
        minFlingVelocity = velocity
    }

    fun setAnimateAlpha(b: Boolean) {
        animateAlpha = b
    }

    fun setListener(l: OnPullDismissListener?) {
        onPullDismissListener = l
    }

    private class ViewDragCallback(private val pullDismissLayout: PullDismissLayout) : ViewDragHelper.Callback() {

        private var capturedView: View? = null
        private var dragPercent = 0.0f
        private var startTop = 0
        private var dismissed = false

        override fun tryCaptureView(view: View, i: Int): Boolean {
            return capturedView == null
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return if (top < 0) 0 else top
        }

        override fun onViewCaptured(view: View, activePointerId: Int) {
            capturedView = view
            startTop = view.top
            dragPercent = 0.0f
            dismissed = false
        }

        @SuppressLint("NewApi")
        override fun onViewPositionChanged(view: View, left: Int, top: Int, dx: Int, dy: Int) {
            val range = pullDismissLayout.height
            val moved = abs(top - startTop)
            if (range > 0) {
                dragPercent = moved.toFloat() / range.toFloat()
            }
            if (pullDismissLayout.animateAlpha) {
                view.alpha = 1.0f - dragPercent
                pullDismissLayout.invalidate()
            }
        }

        override fun onViewDragStateChanged(state: Int) {
            if (capturedView != null && dismissed && state == ViewDragHelper.STATE_IDLE) {
                pullDismissLayout.removeView(capturedView)
                if (pullDismissLayout.onPullDismissListener != null) {
                    pullDismissLayout.onPullDismissListener?.onDismissed()
                }
            }
        }

        override fun onViewReleased(view: View, xv: Float, yv: Float) {
            dismissed = dragPercent >= 0.50f || abs(xv) > pullDismissLayout.minFlingVelocity && dragPercent > 0.20f
            val finalTop = if (dismissed) pullDismissLayout.height else startTop
            if (!dismissed) {
                pullDismissLayout.getTouchCallbacks()?.touchUp()
            }
            pullDismissLayout.viewDragHelper?.settleCapturedViewAt(0, finalTop)
            pullDismissLayout.invalidate()
        }
    }

    fun setTouchCallbacks(onTouchListener: OnTouchCallback) {
        this.onTouchCallback = onTouchListener
    }

    fun getTouchCallbacks(): OnTouchCallback? {
        return onTouchCallback
    }
}