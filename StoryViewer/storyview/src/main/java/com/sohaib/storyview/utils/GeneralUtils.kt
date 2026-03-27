package com.sohaib.storyview.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper


/**
 * Created by Sohaib Ahmed on 04/04/2023.
 * github -> https://github.com/epegasus
 * linked-in -> https://www.linkedin.com/in/epegasus
 */

object GeneralUtils {

    fun Context.getActivity(): Activity? {
        return when (this) {
            is Activity -> this
            is ContextWrapper -> this.baseContext.getActivity()
            else -> null
        }
    }
}