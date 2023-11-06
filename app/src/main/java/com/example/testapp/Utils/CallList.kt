package com.example.testapp.Utils

import com.example.testapp.Models.CallModel

class CallList {
    companion object {
        val callList: ArrayList<CallModel> = ArrayList()
    }

    fun addItem(item: CallModel) {
        callList.add(item)
    }

    fun getAllData(): ArrayList<CallModel> {
        return callList
    }

    fun deleteAll() {
        callList.clear()
    }

    fun remoteLast() {
        callList.removeLast()
    }
}