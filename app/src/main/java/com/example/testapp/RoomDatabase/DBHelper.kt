package com.example.testapp.RoomDatabase

import android.content.Context
import android.util.Log
import androidx.room.Room

class DBHelper {
    companion object {
        private var INSTANCE: ContactDatabase? = null
        fun getInstance(context: Context): ContactDatabase? {
            return try {
                if (INSTANCE == null) {
                    Log.d("DBHelper", "getInstance: INSTANCE IS NULL")
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ContactDatabase::class.java,
                        "RoomHelper"
                    ).build()
                }
                INSTANCE!!
            } catch (ex: Exception) {
                Log.e("DBHelper", "getInstance: $ex")
                null

            }
        }
    }
}