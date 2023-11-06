package com.example.testapp.Models

import android.telecom.Call

data class CallModel(val callData: Call, val isActive: Boolean, val isConfereneActive: Boolean)