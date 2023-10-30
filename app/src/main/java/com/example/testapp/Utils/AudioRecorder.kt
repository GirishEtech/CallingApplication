package com.example.testapp.Utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File

@RequiresApi(Build.VERSION_CODES.S)
class AudioRecorder(val context: Context) {
    val TAG = "AudioRecorder"
    var mediaRecorder: MediaRecorder

    init {
        mediaRecorder =
            MediaRecorder()
    }

    fun startRecording(outputFile: File) {
        try {
            val Media = MediaRecorder.AudioSource.VOICE_UPLINK
//            mediaRecorder.setAudioSource(Media)
//            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
//            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder.setOutputFile(outputFile)
            mediaRecorder.prepare()
            mediaRecorder.start() // Start recording

        } catch (ex: Exception) {
            Log.d(TAG, "startRecording: ERROR :${ex}")
        }
    }

    fun stopRecording() {
        try {
            mediaRecorder.release()
            mediaRecorder.reset()
            mediaRecorder.stop()
        } catch (Ex: Exception) {
            Log.e(TAG, "stopRecording: Error $Ex")
        }
    }

}


