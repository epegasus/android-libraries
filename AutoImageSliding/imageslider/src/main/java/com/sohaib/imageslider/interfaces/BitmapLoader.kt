package com.sohaib.imageslider.interfaces

import android.content.Context
import android.graphics.Bitmap

interface BitmapLoader {
    fun loadDrawable(context: Context, resourceId: Int): Bitmap?
}