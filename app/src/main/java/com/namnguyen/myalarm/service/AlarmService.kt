package com.namnguyen.myalarm.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.namnguyen.myalarm.receiver.AlarmReceiver
import com.namnguyen.myalarm.util.Constants
import com.namnguyen.myalarm.util.RandomInt

class AlarmService(private val context: Context) {

    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

    private lateinit var mPendingIntent: PendingIntent

    // time in milliseconds
    fun setExactAlarm(time: Long) {
        mPendingIntent = getPendingIntent(
            getIntent().apply {
                action = Constants.ACTION_SET_EXACT_ALARM
                putExtra(Constants.EXTRA_EXACT_ALARM_TIME, time)
            }
        )
        setAlarm(time, mPendingIntent)
    }

    // time in milliseconds. every week
    fun setRepetitiveAlarm(time: Long) {
        mPendingIntent = getPendingIntent(
            getIntent().apply {
                action = Constants.ACTION_SET_REPETITIVE_ALARM
                putExtra(Constants.EXTRA_EXACT_ALARM_TIME, time)
            }
        )
        setAlarm(time, mPendingIntent)
    }

    fun resetAlarm() {
        alarmManager?.cancel(mPendingIntent)
    }

    // time in millisecond
    private fun setAlarm(time: Long, pendingIntent: PendingIntent) {
        alarmManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
            } else {
                it.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
            }
        }
    }

    private fun getIntent() = Intent(context, AlarmReceiver::class.java)

    private fun getPendingIntent(intent: Intent): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            RandomInt.getRandomInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
}