package com.example.testapp.Activities

import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.telecom.Call
import android.telecom.VideoProfile
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.callingapp.Utils.Utils
import com.example.testapp.databinding.ActivityIncomingCallBinding


@Suppress("CAST_NEVER_SUCCEEDS")
class IncomingCallActivity : AppCompatActivity() {

    companion object {
        var call: Call? = null
    }

    val TAG = "IncomingCallActivity"
    lateinit var ringtone: Ringtone
    lateinit var _binding: ActivityIncomingCallBinding
    private val binding: ActivityIncomingCallBinding
        get() = _binding

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
        }
        initComponent()
    }

    fun startDefaultRingtone() {
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(this, ringtoneUri)
        ringtone.play()
    }

    private fun initComponent() {
        startDefaultRingtone()
        binding.btnCallAccept.setOnClickListener {
            if (call != null) {
                OutGoingCallActivity.call = call
                call!!.answer(VideoProfile.STATE_AUDIO_ONLY)
                call!!.playDtmfTone('1')
                startActivity(Intent(this, OutGoingCallActivity::class.java))
                stopRingtone()
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
}