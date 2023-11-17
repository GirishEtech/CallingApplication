package com.example.testapp.RoomDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ContactLogDetails")
data class ContactLogs(
    @PrimaryKey(autoGenerate = true)
    val pmId: Int,
    val id: String,
    val name: String,
    val type: String,
    val number: String
)