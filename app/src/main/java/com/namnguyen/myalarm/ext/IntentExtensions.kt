package com.namnguyen.myalarm.ext

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

fun Context.makeVibratePhone(milliseconds: Long = 100L) {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (vibrator.hasVibrator()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    milliseconds,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vibrator.vibrate(milliseconds)
        }
    }
}