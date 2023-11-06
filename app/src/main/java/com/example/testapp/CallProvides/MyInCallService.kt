package com.example.testapp.CallProvides

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telecom.Call
import android.telecom.CallEndpoint
import android.telecom.InCallService
import android.util.Log
import androidx.annotation.RequiresApi


class MyInCallService : InCallService() {


    private var list = ArrayList<Call>()
    val TAG = "MyInCallService"
    var callManger: CallObject? = null

    companion object {
        var INSTANCE: MyInCallService? = null
    }


    @SuppressLint("RemoteViewLayout", "NotificationPermission", "MissingPermission")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)
        Log.i(TAG, "onCallAdded: ALL CALLS :$calls \n")
        INSTANCE = this
        Log.i(TAG, "onCallAdded: Call Added ")
        Log.i(TAG, "onCallAdded:Call Extras ${call!!.details.extras}")
        Log.i(TAG, "onCallAdded: Gateway info ${call.details.gatewayInfo}")
        list.add(call)
        callManger = CallObject(this)
        callManger!!.updateCall(call)
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

}