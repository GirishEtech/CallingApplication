package com.example.testapp

import android.content.Intent
import android.os.Bundle
import android.telecom.Call
import android.telecom.Call.Details
import android.telecom.CallAudioState
import android.telecom.InCallService
import android.util.Log


class MyInCallService : InCallService() {

    val TAG = "MyInCallService"
    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)
        Log.i(TAG, "onCallAdded: Call Added ")
        Log.i(TAG, "onCallAdded: CallerName :${call!!.details.callerDisplayName}")
        Log.i(TAG, "onCallAdded:Call Extras ${call.details.extras}")
        Log.i(TAG, "onCallAdded: Gateway info ${call.details.gatewayInfo}")
        
        val details = call.details
        val callDirection = details.callDirection
        when (callDirection) {
            Details.DIRECTION_OUTGOING -> {
                OutGoingCallActivity.call = call
                val intent = Intent(this, OutGoingCallActivity::class.java)
                intent.putExtra("NAME", call.details.callerDisplayName)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            Details.DIRECTION_INCOMING -> {
                val intent = Intent(this, IncomingCallActivity::class.java)
                intent.putExtra("NAME", call.details.callerDisplayName)
                IncomingCallActivity.call = call
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
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
        Log.i(
            TAG,
            "onConnectionEvent: call Object :$call \n Event is :$event \n Extras is :$extras"
        )
        super.onConnectionEvent(call, event, extras)

    }

    override fun onCallAudioStateChanged(audioState: CallAudioState?) {
        super.onCallAudioStateChanged(audioState)
    }
}