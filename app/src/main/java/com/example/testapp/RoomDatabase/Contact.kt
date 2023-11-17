package com.example.testapp.RoomDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ContactDetails")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val tempId: Int,
    val id: Int,
    val name: String,
    val number: String
)