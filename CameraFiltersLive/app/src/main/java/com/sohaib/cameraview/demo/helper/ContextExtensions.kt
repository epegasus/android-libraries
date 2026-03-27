package com.sohaib.cameraview.demo.helper

import android.content.Context
import android.widget.Toast

fun Context.showToast(message: String, longDuration: Boolean = false) {
    val length = if (longDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    Toast.makeText(this, message, length).show()
}