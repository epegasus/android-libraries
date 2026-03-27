package com.sohaib.storyview.dataClasses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Sohaib Ahmed on 03/04/2023.
 * github -> https://github.com/epegasus
 * linked-in -> https://www.linkedin.com/in/epegasus
 */

@Parcelize
data class HeaderInfo(
    var title: String? = null,
    var subtitle: String? = null,
    var titleIconUrl: String? = null
) : Parcelable