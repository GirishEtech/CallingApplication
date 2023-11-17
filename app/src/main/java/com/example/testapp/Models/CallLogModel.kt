package com.example.testapp.Models

import com.example.testapp.Utils.CallType

data class CallLogModel(
    val id: String,
    val name: String,
    val type: CallType,
    val Number: String,
)