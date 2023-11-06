package com.example.testapp.Activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.CallProvides.CallManager
import com.example.testapp.databinding.ActivityReCallingBinding

class ReCallingActivity : AppCompatActivity() {

    lateinit var callManager: CallManager
    lateinit var _binding: ActivityReCallingBinding
    val binding: ActivityReCallingBinding
        get() = _binding
    val handler = Handler(Looper.getMainLooper())

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityReCallingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val run = Runnable {
            startActivity(Intent(this@ReCallingActivity, MainActivity::class.java))
        }
        callManager = CallManager(this)
        val name = intent.getStringExtra("NAME")
        val number = intent.getStringExtra("NUMBER")
        binding.TxtCallName.text = name
        binding.TxtCallerNumber.text = number
        binding.btnTryCall.setOnClickListener {
            handler.removeCallbacks(run)
            finish()
            callManager.startOutgoingCall(number!!)
        }
        handler.postDelayed(run, 5000)
    }
}