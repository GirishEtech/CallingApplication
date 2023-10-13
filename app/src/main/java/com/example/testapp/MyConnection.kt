package com.example.testapp

import android.content.Context
import android.content.Intent
import android.telecom.Connection
import android.telecom.DisconnectCause
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(34)
class MyConnection(context: Context) : Connection() {
    val TAG = "MyConnection"

    companion object {
        var MAINSTATE = -1
        fun getState(): Int {
            return MAINSTATE
        }
    }

    init {
        connectionProperties = PROPERTY_SELF_MANAGED
        connectionCapabilities = CAPABILITY_SUPPORT_HOLD and CAPABILITY_HOLD
        Log.i(TAG, "current Call State : $state")
        Log.i(TAG, "callerDisplayNamePresentation : $callerDisplayNamePresentation")
        Log.i(TAG, "callerNumberVerificationStatus :$callerNumberVerificationStatus ")
        context.startActivity(
            Intent(
                context,
                OutGoingCallActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun onStateChanged(state: Int) {
        super.onStateChanged(state)
        MAINSTATE = state
        when (state) {
            STATE_NEW -> {
                Log.d(TAG, "onStateChanged:New Call Instantiated  ")
            }

            STATE_INITIALIZING -> {
                Log.d(TAG, "onStateChanged:STATE_INITIALIZING ")
            }

            STATE_DISCONNECTED -> {
                Log.d(TAG, "onStateChanged:STATE_DISCONNECTED ")
            }

            STATE_RINGING -> {
                Log.d(TAG, "onStateChanged:STATE_RINGING ")
            }

            STATE_ACTIVE -> {
                Log.d(TAG, "onStateChanged:STATE_ACTIVE ")
            }

            STATE_DIALING -> {
                Log.d(TAG, "onStateChanged:STATE_DIALING ")
            }
        }
    }

    override fun onAnswer() {
        super.onAnswer()
        Log.i(TAG, "onAnswer: On Answer")
    }


    override fun onAbort() {
        super.onAbort()
        Log.i(TAG, "onAbort: onAbort is Called")
    }

    override fun onHold() {
        super.onHold()
        Log.i(TAG, "onHold: on Hold")
    }

    override fun onDisconnect() {
        super.onDisconnect()
        Log.i(TAG, "onDisconnect: on Disconnect is Call")
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
    }

    override fun onReject() {
        super.onReject()
        Log.i(TAG, "onReject: on Reject")
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
    }

    override fun onShowIncomingCallUi() {
        super.onShowIncomingCallUi()
        Log.i(TAG, "onShowIncomingCallUi: Incoming Call")
    }

    override fun onUnhold() {
        super.onUnhold()
        Log.i(TAG, "onUnhold: On Unhold")

    }

}