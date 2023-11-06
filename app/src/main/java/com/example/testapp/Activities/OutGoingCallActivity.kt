package com.example.testapp.Activities


import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.TelecomManager
import android.telecom.VideoProfile
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.callingapp.Utils.Utils
import com.example.testapp.Adapter.CallAdapter
import com.example.testapp.CallProvides.CallManager
import com.example.testapp.CallProvides.CallObject
import com.example.testapp.Fragments.ModalBottomSheet
import com.example.testapp.Models.CallModel
import com.example.testapp.R
import com.example.testapp.Utils.CallList
import com.example.testapp.Utils.NotificationManager
import com.example.testapp.Utils.OutputDevice
import com.example.testapp.databinding.ActivityOutGoingCallBinding


class OutGoingCallActivity : BaseActivity() {

    lateinit var callList: CallList
    lateinit var notificationManager: NotificationManager
    lateinit var callAudioState: CallAudioState
    private lateinit var callManager: CallManager
    private var btnOn: Int = 0
    private var btnOff = 0
    private val handler = Handler(Looper.getMainLooper())
    private var totalSeconds = 0
    private val REQUEST_CODE = 0
    private var isBluetoothon = false
    private var isSpeakeron = false
    lateinit var adapter: CallAdapter
    private lateinit var audioManager: AudioManager
    private lateinit var buttomSheet: ModalBottomSheet
    var isCallActive = false
    var Currentcall: Call? = null

    val TAG = "OutGoingCallActivity"
    private lateinit var _binding: ActivityOutGoingCallBinding
    val binding: ActivityOutGoingCallBinding
        get() = _binding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOutGoingCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Audio Permission Denied", Toast.LENGTH_SHORT).show()
        } else {
            setDefault()
            bluetoothManage()
            speakerManage()
            muteManage()
            holdManage()
        }
    }

    private fun setDefault() {
        when (callAudioState.route) {
            CallAudioState.ROUTE_BLUETOOTH -> {
                binding.btnCallSound.imageTintList = ColorStateList.valueOf(btnOn)
                isBluetoothon = true
            }

            CallAudioState.ROUTE_SPEAKER -> {
                binding.btnSpeaker.imageTintList = ColorStateList.valueOf(btnOn)
                isSpeakeron = true
            }

            else -> {

            }
        }

    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun bluetoothManage() {

        checkBluetoothPermission()
        val bAdapter = BluetoothAdapter.getDefaultAdapter()
        binding.btnCallSound.setOnClickListener {
            if (isSpeakeron) {
                binding.btnSpeaker.imageTintList = ColorStateList.valueOf(btnOff)
            }
            if (isBluetoothon) {
                callManager.setOutput(OutputDevice.EARPIECE)
                binding.btnCallSound.imageTintList = ColorStateList.valueOf(btnOff)
                Log.d(TAG, "bluetoothManage: bluetooth is OFF")
                bAdapter.disable()
                isBluetoothon = false
            } else {
                if (!Utils.getDeviceIsConnected()) {
                    val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                    startActivity(intent)
                }
                callManager.setOutput(OutputDevice.BLUETOOTH)
                binding.btnCallSound.imageTintList = ColorStateList.valueOf(btnOn)
                Log.d(TAG, "bluetoothManage: bluetooth is ON")
                bAdapter.enable()
                isBluetoothon = true
            }
        }
    }

    private fun checkBluetoothPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                1
            )
            return false
        } else {
            return true
        }
    }

    private fun holdManage() {
        var ishold = true
        binding.btnCallHold.setOnClickListener {
            if (ishold) {
                binding.btnCallHold.imageTintList = ColorStateList.valueOf(btnOn)
                Log.i(TAG, "initComponents: Call is Holding")
                ishold = false
                Currentcall!!.hold()
            } else {
                binding.btnCallHold.imageTintList = ColorStateList.valueOf(btnOff)
                Currentcall!!.unhold()
                Log.i(TAG, "initComponents: call is unholding")
                ishold = true
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun muteManage() {
        var isPause = true
        binding.btnCallMute.setOnClickListener {
            if (isBluetoothon) {
                binding.btnCallSound.imageTintList = ColorStateList.valueOf(btnOff)
            } else if (isSpeakeron) {
                binding.btnSpeaker.imageTintList = ColorStateList.valueOf(btnOff)
            }
            if (isPause) {
                callManager.setMute(true)

                binding.btnCallMute.imageTintList = ColorStateList.valueOf(btnOn)
                isPause = false
            } else {
                callManager.setMute(false)
                binding.btnCallMute.imageTintList = ColorStateList.valueOf(btnOff)
                isPause = true
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("ResourceType", "MissingPermission")
    private fun speakerManage() {
        // audioManager.requestAudioFocus(getAudioFocusRequest())
        try {
            var isOn = (callAudioState.route == CallAudioState.ROUTE_SPEAKER)
            binding.btnSpeaker.setOnClickListener {
                if (isBluetoothon) {
                    binding.btnCallSound.imageTintList = ColorStateList.valueOf(btnOff)
                }
                if (!isOn) {
                    Log.i(TAG, "speakerManage: Speaker on ${audioManager.isSpeakerphoneOn}")
                    isOn = true
                    callManager.setOutput(OutputDevice.SPEAKER)
                    isSpeakeron = true
                    binding.btnSpeaker.imageTintList = ColorStateList.valueOf(btnOn)
                } else {
                    Log.i(TAG, "speakerManage: Speaker off ${audioManager.isSpeakerphoneOn}")
                    isOn = false
                    callManager.setOutput(OutputDevice.EARPIECE)
                    isSpeakeron = false
                    binding.btnSpeaker.imageTintList = ColorStateList.valueOf(btnOff)
                }
            }
        } catch (ex: Exception) {
            Log.i(TAG, "speakerManage: $ex")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun callBack() {
        Currentcall!!.registerCallback(object : Call.Callback() {
            override fun onStateChanged(call: Call?, state: Int) {
                super.onStateChanged(call, state)
                when (state) {
                    Call.STATE_ACTIVE -> {
                        binding.txtCallingStatus.text = getString(R.string.lbl_active)
                        call!!.answer(VideoProfile.STATE_AUDIO_ONLY)
                        isCallActive = true
                        call.playDtmfTone('1')
                        startTimer()
                        adapter.notifyDataSetChanged()
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
                        isCallActive = false
                        stopTimer()
                        preferenceManager.setConference(false)
                        callList.deleteAll()
                        adapter.notifyDataSetChanged()
                        finish()
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
                        binding.txtCallingStatus.text = "Hold"
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


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initComponents() {
        Log.i(TAG, "initComponents: Activity is Rebuild...")
        callList = CallList()
        adapter = CallAdapter(callList.getAllData())
        Currentcall = CallObject.CURRENT_CALL
        notificationManager = NotificationManager(this)
        if (intent.action == "${packageName}.ANSWER") {
            notificationManager.dismiss()
            Currentcall!!.answer(VideoProfile.STATE_AUDIO_ONLY)
        }
        btnOff = resources.getColor(android.R.color.black)
        btnOn = resources.getColor(R.color.SpeakerOn)
        buttomSheet = ModalBottomSheet()
        setAnimation()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        callManager = CallManager(this)
        callAudioState = callManager.getDefault()!!
        if (Currentcall != null && preferenceManager.getConference()) {
            adapter.notifyDataSetChanged()
            callBack()
        } else {
            if (Currentcall != null) {
                callList.addItem(CallModel(Currentcall!!, true, false))
                callBack()
            }
            binding.callList.adapter = adapter
            preferenceManager.setConference(false)
        }
        binding.btnCallend.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ANSWER_PHONE_CALLS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Permission is not Granted", Toast.LENGTH_SHORT).show()
            } else {
                val Number = CallObject.CURRENT_CALL!!.details.handle.schemeSpecificPart
                if (Currentcall == null) {

                    startActivity(Intent(this, ReCallingActivity::class.java).apply {
                        putExtra("NAME", Utils.getCallerName(this@OutGoingCallActivity, Number))
                        putExtra("NUMBER", Number)
                    })
                } else {
                    if (isCallActive) {
                        Currentcall!!.disconnect()
                        startActivity(Intent(this, ReCallingActivity::class.java).apply {
                            putExtra("NAME", Utils.getCallerName(this@OutGoingCallActivity, Number))
                            putExtra("NUMBER", Number)
                        })
                    } else {
                        getSystemService(TelecomManager::class.java).endCall()
                        startActivity(Intent(this, ReCallingActivity::class.java).apply {
                            putExtra("NAME", Utils.getCallerName(this@OutGoingCallActivity, Number))
                            putExtra("NUMBER", Number)
                        })
                    }
                }
            }
        }
        binding.btnAddCall.setOnClickListener {
            buttomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (REQUEST_CODE == requestCode) {
            Toast.makeText(this, "Now Your granted to record Calls", Toast.LENGTH_SHORT).show()
        }
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