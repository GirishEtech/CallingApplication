package com.example.testapp.Activities

import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telecom.Call
import android.telecom.VideoProfile
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.callingapp.Utils.Utils
import com.example.testapp.CallProvides.CallObject
import com.example.testapp.Utils.RingtoneManage
import com.example.testapp.databinding.ActivityIncomingCallBinding


class IncomingCallActivity : BaseActivity() {

    companion object {
        var call: Call? = null
    }

    val TAG = "IncomingCallActivity"
    lateinit var _binding: ActivityIncomingCallBinding
    private val binding: ActivityIncomingCallBinding
        get() = _binding

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityIncomingCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (call != null) {
            binding.TxtCallName.text =
                Utils.getCallerName(this, call!!.details.handle.schemeSpecificPart)
            Log.i(TAG, "onCreate: full call Details ${call!!.details} ")
            Toast.makeText(this, "call is Ringing", Toast.LENGTH_SHORT).show()
            callBack()
        }
        initComponent()
    }

    private fun callBack() {
        call!!.registerCallback(object : Call.Callback() {
            @RequiresApi(Build.VERSION_CODES.R)
            override fun onStateChanged(call: Call?, state: Int) {
                super.onStateChanged(call, state)
                when (state) {
                    Call.STATE_DISCONNECTED -> {
                        Toast.makeText(
                            this@IncomingCallActivity,
                            "Call is Disconnected",
                            Toast.LENGTH_SHORT
                        ).show()
                        call!!.disconnect()
                        call.reject(Call.REJECT_REASON_DECLINED)
                        startActivity(
                            Intent(this@IncomingCallActivity, MainActivity::class.java)
                                .putExtra("isLog", true)
                        )
                        RingtoneManage.getInstance(this@IncomingCallActivity).StopRing()
                        finish()
                    }

                    Call.STATE_SELECT_PHONE_ACCOUNT -> {

                    }

                    Call.STATE_SIMULATED_RINGING -> {

                    }

                    Call.STATE_ACTIVE -> {
                        Toast.makeText(
                            this@IncomingCallActivity,
                            "INCOMING IS ACTIVE",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    Call.STATE_AUDIO_PROCESSING -> {
                    }

                    Call.STATE_CONNECTING -> {

                    }

                    Call.STATE_DIALING -> {
                        Toast.makeText(
                            this@IncomingCallActivity,
                            "INCOMING IS DIALING",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    Call.STATE_DISCONNECTING -> {

                    }

                    Call.STATE_HOLDING -> {
                        Toast.makeText(
                            this@IncomingCallActivity,
                            "INCOMING IS HOLDING",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    Call.STATE_NEW -> {
                        Toast.makeText(this@IncomingCallActivity, "NEW STATE", Toast.LENGTH_SHORT)
                            .show()
                    }

                    Call.STATE_PULLING_CALL -> {
                        Toast.makeText(
                            this@IncomingCallActivity,
                            "INCOMING IS PULLING",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    Call.STATE_RINGING -> {
                        Toast.makeText(
                            this@IncomingCallActivity,
                            "INCOMING IS RINGING",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun initComponent() {
        binding.btnCallAccept.setOnClickListener {
            if (call != null) {
                CallObject.CURRENT_CALL = call
                call!!.answer(VideoProfile.STATE_AUDIO_ONLY)
                call!!.playDtmfTone('1')
                startActivity(Intent(this, OutGoingCallActivity::class.java))
                RingtoneManage.getInstance(this).StopRing()
                finish()
            }
        }
        binding.btnCallDecline.setOnClickListener {
            if (call != null) {
                call!!.reject(Call.REJECT_REASON_DECLINED)
                startActivity(Intent(this, MainActivity::class.java))
                RingtoneManage.getInstance(this).StopRing()
            }
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == "ANSWER" || intent?.action == "DECLINE") {
            dismissNotification()
        }
    }

    private fun dismissNotification() {
        RingtoneManage.getInstance(this).StopRing()
        Utils.deleteLastCallLogEntry(this, Handler(Looper.getMainLooper()))
        Toast.makeText(this, "Notification is Dismiss", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "dismissNotification: Dismiss Notification Function")
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancel(1) // 1 is the notification ID
    }
}