package com.sohaib.storyview.utils

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.ceil

/**
 * Created by Sohaib Ahmed on 03/04/2023.
 * github -> https://github.com/epegasus
 * linked-in -> https://www.linkedin.com/in/epegasus
 */

class DynamicImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        drawable?.let {
            // ceil not round - avoid thin vertical gaps along the left/right edges
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = ceil((width * it.intrinsicHeight.toFloat() / it.intrinsicWidth).toDouble()).toInt()
            setMeasuredDimension(width, height)
        } ?: kotlin.run {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}