package com.example.testapp.CallProvides

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.telecom.Call
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.callingapp.Utils.Utils
import com.example.testapp.Activities.IncomingCallActivity
import com.example.testapp.Activities.OutGoingCallActivity
import com.example.testapp.Models.CallModel
import com.example.testapp.PreferenceManager
import com.example.testapp.Utils.CallList
import com.example.testapp.Utils.RingtoneManage
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

        @SuppressLint("StaticFieldLeak")
        var INSTANCE: CallObject? = null
        fun MergeConference() {
            CURRENT_CALL!!.mergeConference()
        }

        const val TAG = "CallObject"
        fun swapConferenceCall() {
            CURRENT_CALL?.swapConference()
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    fun isScreenOn(): Boolean {
        val powerManager = context.getSystemService(POWER_SERVICE) as PowerManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            powerManager.isInteractive
        } else {
            @Suppress("DEPRECATION")
            powerManager.isScreenOn
        }
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
                    OutGoingCallActivity.adapter?.notifyDataSetChanged()
                    OutGoingCallActivity.binding1?.callListLayout?.visibility = View.VISIBLE
                    OutGoingCallActivity.binding1?.layoutTemp?.visibility = View.GONE
                }

            }

            Call.Details.DIRECTION_INCOMING -> {
                RingtoneManage.getInstance(context).PlayRing()
                Log.i(LOG_TAG, "onCallAdded: isActive :$isActive")
                Log.i(LOG_TAG, "onCallAdded: ConferenceAdded-orNot $isConference")
                if (isActive || !isScreenOn()) {
                    val intent = Intent(context, IncomingCallActivity::class.java)
                    IncomingCallActivity.call = call
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                    Toast.makeText(context, "INCOMING IS CALl", Toast.LENGTH_SHORT).show()
                } else {
                    preferenceManager.PutRinging(true)
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
        if (preferenceManager.getConference()) {
            call?.let {
                if (it.details.handle.schemeSpecificPart ==
                    CURRENT_CALL!!.details.handle.schemeSpecificPart
                ) {
                    CURRENT_CALL = call
                    Log.i(TAG, "updateCall: this is Same Call as Well")
                } else {
                    preferenceManager.setConference(true)
                    CallList.callList.add(CallModel(call, true, true))
                    CURRENT_CALL!!.conference(call)
                    Log.i(TAG, "updateCall: this is not Same Call As Well")
                }
            }

        } else {
            call?.let { CallModel(it, true, true) }?.let { CallList.callList.add(it) }
            CURRENT_CALL = call
            preferenceManager.setConference(false)
        }
        setDirection(call)
        call?.let {
            subject.onNext(it)
        }
    }

}