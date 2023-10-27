package com.example.testapp.Utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.testapp.Models.Contact

class DatabaseHelper(context: Context) :

    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    val TAG = "DatabaseHelper"

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "ContactsDatabase"
        private const val TABLE_CONTACTS = "contacts"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_PHONE = "phone"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_PHONE + " TEXT" + ")")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }

    fun addContact(contact: Contact) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_NAME, contact.name)
        values.put(KEY_PHONE, contact.number)
        Log.i(TAG, "addContact: added $contact")
        db.insert(TABLE_CONTACTS, null, values)
        db.close()
    }

    fun getContact(id: Int): Contact? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_CONTACTS,
            arrayOf(KEY_ID, KEY_NAME, KEY_PHONE),
            "$KEY_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )
        return if (cursor != null) {
            cursor.moveToFirst()
            val contact = Contact(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2)
            )
            cursor.close()
            contact
        } else {
            null
        }
    }

    fun getAllContacts(): List<Contact> {
        val contactList = mutableListOf<Contact>()
        val selectQuery = "SELECT * FROM $TABLE_CONTACTS"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val contact = Contact(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)
                    )
                    contactList.add(contact)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return contactList
    }

    fun updateContact(contact: Contact): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_NAME, contact.name)
        values.put(KEY_PHONE, contact.number)
        return db.update(TABLE_CONTACTS, values, "$KEY_ID = ?", arrayOf(contact.id.toString()))
    }

    fun deleteContact(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_CONTACTS, "$KEY_ID = ?", arrayOf(id.toString()))
        db.close()
    }
}
