package com.example.testapp.CallProvides

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.telecom.Call
import android.telecom.CallEndpoint
import android.telecom.InCallService
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.testapp.PreferenceManager
import com.example.testapp.reciever.PhoneCallReceiver


class MyInCallService : InCallService() {


    private var preferenceManager: PreferenceManager? = null
    private var list = ArrayList<Call>()
    val TAG = "MyInCallService"
    var callObject: CallObject? = null

    companion object {
        var INSTANCE: MyInCallService? = null
        var receiver = PhoneCallReceiver()
    }

    var intentFilter: IntentFilter? = null
    override fun onCreate() {
        super.onCreate()
        intentFilter = IntentFilter()
        intentFilter!!.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
    }

    @SuppressLint(
        "RemoteViewLayout", "NotificationPermission", "MissingPermission",
        "UnspecifiedRegisterReceiverFlag"
    )
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)
        getSystemService(TelecomManager::class.java).cancelMissedCallsNotification()
        preferenceManager = PreferenceManager(this)

        INSTANCE = this
        Log.i(TAG, "onCallAdded: Call Added ")
        Log.i(TAG, "onCallAdded:Call Extras ${call!!.details.extras}")
        Log.i(TAG, "onCallAdded: Gateway info ${call.details.gatewayInfo}")
        list.add(call)
        if (call.details.callDirection == Call.Details.DIRECTION_INCOMING) {
            if (!preferenceManager!!.getStatus()) {
                registerReceiver(receiver, intentFilter)
                Toast.makeText(this, "RECEIVER IS REGISTERED", Toast.LENGTH_SHORT).show()
                preferenceManager!!.putIsRegister(true)
            }
        } else if (call.details.callDirection == Call.Details.DIRECTION_OUTGOING) {
            if (preferenceManager!!.getIsregister()) {
                unregisterReceiver(receiver)
                preferenceManager!!.putIsRegister(false)
            }
        }
        callObject = CallObject(this)
        callObject!!.updateCall(call)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand: $intent")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onAvailableCallEndpointsChanged(availableEndpoints: MutableList<CallEndpoint>) {
        super.onAvailableCallEndpointsChanged(availableEndpoints)
    }

    override fun onConnectionEvent(call: Call?, event: String?, extras: Bundle?) {
        super.onConnectionEvent(call, event, extras)
        Log.i(
            TAG,
            "onConnectionEvent: call Object :$call \n Event is :$event \n Extras is :$extras"
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCallRemoved(call: Call?) {
        super.onCallRemoved(call)
    }
}