package com.example.callingapp.Utils

import android.content.Context
import android.widget.Toast

class Messages {
    companion object {
        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}