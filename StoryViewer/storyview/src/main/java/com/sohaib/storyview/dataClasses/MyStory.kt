package com.sohaib.storyview.dataClasses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Created by Sohaib Ahmed on 02/04/2023.
 * github -> https://github.com/epegasus
 * linked-in -> https://www.linkedin.com/in/epegasus
 */

@Parcelize
data class MyStory(
    val url: String? = null,
    val date: Date? = null,
    val description: String? = null
) : Parcelable
