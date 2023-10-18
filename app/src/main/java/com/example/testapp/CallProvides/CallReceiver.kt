package com.example.testapp.CallProvides

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.testapp.Activities.IncomingCallActivity


class CallReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context?, intent: Intent?) {
        val telephonyManager =
            context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val callState = telephonyManager.callState
        if (callState == TelephonyManager.CALL_STATE_RINGING) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val callerNumber = telephonyManager.line1Number

                val activityIntent = Intent(context, IncomingCallActivity::class.java)
                activityIntent.putExtra("callerNumber", callerNumber)
                activityIntent.putExtra("callerName", "DEMO")
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    activityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                val manager = CallManager(context)
                //manager.startIncomingCall()
                intent!!.putExtra("pendingIntent", pendingIntent)
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Permission is not Granted", Toast.LENGTH_SHORT).show()
            }
        }

    }
}