package com.sohaib.storyview.utils

import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.palette.graphics.Palette
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

/**
 * Created by Sohaib Ahmed on 03/04/2023.
 * github -> https://github.com/epegasus
 * linked-in -> https://www.linkedin.com/in/epegasus
 */

class PaletteExtraction(view: View, resource: Bitmap?) {

    private val wrView: WeakReference<View>
    private val wrBitmap: WeakReference<Bitmap?>

    init {
        wrView = WeakReference(view)
        wrBitmap = WeakReference(resource)
    }

    fun execute() {
        CoroutineScope(Dispatchers.Default).launch {
            wrBitmap.get()?.let { bitmap ->
                wrView.get()?.let { view ->
                    val palette = Palette.from(bitmap).generate()
                    val drawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(palette.getDarkVibrantColor(0), palette.getLightMutedColor(0)))
                    drawable.cornerRadius = 0f
                    withContext(Dispatchers.Main) {
                        view.background = drawable
                    }
                }
            }
        }
    }
}