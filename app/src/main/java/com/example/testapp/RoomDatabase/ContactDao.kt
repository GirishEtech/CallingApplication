package com.example.testapp.RoomDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ContactDao {
    @Insert
    suspend fun insertUser(user: Contact)

    @Update
    suspend fun updateUser(user: Contact)

    @Delete
    suspend fun deleteUser(user: Contact)

    @Query("SELECT * FROM ContactDetails")
    fun getUsers(): List<Contact>
}