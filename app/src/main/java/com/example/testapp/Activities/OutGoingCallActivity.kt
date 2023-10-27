package com.example.testapp.Activities


import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telecom.Call
import android.telecom.VideoProfile
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.callingapp.Utils.Utils
import com.example.testapp.R
import com.example.testapp.Utils.AudioRecorder
import com.example.testapp.databinding.ActivityOutGoingCallBinding
import java.io.IOException


class OutGoingCallActivity : AppCompatActivity() {


    private var btnOn: Int = 0
    private var btnOff = 0
    private val handler = Handler(Looper.getMainLooper())
    private var totalSeconds = 0
    private val REQUEST_CODE = 0
    private var mDPM: DevicePolicyManager? = null
    private var mAdminName: ComponentName? = null
    lateinit var mediaRecorder: AudioRecorder
    lateinit var audioManager: AudioManager
    lateinit var outputPath: String

    companion object {
        var call: Call? = null
    }

    val TAG = "OutGoingCallActivity"
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
            createAdmin()
        }
    }

    private fun createAdmin() {
        try {
            // Initiate DevicePolicyManager.
            mDPM = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
            mAdminName = ComponentName(this, OutGoingCallActivity::class.java)
            if (!mDPM!!.isAdminActive(mAdminName!!)) {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName)
                intent.putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Click on Activate button to secure your application."
                )
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                // mDPM.lockNow();
                // Intent intent = new Intent(MainActivity.this,
                // TrackDeviceService.class);
                // startService(intent);
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    @SuppressLint("ResourceType")
    private fun speakerManage() {
        try {
            var isSpeakerOn = false
            binding.btnSpeaker.imageTintList = ColorStateList.valueOf(btnOn)
            binding.btnSpeaker.setOnClickListener {
                if (isSpeakerOn) {
                    Log.i(TAG, "speakerManage: Speaker on ${audioManager.isSpeakerphoneOn}")
                    isSpeakerOn = false
                    audioManager.mode = AudioManager.MODE_IN_CALL
                    audioManager.isSpeakerphoneOn = true
                    binding.btnSpeaker.imageTintList = ColorStateList.valueOf(btnOn)
                } else {
                    Log.i(TAG, "speakerManage: Speaker off ${audioManager.isSpeakerphoneOn}")
                    isSpeakerOn = true
                    audioManager.mode = AudioManager.MODE_NORMAL
                    audioManager.isSpeakerphoneOn = false
                    binding.btnSpeaker.imageTintList = ColorStateList.valueOf(btnOff)
                }
            }
        } catch (ex: Exception) {
            Log.i(TAG, "speakerManage: $ex")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun callBack() {
        call!!.registerCallback(object : Call.Callback() {
            override fun onStateChanged(call: Call?, state: Int) {
                super.onStateChanged(call, state)
                when (state) {
                    Call.STATE_ACTIVE -> {
                        binding.txtCallingStatus.text = getString(R.string.lbl_active)
                        call!!.answer(VideoProfile.STATE_AUDIO_ONLY)
                        call.playDtmfTone('1')
                        startRecording()
                        startTimer()
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
                        call!!.disconnect()
                        call.reject(Call.REJECT_REASON_DECLINED)
                        stopRecording()
                        stopTimer()
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

    @RequiresApi(Build.VERSION_CODES.S)
    private fun stopRecording() {
        try {
            mediaRecorder.stopRecording()
            Toast.makeText(this, "audio is Recorded", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@OutGoingCallActivity, PlayAudio::class.java)
            intent.putExtra("filePath", outputPath)
            startActivity(intent)
        } catch (ex: Exception) {
            Log.i(TAG, "stopRecording:${ex.message}")
        }
    }

    private fun requestAudioPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1,
            )
            Toast.makeText(this, "Permission Denied for Record Audio", Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecording() {
        if (requestAudioPermission()) {
            try {
                val outputFilePath = Utils.getRecordingFile(
                    Utils.getCallerName(
                        this,
                        call!!.details.handle.schemeSpecificPart
                    )
                )

                if (outputFilePath == null) {
                    Toast.makeText(this, "File is Null", Toast.LENGTH_SHORT).show()
                } else {
                    mediaRecorder.startRecording(outputFilePath)
                    this.outputPath = outputFilePath.absolutePath
                }

                Toast.makeText(this, "Recording is Started", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Log.e(TAG, "startRecording: Record Audio is Denied")
        }

    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initComponents() {
        btnOff = resources.getColor(android.R.color.black)
        btnOn = resources.getColor(R.color.SpeakerOn)
        setAnimation()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        mediaRecorder = AudioRecorder(this)
        if (call != null) {
            val name = Utils.getCallerName(this, call!!.details.handle.schemeSpecificPart)
            val number = call!!.details.handle.schemeSpecificPart
            binding.TxtCallName.text = name
            binding.TxtCallerNumber.text = number
        }
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
        var isPause = true
        binding.btnCallMute.setOnClickListener {
            if (isPause) {
                binding.btnCallMute.imageTintList = ColorStateList.valueOf(btnOn)
                isPause = false
                call!!.stopDtmfTone()
            } else {
                binding.btnCallMute.imageTintList = ColorStateList.valueOf(btnOff)
                isPause = true
                call!!.playDtmfTone('1')
            }
        }
        var ishold = true
        binding.btnCallHold.setOnClickListener {
            if (ishold) {
                binding.btnCallHold.imageTintList = ColorStateList.valueOf(btnOn)
                call!!.hold()
                Log.i(TAG, "initComponents: Call is Holding")
                ishold = false
            } else {
                binding.btnCallHold.imageTintList = ColorStateList.valueOf(btnOff)
                call!!.unhold()
                call!!.playDtmfTone('1')
                call!!.answer(VideoProfile.STATE_AUDIO_ONLY)
                Log.i(TAG, "initComponents: call is unholding")
                ishold = true

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (REQUEST_CODE == requestCode) {
            Toast.makeText(this, "Now Your granted to record Calls", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasPermission(context: Context, permissionStr: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permissionStr
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun stopTimer() {
        handler.removeCallbacksAndMessages(null)
    }

    private fun startTimer() {
        handler.post(object : Runnable {
            override fun run() {
                val h = totalSeconds / 3600
                val m = (totalSeconds % 3600) / 60
                val s = totalSeconds % 60

                binding.txtCallingStatus.text = String.format("%02d:%02d:%02d", h, m, s)

                totalSeconds++
                handler.postDelayed(this, 1000)  // Update every second
            }
        })
    }


}