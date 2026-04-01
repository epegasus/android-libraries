package com.sohaib.imagescroller

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import com.sohaib.imagescroller.interfaces.BitmapLoader
import java.util.Random
import kotlin.math.abs

class AutoImageScroller(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var bitmapLoader: BitmapLoader = object : BitmapLoader {
        override fun loadDrawable(context: Context, resourceId: Int): Bitmap? {
            val drawable = ContextCompat.getDrawable(context, resourceId)
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            }

            // Render any other kind of drawable to a bitmap
            drawable?.let {
                val bitmap = createBitmap(it.intrinsicWidth, it.intrinsicHeight)
                val canvas = Canvas(bitmap)
                it.setBounds(0, 0, canvas.width, canvas.height)
                it.draw(canvas)
                return bitmap
            }
            return null
        }
    }

    private var paint: Paint? = null

    private lateinit var bitmaps: List<Bitmap>

    /** Pixels per second  */
    private var speed = 0.0
    private var arrayIndex = 0
    private var maxBitmapHeight = 0

    private val clipBounds = Rect()
    private var offset = 0f

    private val nanosPerSec = 1e9

    /** Moment when the last call to onDraw() started  */
    private var lastFrameInstant: Long = -1
    private var frameTimeNanos: Long = -1

    private var isStarted = false
    private lateinit var scene: IntArray

    init {
        val typedArr = context.obtainStyledAttributes(attrs, R.styleable.AutoImageScroller, 0, 0)
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)

                val initialState: Int
                try {
                    speed = typedArr.getDimension(R.styleable.AutoImageScroller_speed, 60f).toDouble()
                    initialState = typedArr.getInt(R.styleable.AutoImageScroller_initialState, 0)
                    val sceneLength = typedArr.getInt(R.styleable.AutoImageScroller_sceneLength, 1000)
                    val randomnessResourceId = typedArr.getResourceId(R.styleable.AutoImageScroller_randomness, 0)
                    // When true, randomness is ignored and bitmaps are loaded in the order as they appear in the src array */
                    val contiguous = typedArr.getBoolean(R.styleable.AutoImageScroller_contiguous, false)

                    var randomness = IntArray(0)
                    if (randomnessResourceId > 0) {
                        randomness = resources.getIntArray(randomnessResourceId)
                    }
                    val type = if (isInEditMode) TypedValue.TYPE_STRING else typedArr.peekValue(R.styleable.AutoImageScroller_imageId).type

                    if (type == TypedValue.TYPE_REFERENCE) {
                        val resourceId = typedArr.getResourceId(R.styleable.AutoImageScroller_imageId, 0)
                        val typedArray = resources.obtainTypedArray(resourceId)
                        try {
                            var bitmapsSize = 0
                            for (r in randomness) {
                                bitmapsSize += r
                            }
                            bitmaps = ArrayList()
                            for (i in 0 until typedArray.length()) {
                                var multiplier = 1
                                if (randomness.isNotEmpty() && (i < randomness.size)) {
                                    multiplier = 1.coerceAtLeast(randomness[i])
                                }
                                val bitmap: Bitmap? = bitmapLoader.loadDrawable(getContext(), typedArray.getResourceId(i, 0))
                                bitmap?.let { bmp ->
                                    repeat((0 until multiplier).count()) {
                                        val scaledBitmap = scaleBitmap(bmp, height)
                                        (bitmaps as ArrayList<Bitmap>).add(scaledBitmap)
                                    }
                                    maxBitmapHeight = measuredHeight
                                }
                            }
                            val random = Random()
                            scene = IntArray(sceneLength)
                            for (i in scene.indices) {
                                if (contiguous) {
                                    scene[i] = i % bitmaps.size
                                } else {
                                    scene[i] = random.nextInt(bitmaps.size)
                                }
                            }
                        } finally {
                            typedArray.recycle()
                        }
                    } else if (type == TypedValue.TYPE_STRING) {
                        val bitmap: Bitmap? = bitmapLoader.loadDrawable(getContext(), typedArr.getResourceId(R.styleable.AutoImageScroller_imageId, 0))
                        bitmap?.let {
                            val scaledBitmap = scaleBitmap(it, height)
                            bitmaps = listOf(scaledBitmap)
                            scene = intArrayOf(0)
                            maxBitmapHeight = measuredHeight
                        } ?: run {
                            bitmaps = emptyList()
                        }
                    }
                } finally {
                    typedArr.recycle()
                }
                if (initialState == 0) {
                    start()
                }
            }
        })
    }

    fun scaleBitmap(bitmap: Bitmap, newHeight: Int): Bitmap {
        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val newWidth = (newHeight * aspectRatio).toInt()
        return bitmap.scale(newWidth, newHeight, false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        if (!isInEditMode) {
            if (lastFrameInstant == -1L) {
                lastFrameInstant = System.nanoTime()
            }
            frameTimeNanos = System.nanoTime() - lastFrameInstant
            lastFrameInstant = System.nanoTime()
            super.onDraw(canvas)
            if (bitmaps.isEmpty()) {
                return
            }
            canvas.getClipBounds(clipBounds)
            while (offset <= -getBitmap(arrayIndex).width) {
                offset += getBitmap(arrayIndex).width.toFloat()
                arrayIndex = (arrayIndex + 1) % scene.size
            }
            var left = offset
            var i = 0
            while (left < clipBounds.width()) {
                val bitmap = getBitmap((arrayIndex + i) % scene.size)
                val width = bitmap.width
                canvas.drawBitmap(bitmap, getBitmapLeft(width.toFloat(), left), 0f, paint)
                left += width.toFloat()
                i++
            }
            if (isStarted && speed != 0.0) {
                val temp = abs(speed)
                offset = (offset - temp / nanosPerSec * frameTimeNanos).toFloat()
                postInvalidateOnAnimation()
            }
        }
    }

    private fun getBitmap(sceneIndex: Int): Bitmap {
        return bitmaps[scene[sceneIndex]]
    }

    private fun getBitmapLeft(layerWidth: Float, left: Float): Float {
        return if (speed < 0) {
            clipBounds.width() - layerWidth - left
        } else {
            left
        }
    }

    /**
     * Start the animation
     */
    private fun start() {
        if (!isStarted) {
            isStarted = true
            lastFrameInstant = -1
            postInvalidateOnAnimation()
        }
    }

    /**
     * Stop the animation
     */
    @Suppress("unused")
    fun stop() {
        if (isStarted) {
            isStarted = false
            lastFrameInstant = -1
            invalidate()
        }
    }
}