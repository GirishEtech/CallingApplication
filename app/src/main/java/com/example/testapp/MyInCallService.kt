package com.example.testapp

import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.InCallService
import android.util.Log

class MyInCallService :InCallService() {
    val TAG = "MyInCallService"
    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)
        Log.i(TAG, "onCallAdded: Call Added ")

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand: $intent")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onConnectionEvent(call: Call?, event: String?, extras: Bundle?) {
        Log.i(TAG, "onConnectionEvent: call Object :$call \n Event is :$event \n Extras is :$extras")
        super.onConnectionEvent(call, event, extras)

    }

    override fun onCallAudioStateChanged(audioState: CallAudioState?) {
        super.onCallAudioStateChanged(audioState)
    }
}