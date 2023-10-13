package com.example.testapp

import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.databinding.ActivityIncomingCallBinding


class IncomingCallActivity : AppCompatActivity() {

    lateinit var ringtone: Ringtone
    lateinit var _binding: ActivityIncomingCallBinding
    private val binding: ActivityIncomingCallBinding
        get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityIncomingCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val name = intent.getStringExtra("NAME")
        if (name != null) {
            binding.TxtCallName.text = name
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
    }

    override fun onStop() {
        super.onStop()
        if (ringtone.isPlaying) {
            ringtone.stop()
        }

    }
}