package com.example.testapp.Utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.media.MediaRecorder.AudioSource.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.io.File


@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.S)
class AudioRecorder(val context: Context) {
    val TAG = "AudioRecorder"
    private var mediaRecorder: MediaRecorder? = null
    private val minBuffSize = 44000

    init {
        mediaRecorder =
            MediaRecorder(context)
    }

    @SuppressLint("WrongConstant")
    fun startRecording(outputFile: File) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                val source =
                    VOICE_RECOGNITION
                mediaRecorder!!.setAudioSource(source)
                mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                mediaRecorder!!.setOutputFile(outputFile)
                mediaRecorder!!.prepare()
                mediaRecorder!!.start() // Start recording

            } catch (ex: Exception) {
                Log.d(TAG, "startRecording: ERROR :${ex}")
            }
        } else {
            Log.e(TAG, "startRecording: Permission for Recording Audio is not Granted")
        }

    }


    fun stopRecording() {
        try {
            mediaRecorder!!.release()
            mediaRecorder!!.reset()
            mediaRecorder!!.stop()

        } catch (Ex: Exception) {
            Log.e(TAG, "stopRecording: Error $Ex")
        }
    }


}


