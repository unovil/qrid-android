package com.unovil.tardyscan.service

import android.app.NotificationManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.unovil.tardyscan.R
import com.unovil.tardyscan.STUDENT_CHANNEL_ID
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@ExperimentalTime
class AppFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        val type = remoteMessage.data["type"]
        val timestamp = remoteMessage.data["timestamp_utc"]

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (type == "ATTENDANCE_RECORDED" && timestamp != null) {
            val timestampFormat = LocalDateTime.Format {
                monthName(MonthNames.ENGLISH_ABBREVIATED)
                chars(" ")
                day()
                chars(", ")
                year()

                chars(" at ")

                amPmHour(Padding.NONE)      // 1-12 instead of 0-23
                chars(":")
                minute()
                chars(":")
                second()
                chars(" ")
                amPmMarker("AM", "PM")
            }

            val currentTime = Instant.parse(timestamp.replace(" ", "T"))
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .format(timestampFormat)

            val notificationBuilder = NotificationCompat.Builder(this, STUDENT_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Attendance recorded!")
                .setContentText("You went in on $currentTime.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_STATUS)

            notificationManager.notify(100, notificationBuilder.build())
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    companion object {
        private const val TAG = "AppFirebaseMessagingService"
    }
}