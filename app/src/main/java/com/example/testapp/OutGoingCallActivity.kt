package com.example.testapp


import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.telecom.Connection.*
import android.telecom.DisconnectCause
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.testapp.databinding.ActivityOutGoingCallBinding


class OutGoingCallActivity : AppCompatActivity() {

    val TAG = "OutGoingCallActivity"

    lateinit var connection: MyConnection
    lateinit var _binding: ActivityOutGoingCallBinding
    val binding: ActivityOutGoingCallBinding
        get() = _binding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOutGoingCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
        connection = CallService.getConnection()
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initComponents() {
        setAnimation()
        binding.btnCallend.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ANSWER_PHONE_CALLS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Permission is not Granted", Toast.LENGTH_SHORT).show()
            }
            finish()
            connection.setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
            connection.destroy()
            connection.isRingbackRequested = false
            startActivity(Intent(this, MainActivity::class.java))
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