package com.example.testapp.CallProvides

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.CallAudioState
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.testapp.R
import com.example.testapp.Utils.OutputDevice


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class CallManager(
    private val context: Context
) {
    private val TAG = "CallManager"
    private var telecomManager: TelecomManager =
        context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    private lateinit var phoneAccountHandle: PhoneAccountHandle

    init {
        createPhoneAccount()
    }

    @RequiresApi(34)
    @SuppressLint("MissingPermission")
    fun startOutgoingCall(number: String) {
        val test = Bundle()
        //test.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true)
        test.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
        // test.putBoolean(context.packageName + ".extra.NO_CALL_LOG", true)
        //CallResponse

        try {
            val account = telecomManager.getPhoneAccount(phoneAccountHandle)
            val isCallableAccount =
                telecomManager.isOutgoingCallPermitted(phoneAccountHandle)
            Log.i(TAG, "startOutgoingCall: isAble to Call :$isCallableAccount")
            if (account != null) {
                //${number.substringAfter("+91")}
                val uri = Uri.parse("tel:+91${number.substringAfter("+91")}")
                telecomManager.placeCall(uri, test)
            } else {
                Log.d(TAG, "account is not Available")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ERROR", "ERROR FOR CALL" + e.message.toString())
        }

    }

    fun startIncomingCall() {
        if (this.context.checkSelfPermission(Manifest.permission.MANAGE_OWN_CALLS) == PackageManager.PERMISSION_GRANTED) {
            val extras = Bundle()
            val uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, "", null)
            extras.putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, uri)
            extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
            extras.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true)
            val isCallPermitted =
                telecomManager.isIncomingCallPermitted(phoneAccountHandle)
            Log.i(TAG, "is incoming call permitted = $isCallPermitted")
            telecomManager.addNewIncomingCall(phoneAccountHandle, extras)
        }
    }

    private fun createPhoneAccount() {
        //val uuid = UUID.randomUUID()
        val componentName =
            ComponentName(context.packageName, CallService::class.java.name)
        phoneAccountHandle = PhoneAccountHandle(componentName, "123UUID")
        val phoneAccount = PhoneAccount.Builder(
            phoneAccountHandle,
            "${R.string.app_name}"
        )
            .setCapabilities(
                PhoneAccount.CAPABILITY_CALL_PROVIDER and PhoneAccount.CAPABILITY_ADHOC_CONFERENCE_CALLING
            )
            .setIcon(Icon.createWithResource(context, R.drawable.ic_call))
            .setShortDescription("${R.string.app_name}")
            .build()
        telecomManager.registerPhoneAccount(phoneAccount)

    }


    fun showPhoneAccount() {
        val intent = Intent()
        intent.component = ComponentName(
            "com.android.server.telecom",
            "com.android.server.telecom.settings.EnableAccountPreferenceActivity"
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun setOutput(outputType: OutputDevice) {
        val service = MyInCallService.INSTANCE
        when (outputType) {
            OutputDevice.SPEAKER -> {
                service!!.setAudioRoute(CallAudioState.ROUTE_SPEAKER)
            }

            OutputDevice.EARPIECE -> {
                service!!.setAudioRoute(CallAudioState.ROUTE_EARPIECE)
            }

            OutputDevice.BLUETOOTH -> {
                service!!.setAudioRoute(CallAudioState.ROUTE_BLUETOOTH)
            }

            else -> {
                Log.i(TAG, "setOutput: invalid Output Device")
            }
        }
    }

    fun getDefault(): CallAudioState? {
        return try {
            val service = MyInCallService.INSTANCE
            service!!.callAudioState
        } catch (ex: Exception) {
            null
        }
    }

    fun setMute(mute: Boolean) {
        val service = MyInCallService.INSTANCE
        service!!.setMuted(mute)
    }

    fun mergeConference() {
        CallObject.MergeConference(context)
    }

    fun SwapCalls() {
        CallObject.swapConferenceCall()
    }
}