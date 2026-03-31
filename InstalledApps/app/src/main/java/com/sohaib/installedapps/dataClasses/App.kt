package com.sohaib.installedapps.dataClasses

import android.graphics.drawable.Drawable

data class App(
    val id: Int,
    val icon: Drawable,
    val appName: String,
    val packageName: String
)
