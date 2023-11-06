package com.example.testapp.Activities

import android.app.NotificationManager
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.telecom.Call
import android.telecom.VideoProfile
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.callingapp.Utils.Utils
import com.example.testapp.CallProvides.CallObject
import com.example.testapp.databinding.ActivityIncomingCallBinding


@Suppress("CAST_NEVER_SUCCEEDS")
class IncomingCallActivity : BaseActivity() {

    companion object {
        var call: Call? = null
    }

    val TAG = "IncomingCallActivity"
    lateinit var ringtone: Ringtone
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
            binding.TxtCallerNumber.text = call!!.details.handle.schemeSpecificPart
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
                        startActivity(Intent(this@IncomingCallActivity, MainActivity::class.java))
                        finish()
                    }

                    Call.STATE_SELECT_PHONE_ACCOUNT -> {

                    }

                    Call.STATE_SIMULATED_RINGING -> {

                    }

                    Call.STATE_ACTIVE -> {

                    }

                    Call.STATE_AUDIO_PROCESSING -> {
                    }

                    Call.STATE_CONNECTING -> {
                        TODO()
                    }

                    Call.STATE_DIALING -> {
                        TODO()
                    }

                    Call.STATE_DISCONNECTING -> {
                        TODO()
                    }

                    Call.STATE_HOLDING -> {
                        TODO()
                    }

                    Call.STATE_NEW -> {
                        TODO()
                    }

                    Call.STATE_PULLING_CALL -> {
                        TODO()
                    }

                    Call.STATE_RINGING -> {
                        TODO()
                    }
                }
            }
        })
    }

    fun startDefaultRingtone() {
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(this, ringtoneUri)
        ringtone.play()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun initComponent() {
        startDefaultRingtone()
        binding.btnCallAccept.setOnClickListener {
            if (call != null) {
                CallObject.CURRENT_CALL = call
                call!!.answer(VideoProfile.STATE_AUDIO_ONLY)
                call!!.playDtmfTone('1')
                startActivity(Intent(this, OutGoingCallActivity::class.java))
                stopRingtone()
                finish()
            }
        }
        binding.btnCallDecline.setOnClickListener {
            if (call != null) {
                call!!.reject(Call.REJECT_REASON_DECLINED)
                startActivity(Intent(this, MainActivity::class.java))
                stopRingtone()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        stopRingtone()

    }

    fun stopRingtone() {
        if (ringtone.isPlaying) {
            ringtone.stop()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == "ANSWER") {
            dismissNotification()
        } else if (intent?.action == "DECLINE") {
            dismissNotification()
        }
    }

    private fun dismissNotification() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancel(1) // 1 is the notification ID
    }
}