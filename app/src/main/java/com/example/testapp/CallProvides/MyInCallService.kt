package com.example.testapp.CallProvides

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telecom.Call
import android.telecom.Call.Details
import android.telecom.InCallService
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.testapp.Activities.IncomingCallActivity
import com.example.testapp.Activities.MainActivity
import com.example.testapp.Activities.OutGoingCallActivity
import com.example.testapp.PreferenceManager
import com.example.testapp.R


class MyInCallService : InCallService() {

    val TAG = "MyInCallService"

    // declaring variables
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"


    @SuppressLint("RemoteViewLayout", "NotificationPermission", "MissingPermission")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)
        Log.i(TAG, "onCallAdded: Call Added ")
        Log.i(TAG, "onCallAdded: CallerName :${call!!.details.callerDisplayName}")
        Log.i(TAG, "onCallAdded:Call Extras ${call.details.extras}")
        Log.i(TAG, "onCallAdded: Gateway info ${call.details.gatewayInfo}")
        val preferenceManager = PreferenceManager(this)
        val isActive = preferenceManager.getStatus()
        val intent = Intent(this, OutGoingCallActivity::class.java)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        val details = call.details
        val callDirection = details.callDirection
        val contentView = RemoteViews(packageName, R.layout.activity_incoming_call)
        val actionIntent1 = Intent(this, OutGoingCallActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        actionIntent1.action = "ANSWER"
        val pendingIntent1 =
            PendingIntent.getActivity(this, 0, actionIntent1, PendingIntent.FLAG_MUTABLE)


        val actionIntent2 = Intent(this, MainActivity::class.java)
        actionIntent2.action = "DECLINE"
        val pendingIntent2 =
            PendingIntent.getActivity(this, 1, actionIntent2, PendingIntent.FLAG_MUTABLE)
        when (callDirection) {
            Details.DIRECTION_OUTGOING -> {
                OutGoingCallActivity.call = call
                val intent = Intent(this, OutGoingCallActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            Details.DIRECTION_INCOMING -> {
                Log.i(TAG, "onCallAdded: isActive :$isActive")
                if (isActive) {
                    val intent = Intent(this, IncomingCallActivity::class.java)
                    IncomingCallActivity.call = call
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        val channelId = "my_channel_id"
//                        val channelName = "My Channel"
//                        val importance = NotificationManager.IMPORTANCE_HIGH
//                        val channel = NotificationChannel(channelId, channelName, importance)
//                        val notificationManager = getSystemService(NotificationManager::class.java)
//                        notificationManager.createNotificationChannel(channel)
                    }
                    IncomingCallActivity.call = call
                    val builder = NotificationCompat.Builder(this, "my_channel_id")
                        .setSmallIcon(R.drawable.ic_call)
                        .setContentTitle("Ringing")
                        .setContentText(call.details.handle.schemeSpecificPart)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .addAction(R.drawable.ic_call, "Answer", pendingIntent1)
                        .addAction(R.drawable.ic_call, "Decline", pendingIntent2)
                        .setAutoCancel(true)

// Show the notificat
                    with(NotificationManagerCompat.from(this)) {
                        notify(1, builder.build())
                    }

                }
            }

            else -> {
                // This may represent other call types like self-managed or unknown
            }
        }


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand: $intent")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onConnectionEvent(call: Call?, event: String?, extras: Bundle?) {

        super.onConnectionEvent(call, event, extras)
        Log.i(
            TAG,
            "onConnectionEvent: call Object :$call \n Event is :$event \n Extras is :$extras"
        )
    }

}