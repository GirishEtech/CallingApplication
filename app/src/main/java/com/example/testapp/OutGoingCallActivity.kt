package com.example.testapp


import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.telecom.Call
import android.telecom.Connection.*
import android.telecom.VideoProfile
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.testapp.databinding.ActivityOutGoingCallBinding
import java.io.File
import java.io.IOException


class OutGoingCallActivity : AppCompatActivity() {

    lateinit var mediaRecorder: MediaRecorder
    lateinit var audioManager: AudioManager

    companion object {
        var call: Call? = null
    }

    val TAG = "OutGoingCallActivity"

    //lateinit var connection: MyConnection
    lateinit var _binding: ActivityOutGoingCallBinding
    val binding: ActivityOutGoingCallBinding
        get() = _binding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOutGoingCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
        if (call != null) {
            callBack()
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Audio Permission Denied", Toast.LENGTH_SHORT).show()
        } else {
            speakerManage()
        }
    }


    @SuppressLint("ResourceType")
    private fun speakerManage() {
        try {
            var isSpeakerOn = true
            val SpeakerOff = resources.getColor(android.R.color.black)
            val speakerOn = resources.getColor(R.color.SpeakerOn)
            binding.btnSpeaker.imageTintList = ColorStateList.valueOf(speakerOn)
            binding.btnSpeaker.setOnClickListener {
                if (isSpeakerOn) {
                    Log.i(TAG, "speakerManage: Speaker on ${audioManager.isSpeakerphoneOn}")
                    isSpeakerOn = false
                    audioManager.mode = AudioManager.MODE_IN_CALL
                    audioManager.isSpeakerphoneOn = true
                    binding.btnSpeaker.imageTintList = ColorStateList.valueOf(speakerOn)
                } else {
                    Log.i(TAG, "speakerManage: Speaker off ${audioManager.isSpeakerphoneOn}")
                    isSpeakerOn = true
                    audioManager.mode = AudioManager.RINGER_MODE_SILENT
                    audioManager.isSpeakerphoneOn = false
                    binding.btnSpeaker.imageTintList = ColorStateList.valueOf(SpeakerOff)
                }
            }
        } catch (ex: Exception) {
            Log.i(TAG, "speakerManage: $ex")
        }
    }

    private fun callBack() {
        call!!.registerCallback(object : Call.Callback() {
            override fun onStateChanged(call: Call?, state: Int) {
                super.onStateChanged(call, state)
                when (state) {
                    Call.STATE_ACTIVE -> {
                        Toast.makeText(
                            this@OutGoingCallActivity,
                            "Call is Active",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.txtCallingStatus.text = getString(R.string.lbl_active)
                        call!!.answer(VideoProfile.STATE_AUDIO_ONLY)
                        call.playDtmfTone('1')
                        startRecording()
                    }

                    Call.STATE_AUDIO_PROCESSING -> {
                        Toast.makeText(
                            this@OutGoingCallActivity,
                            "Call audio is Processing",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    Call.STATE_CONNECTING -> {
                        Toast.makeText(
                            this@OutGoingCallActivity,
                            "Call is Connecting",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    Call.STATE_DIALING -> {
                        Toast.makeText(
                            this@OutGoingCallActivity,
                            "Call is Dialing",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.txtCallingStatus.text = getString(R.string.lbl_calling)
                    }

                    Call.STATE_DISCONNECTED -> {
                        Toast.makeText(
                            this@OutGoingCallActivity,
                            "Call is Disconnected",
                            Toast.LENGTH_SHORT
                        ).show()
                        call!!.disconnect()
                        call.reject(Call.REJECT_REASON_DECLINED)
                        stopRecording()
                        startActivity(Intent(this@OutGoingCallActivity, MainActivity::class.java))
                    }

                    Call.STATE_DISCONNECTING -> {
                        Toast.makeText(
                            this@OutGoingCallActivity,
                            "Call Disconnecting",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    Call.STATE_HOLDING -> {
                        Toast.makeText(
                            this@OutGoingCallActivity,
                            "Call is Holding",
                            Toast.LENGTH_SHORT
                        ).show()
                        call!!.hold()
                    }

                    Call.STATE_NEW -> {
                        Toast.makeText(
                            this@OutGoingCallActivity,
                            "Start the call",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.txtCallingStatus.text = getString(R.string.lbl_dialing)
                    }

                    Call.STATE_PULLING_CALL -> {
                        Toast.makeText(
                            this@OutGoingCallActivity,
                            "Call is Pulling",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    Call.STATE_RINGING -> {
                        Toast.makeText(
                            this@OutGoingCallActivity,
                            "Call is Ringing",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    Call.STATE_SELECT_PHONE_ACCOUNT -> {

                    }

                    Call.STATE_SIMULATED_RINGING -> {

                    }
                }
            }
        })
    }

    private fun stopRecording() {
        try {
            mediaRecorder.apply {
                stop()
                //release()
                Log.i(TAG, "stopRecording: Recording is Stop")
            }
        } catch (ex: Exception) {
            Log.i(TAG, "stopRecording:${ex.message}")
        }
    }

    private fun startRecording() {
        mediaRecorder = MediaRecorder(this)
        mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        }
        val outputFileName = "call(${call!!.details.handle.schemeSpecificPart}).m4a"
        val outputFilePath =
            Environment.getExternalStorageDirectory().absolutePath + File.separator + outputFileName
        Log.i(TAG, "startRecording: outputFilePath :$outputFilePath")
        mediaRecorder.setOutputFile(outputFilePath)
        try {
            mediaRecorder.prepare()
            mediaRecorder.start()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.i(TAG, "startRecording: ${e.message}")
        }

    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initComponents() {
        setAnimation()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        binding.btnCallend.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ANSWER_PHONE_CALLS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Permission is not Granted", Toast.LENGTH_SHORT).show()
            } else {
                if (call == null) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    call!!.disconnect()
                    //deleteEntry()
                }
            }
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun setAnimation() {
        val colorAnimator = ObjectAnimator.ofArgb(
            binding.OutContainer.background,
            "color",
            Color.parseColor("#1F39BC"),
            Color.parseColor("#067BD8")
        )
        colorAnimator.duration = 3000
        colorAnimator.start()
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(colorAnimator)
        animatorSet.interpolator = LinearInterpolator()
        animatorSet.duration = 4000 // Total duration of one cycle

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                animation.start() // Restart the animation
            }
        })

        animatorSet.start() // Start the animation
    }


}