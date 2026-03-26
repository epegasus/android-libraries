package com.sohaib.regret.demo.helper

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.sohaib.regret.demo.R

/**
 * App-only helper utilities for the demo UI.
 *
 * It owns the demo "cycling" sequences (colors + typefaces) so MainActivity stays smaller.
 */
class DemoHelper(private val context: Context) {

    private val colorCycleRes = listOf(
        R.color.black,
        R.color.text_red,
        R.color.text_green,
        R.color.text_blue,
        R.color.text_purple
    )

    private val typefaceCycle = listOf(
        Typeface.DEFAULT,
        Typeface.SERIF,
        Typeface.SANS_SERIF,
        Typeface.MONOSPACE
    )

    fun cycleTextColor(textView: TextView): Int {
        val currentColor = textView.currentTextColor
        val currentIndex = colorCycleRes.indexOfFirst { resId ->
            ContextCompat.getColor(context, resId) == currentColor
        }
        val nextIndex = if (currentIndex == -1) 0 else (currentIndex + 1) % colorCycleRes.size
        return ContextCompat.getColor(context, colorCycleRes[nextIndex])
    }

    fun cycleTypeface(textView: TextView): Typeface {
        val currentTypeface = textView.typeface ?: Typeface.DEFAULT
        val currentIndex = typefaceCycle.indexOfFirst { it == currentTypeface }
        val nextIndex = if (currentIndex == -1) 0 else (currentIndex + 1) % typefaceCycle.size
        return typefaceCycle[nextIndex]
    }
}