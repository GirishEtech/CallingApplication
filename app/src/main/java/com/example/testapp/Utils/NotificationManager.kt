package com.example.testapp.Utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.testapp.Activities.MainActivity
import com.example.testapp.Activities.OutGoingCallActivity
import com.example.testapp.R

class NotificationManager(val context: Context) {
    var notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notificationID = 1

    @SuppressLint("MissingPermission")
    fun createNotification(callerName: String) {
        val actionIntent1 = Intent(context, OutGoingCallActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            action = "${context.packageName}.ANSWER"
        }
        val pendingIntent1 =
            PendingIntent.getActivity(context, 0, actionIntent1, PendingIntent.FLAG_MUTABLE)
        val actionIntent2 = Intent(context, MainActivity::class.java).apply {
            action = "${context.packageName}.DECLINE"
        }
        val pendingIntent2 =
            PendingIntent.getActivity(context, 1, actionIntent2, PendingIntent.FLAG_MUTABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "my_channel_id"
            val channelName = "My Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
            val builder = NotificationCompat.Builder(context, "my_channel_id")
                .setSmallIcon(R.drawable.ic_call)
                .setContentTitle("$callerName")
                .setContentText(
                    "Incoming Call"
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.ic_call, "Answer", pendingIntent1)
                .addAction(R.drawable.ic_call, "Decline", pendingIntent2)

            with(NotificationManagerCompat.from(context)) {
                notify(notificationID, builder.build())
            }

        }
    }

    fun dismiss() {
        notificationManager.cancel(notificationID)
    }
}