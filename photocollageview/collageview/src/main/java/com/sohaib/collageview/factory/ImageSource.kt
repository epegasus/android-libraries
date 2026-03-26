package com.sohaib.collageview.factory

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.DrawableRes

sealed class ImageSource {
    data class UriSource(val uri: Uri?) : ImageSource()
    data class BitmapSource(val bitmap: Bitmap?) : ImageSource()
    data class DrawableSource(val drawable: Drawable?) : ImageSource()
    data class ResourceSource(@param:DrawableRes val resId: Int) : ImageSource()
}

