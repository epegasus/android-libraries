package com.sohaib.regret.demo.utils

import android.view.View

fun View.animatePulse() {
    this.animate().cancel()
    this.animate()
        .scaleX(1.02f)
        .scaleY(1.02f)
        .setDuration(120)
        .withEndAction {
            this.animate().cancel()
            this.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(120)
                .start()
        }
        .start()
}