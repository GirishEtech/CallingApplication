package com.example.testapp.Utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.callingapp.Utils.Utils
import com.example.testapp.Activities.OutGoingCallActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.S)
class AudioRecorder(val context: Context) {
    val TAG = "AudioRecorder"
    var isError = false
    private var recorder: AudioRecord? = null
    private var outputStream: OutputStream? = null
    private var mediaRecorder: MediaRecorder? = null

    init {

        try {
            Log.i(TAG, "Recording Is Init....")
            mediaRecorder =
                MediaRecorder()
        } catch (ex: Exception) {
            isError = true
            Utils.showPermissionDialog(context, "ERROR", ex.toString())
        }
    }

    fun startRecording(outputFile: File, activity: OutGoingCallActivity) {
        try {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAPTURE_AUDIO_OUTPUT),
                1
            )
            mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL)
            mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder!!.setOutputFile(outputFile)
            mediaRecorder!!.prepare()
            mediaRecorder!!.start() // Start recording

        } catch (ex: Exception) {
            Log.d(TAG, "startRecording: ERROR :${ex}")
        }
    }


    private fun saveRecordingFile(audioData: ByteArray, directory: File) {

        val audioFile = File(directory, "call_recording.wav")
        try {
            val outputStream = FileOutputStream(audioFile)
            outputStream.write(audioData)
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
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


