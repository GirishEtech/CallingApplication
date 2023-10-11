package com.example.testapp

import android.telecom.CallAudioState
import android.telecom.Connection
import android.util.Log

class MyConnection : Connection() {
    val TAG = "MyConnection"

    init {
        connectionProperties = PROPERTY_SELF_MANAGED
        connectionCapabilities = CAPABILITY_SUPPORT_HOLD and CAPABILITY_HOLD
    }

    override fun onStateChanged(state: Int) {
        super.onStateChanged(state)
        when (state) {
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
    }

    override fun onAnswer(videoState: Int) {
        super.onAnswer(videoState)
    }

    override fun onReject() {
        super.onReject()
        Log.i(TAG, "onReject: on Reject")
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