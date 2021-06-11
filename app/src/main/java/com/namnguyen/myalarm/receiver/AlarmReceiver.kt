package com.namnguyen.myalarm.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import com.namnguyen.myalarm.MainActivity
import com.namnguyen.myalarm.R
import com.namnguyen.myalarm.ext.makeVibratePhone
import com.namnguyen.myalarm.ext.showNotification
import com.namnguyen.myalarm.service.AlarmService
import com.namnguyen.myalarm.util.Constants
import com.namnguyen.myalarm.util.DateTimeUtils.formatDate
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val timeIntMillis = intent.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME, 0)
        when (intent.action) {
            Constants.ACTION_SET_EXACT_ALARM -> {
                buildNotification(context, "Exact Time", timeIntMillis.formatDate())
            }
            Constants.ACTION_SET_REPETITIVE_ALARM -> {
                setRepetitiveAlarm(AlarmService(context))
                buildNotification(context, "Repetitive Time", timeIntMillis.formatDate())
            }
        }
        context.makeVibratePhone(300)
        playRingBell(context)
    }

    private fun buildNotification(context: Context, title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java)
        val snoozePendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        context.showNotification(
            title = title,
            content = "I got triggered at - $message",
            pendingIntent = snoozePendingIntent
        )
    }

    private fun setRepetitiveAlarm(alarmService: AlarmService) {
        val cal = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis + TimeUnit.DAYS.toMillis(7)
        }
        alarmService.setRepetitiveAlarm(cal.timeInMillis)
    }

    private var mediaPlayer: MediaPlayer? = null
    private fun playRingBell(context: Context) {
        try {
            val bell = mediaPlayer ?: run {
                MediaPlayer.create(context, R.raw.mp3_found).also { mediaPlayer = it }
            }
            bell.start()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}