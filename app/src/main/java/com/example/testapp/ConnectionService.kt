package com.example.testapp

import android.annotation.SuppressLint
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager.PRESENTATION_ALLOWED
import android.util.Log

class CallService : ConnectionService() {


    companion object {
        @SuppressLint("StaticFieldLeak")
        var con: MyConnection? = null
        fun getConnection(): MyConnection {
            return con!!
        }
    }


    val TAG = "CallServiceDebug"
    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        con = MyConnection(this)
        con!!.setAddress(request!!.address, PRESENTATION_ALLOWED)
        con!!.extras = request.extras
        con!!.videoState = request.videoState
        con!!.isRingbackRequested = true
        Log.i(TAG, "onCreateOutgoingConnection: Connection Is Started ")
        Log.i(TAG, "onCreateOutgoingConnection: ADDRESS OF CONNECTION :${request.address}")
        Log.i(TAG, "onCreateOutgoingConnection: CONNECTION ACCOUNT ;${request.accountHandle}")
        return con!!
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        val connection = MyConnection(this)
        connection.setAddress(request!!.address, PRESENTATION_ALLOWED)
        connection.extras = request.extras
        connection.videoState = request.videoState
        connection.connectionProperties = Connection.PROPERTY_SELF_MANAGED
        connection.isRingbackRequested = true
        connection.setCallerDisplayName("demo", PRESENTATION_ALLOWED)
        con = connection
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
