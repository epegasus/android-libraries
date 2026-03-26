package com.sohaib.wheelview.helper

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.core.content.ContextCompat

internal object ZeroSnapHaptics {

    fun feedback(view: View) {
        view.isHapticFeedbackEnabled = true

        val vibrator = ContextCompat.getSystemService(view.context, Vibrator::class.java)
        if (vibrator == null) {
            fallbackViewHaptic(view)
            return
        }
        val canVibrate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            true
        } else {
            @Suppress("DEPRECATION")
            vibrator.hasVibrator()
        }
        if (!canVibrate) {
            fallbackViewHaptic(view)
            return
        }

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        DURATION_MS,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }
            else -> {
                @Suppress("DEPRECATION")
                vibrator.vibrate(DURATION_MS)
            }
        }
    }

    private fun fallbackViewHaptic(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(
                HapticFeedbackConstants.CONFIRM,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
        } else {
            @Suppress("DEPRECATION")
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    private const val DURATION_MS = 55L
}
