package com.example.testapp.Utils

import android.content.Context
import android.media.RingtoneManager

class RingtoneManage(context: Context) {
    val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
    val ringtone = RingtoneManager.getRingtone(context, uri)

    companion object {
        var INSTANCE: RingtoneManage? = null
        fun getInstance(context: Context): RingtoneManage {
            if (INSTANCE == null) {
                INSTANCE = RingtoneManage(context)
            }
            return INSTANCE!!
        }
    }

    fun PlayRing() {

        if (!ringtone.isPlaying) {
            ringtone.play()
        }
    }

    fun StopRing() {
        if (ringtone.isPlaying) {
            ringtone.stop()
        }
    }

}