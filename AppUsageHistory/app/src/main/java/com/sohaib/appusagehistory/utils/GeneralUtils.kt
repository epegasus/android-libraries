package com.sohaib.appusagehistory.utils

import android.app.Activity
import android.widget.Toast

object GeneralUtils {

    fun Activity.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}