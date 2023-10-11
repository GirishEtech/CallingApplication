package com.example.testapp

import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager.PRESENTATION_ALLOWED
import android.util.Log

class CallService : ConnectionService() {

    val TAG = "CallServiceDebug"
    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        val connection = MyConnection()
        connection.setAddress(request!!.address, PRESENTATION_ALLOWED)
        connection.extras = request.extras
        connection.videoState = request.videoState
        connection.isRingbackRequested = true
        Log.i(TAG, "onCreateOutgoingConnection: Connection Is Started ")
        Log.i(TAG, "onCreateOutgoingConnection: ADDRESS OF CONNECTION :${request.address}")
        Log.i(TAG, "onCreateOutgoingConnection: CONNECTION ACCOUNT ;${request.accountHandle}")
        return connection
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        val connection = MyConnection()
        connection.setAddress(request!!.address, PRESENTATION_ALLOWED)
        connection.extras = request.extras
        connection.videoState = request.videoState
        connection.isRingbackRequested = true

        return connection
    }


    override fun onCreateOutgoingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        Log.i(TAG, "onCreateOutgoingConnectionFailed: failed to Calling")
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request)

    }

    override fun onCreateIncomingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        Log.i(TAG, "onCreateIncomingConnectionFailed: IncomingConnection Failed")
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request)
    }
}
