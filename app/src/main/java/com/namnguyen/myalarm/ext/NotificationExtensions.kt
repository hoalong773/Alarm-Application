package com.namnguyen.myalarm.ext

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.namnguyen.myalarm.R
import com.namnguyen.myalarm.util.Constants
import com.namnguyen.myalarm.util.RandomInt

fun Context?.showNotification(
    notificationId: Int = RandomInt.getRandomInt(),
    title: String? = null,
    content: String? = null,
    subText: String? = null,
    pendingIntent: PendingIntent,
    largeIcon: Bitmap? = null,
    bigPicture: Bitmap? = null,
    priority: Int = getDefaultPriority()
) {
    this ?: return
    val channelId = Constants.CHANNEL_ID
    val builder: NotificationCompat.Builder = createNotificationBuilder(
        context = this,
        channelId = channelId,
        title = title,
        content = content,
        subText = subText,
        pendingIntent = pendingIntent,
        largeIcon = largeIcon,
        bigPicture = bigPicture,
        priority = priority
    )
    val notificationManager = getNotificationManager()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = notificationManager.getNotificationChannel(channelId)
        if (channel == null) createDefaultNotificationChannel()
    }
    notificationManager.notify(notificationId, builder.build())
}

fun getDefaultPriority(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationManager.IMPORTANCE_HIGH
    } else NotificationCompat.PRIORITY_HIGH
}

fun createNotificationBuilder(
    context: Context,
    channelId: String,
    title: String? = null,
    content: String? = null,
    subText: String? = null,
    pendingIntent: PendingIntent,
    largeIcon: Bitmap? = null,
    bigPicture: Bitmap? = null,
    priority: Int
): NotificationCompat.Builder {
    return NotificationCompat.Builder(context, channelId).apply {
        setSmallIcon(R.drawable.ic_calendar)
        color = ContextCompat.getColor(context, R.color.teal_200)
        setContentTitle(title)
        setContentText(content)
        setSubText(subText)
        setAutoCancel(true)
        setLargeIcon(largeIcon)
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setPriority(priority)
        setContentIntent(pendingIntent)
        setWhen(System.currentTimeMillis())
        setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +
                context.packageName + "/" + R.raw.mp3_found))

        if (bigPicture != null) {
            setStyle(createBigPictureStyle(bigPicture, title, content))
        }
    }
}

private fun createBigPictureStyle(
    bigPicture: Bitmap,
    title: String? = null,
    content: String? = null
) = NotificationCompat.BigPictureStyle()
    .bigPicture(bigPicture)
    .setBigContentTitle(title)
    .setSummaryText(content)


fun Context.createDefaultNotificationChannel(): String {

    val channelId = Constants.CHANNEL_ID

    // NotificationChannels are required for Notifications on O (API 26) and above.
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        val channelName: CharSequence =  Constants.CHANNEL_NAME
        val channelDescription: String =  Constants.CHANNEL_DESCRIPTION
        val channelImportance: Int = NotificationManager.IMPORTANCE_HIGH
        val channelEnableVibrate = true
        val channelLockScreenVisibility: Int = NotificationCompat.VISIBILITY_PRIVATE

        // Initializes NotificationChannel.
        val notificationChannel =
            NotificationChannel(channelId, channelName, channelImportance).apply {
                description = channelDescription
                enableVibration(channelEnableVibrate)
                lockscreenVisibility = channelLockScreenVisibility
            }

        val notificationManager = getNotificationManager()
        notificationManager.createNotificationChannel(notificationChannel)
        channelId
    } else {
        channelId
    }
}

fun Context.getNotificationManager(): NotificationManagerCompat {
    return NotificationManagerCompat.from(this)
}
