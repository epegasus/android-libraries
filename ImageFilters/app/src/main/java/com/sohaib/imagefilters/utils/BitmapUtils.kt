package com.sohaib.imagefilters.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

/**
 * @Author: SOHAIB AHMED
 * @Date: 8/30/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

object BitmapUtils {

    fun getBitmap(context: Context, @DrawableRes imageId: Int): Bitmap? {
        val drawable: Drawable? = ContextCompat.getDrawable(context, imageId)
        drawable?.let {
            // Get intrinsic width and height of the drawable
            val width = it.intrinsicWidth
            val height = it.intrinsicHeight

            // Create a blank bitmap with the same dimensions
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            // Create a canvas to draw the drawable onto the bitmap
            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, canvas.width, canvas.height)
            it.draw(canvas)

            return bitmap
        }
        return null
    }

}