package com.sohaib.collageview.demo.model

import androidx.annotation.DrawableRes
import com.sohaib.collageview.factory.CollageViewFactory

data class CollageShapeItem(
    @DrawableRes val iconRes: Int,
    val layoutType: CollageViewFactory.CollageLayoutType
)

