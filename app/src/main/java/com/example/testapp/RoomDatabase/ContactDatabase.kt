package com.example.testapp.RoomDatabase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Contact::class, ContactLogs::class], version = 3)
abstract class ContactDatabase : RoomDatabase() {
    abstract fun ContactDao(): ContactDao
    abstract fun ContactLogsDao(): ContactLogsDao
}