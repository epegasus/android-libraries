package com.sohaib.collageview.views

import android.net.Uri

interface CollageView {

  fun imageCount(): Int

  fun setImageAt(index: Int, uri: Uri?)

  fun setImageAt(index: Int, resId: Int)
}