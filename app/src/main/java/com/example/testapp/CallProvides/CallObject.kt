package com.example.testapp.CallProvides

import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.Call
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.callingapp.Utils.Utils
import com.example.testapp.Activities.IncomingCallActivity
import com.example.testapp.Activities.OutGoingCallActivity
import com.example.testapp.Models.CallModel
import com.example.testapp.PreferenceManager
import com.example.testapp.Utils.CallList
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class CallObject(val context: Context) {

    init {
        ANOTHERC_CALL = ArrayList()
    }

    val notificationManager = com.example.testapp.Utils.NotificationManager(context)
    private val LOG_TAG = "CallObject"
    private val subject = BehaviorSubject.create<Call>()
    private var currentCall: Call? = null
    private var anotherCall: Call? = null
    private var preferenceManager = PreferenceManager(context)
    fun updates(): Observable<Call> = subject

    companion object {
        var CURRENT_CALL: Call? = null
        var ANOTHERC_CALL: ArrayList<Call>? = null
        var INSTANCE: CallObject? = null
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun setDirection(call: Call?) {
        val isActive = preferenceManager.getStatus()
        val isConference = preferenceManager.getConference()
        when (call!!.details!!.callDirection) {
            Call.Details.DIRECTION_OUTGOING -> {
                if (!isConference) {
                    val intent = Intent(context, OutGoingCallActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } else {
                    CallList.callList.add(CallModel(call, true, true))
                }

            }

            Call.Details.DIRECTION_INCOMING -> {
                Log.i(LOG_TAG, "onCallAdded: isActive :$isActive")
                Log.i(LOG_TAG, "onCallAdded: ConferenceAdded-orNot $isConference")
                if (isActive) {
                    val intent = Intent(context, IncomingCallActivity::class.java)
                    IncomingCallActivity.call = call
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } else {
                    notificationManager.createNotification(
                        Utils.getCallerName(
                            context,
                            call.details.handle.schemeSpecificPart
                        )
                    )


                }

            }

            else -> {
                // This may represent other call types like self-managed or unknown
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun updateCall(call: Call?) {
        INSTANCE = this
        if (currentCall != null) {
            call?.let {
                if (it.details.handle.schemeSpecificPart ==
                    currentCall!!.details.handle.schemeSpecificPart
                ) {
                    CURRENT_CALL = call
                    currentCall = call
                } else {
                    ANOTHERC_CALL!!.add(call)
                    anotherCall = call
                }
            }
        } else {
            CURRENT_CALL = call
            currentCall = call
        }
        setDirection(call)
        call?.let {
            subject.onNext(it)
        }
    }


    fun cancelCall() {
        currentCall?.let {
            when (it.state) {
                Call.STATE_RINGING -> rejectCall()
                else -> disconnectCall()
            }
        }
    }


    fun holdCall() {
        currentCall?.let {
            when (it.state) {
                Call.STATE_ACTIVE -> currentCall?.hold()
                else -> Log.i(LOG_TAG, "Call not in connected state")
            }
        }
    }

    //Conference call method
    fun conferenceCall() {
        anotherCall?.conference(currentCall)
    }

    fun swapConferenceCall() {
        currentCall?.swapConference()
    }

    fun mergeConferenceCall() {
        currentCall?.mergeConference()
    }

    fun unHoldCall() {
        currentCall?.let {
            when (it.state) {
                Call.STATE_HOLDING -> currentCall?.unhold()
                else -> Log.i(LOG_TAG, "Call not in connected state")
            }
        }
    }

    fun isCallOnHold(): Boolean {
        currentCall?.let {
            return when (it.state) {
                Call.STATE_HOLDING -> true
                else -> false
            }
        }
        return false
    }

    fun acceptCall() {
        Log.i(LOG_TAG, "acceptCall")
        currentCall?.let {
            it.answer(it.details.videoState)
        }
    }

    private fun rejectCall() {
        Log.i(LOG_TAG, "rejectCall")
        currentCall?.reject(false, "")
    }

    private fun disconnectCall() {
        Log.i(LOG_TAG, "disconnectCall")
        currentCall?.disconnect()
    }


}