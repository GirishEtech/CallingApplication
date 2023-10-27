package com.example.testapp.Activities

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.databinding.ActivityPlayAudioBinding

class PlayAudio : AppCompatActivity() {
    private val TAG: String = "PlayAudio"
    lateinit var mediaPlayer: MediaPlayer
    lateinit var _binding: ActivityPlayAudioBinding
    val binding: ActivityPlayAudioBinding
        get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPlayAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val file = intent.getStringExtra("filePath")

        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(file)
            mediaPlayer.prepare()
        } catch (Ex: Exception) {
            Log.e(TAG, "onCreate: $Ex")
        }
        binding.btnPlay.setOnClickListener {
            if (mediaPlayer != null) {
                if (!mediaPlayer.isPlaying) {
                    mediaPlayer.start()
                } else {
                    mediaPlayer.reset()
                    mediaPlayer.release()
                    mediaPlayer.start()
                }
            }
        }
        binding.btnStop.setOnClickListener {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
            }
        }
    }
}