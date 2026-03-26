package com.sohaib.wheelview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.withStyledAttributes
import com.sohaib.wheelview.helper.TouchHandler
import com.sohaib.wheelview.helper.Wheel
import com.sohaib.wheelview.helper.ZeroSnapHaptics
import com.sohaib.wheelview.utils.ConversionUtils.convertToPx
import kotlin.math.absoluteValue
import kotlin.math.min

class StraightenWheelView(context: Context?, attrs: AttributeSet) : View(context, attrs) {

    private val wheel = Wheel(this)
    private val touchHandler = TouchHandler(this)
    private var listener: Listener? = null
    private var onlyPositiveValues = false
    private var endLock = false
    private var snapToZero = true
    private var armZeroHaptic = true
    private var angle = 0.0
    private var totalSpinnerRotationDegrees = DEFAULT_TOTAL_SPINNER_ROTATION

    init {
        isHapticFeedbackEnabled = true
        readAttrs(attrs)
    }

    private fun readAttrs(attrs: AttributeSet) {
        context.withStyledAttributes(attrs, R.styleable.StraightenWheelView) {
            val marksCount = getInt(R.styleable.StraightenWheelView_sw_marksCount, DEFAULT_MARKS_COUNT)
            val normalColor = getColor(R.styleable.StraightenWheelView_sw_normalColor, DEFAULT_NORMAL_COLOR)
            val activeColor = getColor(R.styleable.StraightenWheelView_sw_activeColor, DEFAULT_ACTIVE_COLOR)
            val showActiveRange = getBoolean(R.styleable.StraightenWheelView_sw_showActiveRange, DEFAULT_SHOW_ACTIVE_RANGE)
            val snapToMarks = getBoolean(R.styleable.StraightenWheelView_sw_snapToMarks, DEFAULT_SNAP_TO_MARKS)
            wheel.setMarksCount(marksCount)
            wheel.setNormalColor(normalColor)
            wheel.setActiveColor(activeColor)
            wheel.setShowActiveRange(showActiveRange)
            touchHandler.setSnapToMarks(snapToMarks)
            endLock = getBoolean(R.styleable.StraightenWheelView_sw_endLock, DEFAULT_END_LOCK)
            onlyPositiveValues = getBoolean(R.styleable.StraightenWheelView_sw_onlyPositiveValues, DEFAULT_ONLY_POSITIVE_VALUES)
            snapToZero = getBoolean(R.styleable.StraightenWheelView_sw_snapToZero, DEFAULT_SNAP_TO_ZERO)
        }
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
        touchHandler.setListener(listener)
    }

    /**
     * When [endLock] is true, angle is a linear position in [-2π, 2π] (or [0, 2π] if only positive):
     * no modular wrap — ends clamp like -1 … 0 … +1 with hard borders.
     * When false, angle wraps every 2π (infinite spinner).
     */
    private fun applyRawAngle(raw: Double, allowZeroSnap: Boolean) {
        if (!raw.isFinite()) {
            return
        }
        if (!endLock) {
            angle = raw % TWO_PI
            if (onlyPositiveValues && angle < 0) {
                angle += TWO_PI
            }
        } else {
            val min = travelMinRadians()
            val max = travelMaxRadians()
            if (raw < min || raw > max) {
                touchHandler.cancelFling()
            }
            angle = raw.coerceIn(min, max)
        }
        if (allowZeroSnap) {
            applyUserSnapToZeroIfNeeded()
        }
    }

    private fun travelMinRadians(): Double =
        if (onlyPositiveValues) 0.0 else -TWO_PI

    private fun travelMaxRadians(): Double = TWO_PI

    /** Clamps to [-2π, 2π] or [0, 2π] when [endLock] is on; otherwise returns [raw]. */
    internal fun clampRadiansToTravelIfLocked(raw: Double): Double {
        if (!endLock || !raw.isFinite()) {
            return raw
        }
        return raw.coerceIn(travelMinRadians(), travelMaxRadians())
    }

    var radiansAngle: Double
        get() = angle
        set(radians) {
            armZeroHaptic = true
            applyRawAngle(radians, allowZeroSnap = false)
            invalidate()
            listener?.onRotationChanged(angle, degreesAngle)
        }

    var degreesAngle: Double
        get() = radiansAngle * totalSpinnerRotationDegrees / Math.PI
        set(degrees) {
            val radians = degrees * Math.PI / totalSpinnerRotationDegrees
            radiansAngle = radians
        }

    var completeTurnFraction: Double
        get() = radiansAngle / TWO_PI
        set(fraction) {
            val radians = fraction * TWO_PI
            radiansAngle = radians
        }

    var totalSpinnerRotation: Float
        get() = totalSpinnerRotationDegrees
        set(value) {
            totalSpinnerRotationDegrees = value
        }

    internal fun setRadiansFromUserInteraction(raw: Double) {
        applyRawAngle(raw, allowZeroSnap = true)
        invalidate()
        listener?.onRotationChanged(angle, degreesAngle)
    }

    internal fun applyUserSnapToZeroAfterGesture() {
        val before = angle
        applyUserSnapToZeroIfNeeded()
        if (before != angle) {
            invalidate()
            listener?.onRotationChanged(angle, degreesAngle)
        }
    }

    private fun applyUserSnapToZeroIfNeeded() {
        if (!snapToZero) {
            return
        }
        val threshold = zeroSnapThresholdRadians()
        val d = distanceToZeroForSnap(angle)
        if (d <= threshold) {
            val shouldHaptic = armZeroHaptic
            angle = 0.0
            if (shouldHaptic) {
                armZeroHaptic = false
                ZeroSnapHaptics.feedback(this)
            }
        } else {
            armZeroHaptic = true
        }
    }

    private fun zeroSnapThresholdRadians(): Double {
        val marks = wheel.getMarksCount().coerceAtLeast(1)
        val step = 2 * Math.PI / marks
        return min(step * ZERO_SNAP_FRACTION_OF_STEP, MAX_ZERO_SNAP_RAD)
    }

    /** Linear distance to 0 when clamped; shortest arc when spinning (no end lock). */
    private fun distanceToZeroForSnap(rad: Double): Double {
        if (endLock) {
            return rad.absoluteValue
        }
        val twoPi = TWO_PI
        val n = ((rad % twoPi) + twoPi) % twoPi
        return minOf(n, twoPi - n)
    }

    fun setOnlyPositiveValues(onlyPositiveValues: Boolean) {
        this.onlyPositiveValues = onlyPositiveValues
    }

    fun setEndLock(lock: Boolean) {
        endLock = lock
    }

    fun setSnapToZero(snap: Boolean) {
        snapToZero = snap
    }

    fun setShowActiveRange(show: Boolean) {
        wheel.setShowActiveRange(show)
        invalidate()
    }

    fun setNormalColor(color: Int) {
        wheel.setNormalColor(color)
        invalidate()
    }

    @Deprecated("Typo; use setNormalColor", ReplaceWith("setNormalColor(color)"))
    fun setNormaColor(color: Int) {
        setNormalColor(color)
    }

    fun setActiveColor(color: Int) {
        wheel.setActiveColor(color)
        invalidate()
    }

    fun setSnapToMarks(snapToMarks: Boolean) {
        touchHandler.setSnapToMarks(snapToMarks)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return touchHandler.onTouchEvent(event)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        wheel.onSizeChanged()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val resolvedWidthSpec = resolveMeasureSpec(widthMeasureSpec, DP_DEFAULT_WIDTH)
        val resolvedHeightSpec = resolveMeasureSpec(heightMeasureSpec, DP_DEFAULT_HEIGHT)
        super.onMeasure(resolvedWidthSpec, resolvedHeightSpec)
    }

    private fun resolveMeasureSpec(measureSpec: Int, dpDefault: Int): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        if (mode == MeasureSpec.EXACTLY) {
            return measureSpec
        }
        var defaultSize = convertToPx(dpDefault, resources)
        if (mode == MeasureSpec.AT_MOST) {
            defaultSize = defaultSize.coerceAtMost(MeasureSpec.getSize(measureSpec))
        }
        return MeasureSpec.makeMeasureSpec(defaultSize, MeasureSpec.EXACTLY)
    }

    override fun onDraw(canvas: Canvas) {
        wheel.onDraw(canvas)
    }

    var marksCount: Int
        get() = wheel.getMarksCount()
        set(marksCount) {
            wheel.setMarksCount(marksCount)
            invalidate()
        }

    open class Listener {
        open fun onRotationChanged(radians: Double, degreesAngle: Double) {}
    }

    companion object {
        private const val DP_DEFAULT_WIDTH = 200
        private const val DP_DEFAULT_HEIGHT = 32
        private const val DEFAULT_MARKS_COUNT = 90
        private const val DEFAULT_NORMAL_COLOR = -0x1
        private const val DEFAULT_ACTIVE_COLOR = -0xab5310
        private const val DEFAULT_SHOW_ACTIVE_RANGE = false
        private const val DEFAULT_SNAP_TO_MARKS = false
        private const val DEFAULT_END_LOCK = true
        private const val DEFAULT_ONLY_POSITIVE_VALUES = false
        private const val DEFAULT_SNAP_TO_ZERO = true
        private const val DEFAULT_TOTAL_SPINNER_ROTATION = 180f
        private const val ZERO_SNAP_FRACTION_OF_STEP = 0.45
        private const val MAX_ZERO_SNAP_RAD = 0.12
        private const val TWO_PI = 2 * Math.PI
        const val SCROLL_STATE_IDLE = 0
        const val SCROLL_STATE_DRAGGING = 1
        const val SCROLL_STATE_SETTLING = 2
    }
}
