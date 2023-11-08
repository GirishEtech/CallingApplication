package com.example.testapp.reciever

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import com.example.callingapp.Utils.Utils
import com.example.testapp.CallProvides.MyInCallService
import com.example.testapp.PreferenceManager
import com.example.testapp.Utils.RingtoneManage

class PhoneCallReceiver : BroadcastReceiver() {

    @SuppressLint("SwitchIntDef")
    override fun onReceive(context: Context?, intent: Intent?) {
        val pref = PreferenceManager(context!!)
        val hand = android.os.Handler(Looper.getMainLooper())
        val manager = context.getSystemService(TelecomManager::class.java)
        if (intent!!.action === TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = context.getSystemService(TelephonyManager::class.java).callState
            when (state) {
                TelephonyManager.DATA_DISCONNECTED -> {
                    if (pref.isRinging() && !pref.getStatus()) {
                        Log.i("FLAG", "onReceive: FLAG VALUE ${pref.isRinging()}")
                        manager.cancelMissedCallsNotification()
                        hand.postDelayed({
                            Utils.deleteLastCallLogEntry(context, hand)
                        }, 500)
                        manager.cancelMissedCallsNotification()
                        val notificationManager: NotificationManager =
                            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        RingtoneManage.getInstance(context).StopRing()
                        notificationManager.cancelAll()
                        if (pref.getIsregister()) {
                            context.unregisterReceiver(MyInCallService.receiver)
                            pref.putIsRegister(false)
                        }
                    }
                    pref.PutRinging(false)
                }

                TelephonyManager.CALL_STATE_RINGING -> {
                    pref.PutRinging(true)
                    Toast.makeText(context, "is RINGING", Toast.LENGTH_SHORT).show()
                }

            }

        }
    }
}

