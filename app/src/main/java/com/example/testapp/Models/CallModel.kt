package com.example.testapp.Models

import android.telecom.Call

data class CallModel(
    var callData: Call,
    var isActive: Boolean,
    val isConfereneActive: Boolean,
    var isFirst: Boolean
)