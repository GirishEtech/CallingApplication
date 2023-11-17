package com.example.testapp.RoomDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ContactLogsDao {
    @Insert
    suspend fun insertUser(user: ContactLogs)

    @Query("SELECT * FROM ContactLogDetails")
    fun getUsers(): List<ContactLogs>

    @Query("DELETE  FROM  ContactDetails")
    fun removeAll()
}