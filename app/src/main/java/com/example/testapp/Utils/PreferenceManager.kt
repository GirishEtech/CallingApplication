package com.example.testapp

import android.content.Context
import android.util.Log
import com.example.callingapp.Utils.Constans

class PreferenceManager(context: Context) {
    val TAG = "PreferenceManager"
    val preference = context.getSharedPreferences(Constans.PREFRENCE, Context.MODE_PRIVATE)
    val editor = preference.edit()

    fun putStatus(value: Boolean) {
        editor.putBoolean(Constans.KEY_ACTIVE, value)
        Log.e(TAG, "putStatus: $value")
        save()
    }

    fun getStatus(): Boolean {
        val value = preference.getBoolean(Constans.KEY_ACTIVE, false)
        Log.e(TAG, "getStatus: $value")
        return value
    }

    fun save() {
        editor.apply()
        editor.commit()
    }


}