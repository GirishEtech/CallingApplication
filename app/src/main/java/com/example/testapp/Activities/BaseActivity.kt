package com.example.testapp.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.PreferenceManager

open class BaseActivity : AppCompatActivity() {
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(this)
        preferenceManager.setConference(false)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.putStatus(false)
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