package com.example.testapp.Activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.CallProvides.MyInCallService
import com.example.testapp.PreferenceManager

open class BaseActivity : AppCompatActivity() {
    lateinit var preferenceManager: PreferenceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(this)
        preferenceManager.setConference(false)
        try {
            if (preferenceManager.getStatus() && preferenceManager.getIsregister()) {
                unregisterReceiver(MyInCallService.receiver)
            }
        } catch (ex: Exception) {
            Log.e("BASE-ACTIVITY", "onCreate: receiver is not Register")
        }
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.putStatus(false)
        preferenceManager.putIsRegister(false)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.putStatus(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceManager.setConference(false)
    }
}