package com.example.callingapp.Utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.provider.CallLog
import android.provider.ContactsContract
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.testapp.Models.Contact
import com.example.testapp.Utils.DatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class Utils {

    companion object {
        var alertDialog: AlertDialog? = null
        private val TAG = "Utils"
        var count = 0

        fun deleteCallLogEntry(context: Context, callId: Long): Int {
            try {
                val contentResolver: ContentResolver = context.contentResolver
                val callUri: Uri = CallLog.Calls.CONTENT_URI
                val whereClause = "${CallLog.Calls._ID} = ?"
                val selectionArgs = arrayOf(callId.toString())

                // Delete the call log entry
                val deletedRows = contentResolver.delete(callUri, whereClause, selectionArgs)

                return deletedRows
            } catch (e: Exception) {
                Log.e("CallLog", "Error deleting call log entry: ${e.message}")
                return 0
            }
        }

        @SuppressLint("Range")
        fun deleteLastCallLogEntry(context: Context, handler: Handler) {
            try {
                handler.postDelayed({
                    val cursor: Cursor? = context.contentResolver.query(
                        CallLog.Calls.CONTENT_URI,  // The content URI for call logs
                        null,                      // Projection (null for all columns)
                        null,                      // Selection
                        null,                      // Selection arguments
                        "${CallLog.Calls.DATE} DESC"  // Sort order (most recent first)
                    )
                    cursor?.use {
                        if (it.moveToFirst()) {
                            val id = it.getInt(it.getColumnIndex(CallLog.Calls._ID))
                            val number =
                                it.getString(it.getColumnIndex(CallLog.Calls.NUMBER))
                            val name =
                                it.getString(it.getColumnIndex(CallLog.Calls.CACHED_NAME))
                            val date = it.getLong(it.getColumnIndex(CallLog.Calls.DATE))
                            val dateFormat =
                                SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                            val dateMain = dateFormat.format(Date(date))
                            Log.i(
                                TAG,
                                "deleteLastCallLogEntry: CALLNUMBER :$number \n CALLNAME :$name \n CALLDATE :$dateMain"
                            )
                            val deletedRow = deleteCallLogEntry(context, id.toLong())
                            if (deletedRow > 0) {
                                Log.i(TAG, "deleteLastCallLogEntry: CALL LOG IS DELETED")
                            }
                        }
                    }
                }, 1500)


            } catch (e: SecurityException) {
                e.printStackTrace()
                // Handle the exception (e.g., request necessary permissions) as needed.
            }
        }


        @SuppressLint("MissingPermission")
        fun getDeviceIsConnected(): Boolean {
            val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            return (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled
                    && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothAdapter.STATE_CONNECTED)
        }

        fun getRecordingFile(FileName: String): File? {

            // Get the external storage directory
            val externalStorageDirectory = Environment.getExternalStorageDirectory()

            // Create the directory if it doesn't exist
            val DIRECTORY_NAME = "CallRecordings"
            val callRecordingsDirectory = File(externalStorageDirectory, DIRECTORY_NAME)
            if (!callRecordingsDirectory.exists()) {
                callRecordingsDirectory.mkdirs()
            }
            var filname = "$FileName.m4a"
            // Create the file for the call recording
            var callRecordingFile = File(callRecordingsDirectory, filname)
            try {
                // Create a new file
                if (callRecordingFile.exists()) {
                    count++
                    filname = "$FileName($count).m4a"
                    callRecordingFile = File(callRecordingsDirectory, filname)
                    return callRecordingFile
                } else {
                    if (callRecordingFile.createNewFile()) {
                        // File successfully created
                        return callRecordingFile
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null // Return null if file creation fails
        }


        @SuppressLint("Range")
        fun getContactList(context: Context): List<Contact> {
            val db = DatabaseHelper(context)
            if (db.getAllContacts().isEmpty()) {
                val scope = CoroutineScope(Dispatchers.IO)
                loadContactFromStorage(context, db)

            } else {
                return db.getAllContacts()
            }
            return ArrayList()
        }


        @SuppressLint("Range")
        private fun loadContactFromStorage(context: Context, db: DatabaseHelper) {
            val contentResolver = context.contentResolver
            val contactsUri = ContactsContract.Contacts.CONTENT_URI
            val sortOrder = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}  ASC"
            val projection = arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
            )
            val cursor = contentResolver.query(contactsUri, projection, null, null, sortOrder)
            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val contactId =
                        cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val contactName =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    // Retrieve phone numbers associated with the contact
                    val contactNumbers = getContactNumbers(context, contactId)
                    val number = contactNumbers.substringBefore(",").replace("-", "").trim()
                    if (contactName == null) {
                        Log.e(TAG, "getContactList: one contact is null")
                    } else {
                        val contact = Contact(contactId.toInt(), contactName, number)
                        db.addContact(contact)
                        Log.i(TAG, "loadContactFromStorage: one Contact added : $contact")
                    }
                }
                cursor.close()
            }
        }

        @SuppressLint("Range")
        private fun getContactNumbers(context: Context, contactId: Long): String {
            val numbers = java.lang.StringBuilder()
            val contentResolver = context.contentResolver
            val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )
            val phoneCursor = contentResolver.query(
                phoneUri,
                projection,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                arrayOf(contactId.toString()),
                null
            )
            if (phoneCursor != null) {
                while (phoneCursor.moveToNext()) {
                    val phoneNumber =
                        phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    numbers.append(phoneNumber).append(", ")
                }
                phoneCursor.close()
            }
            return numbers.toString()
        }

        @SuppressLint("Range")
        fun getCallerName(context: Context, number: String): String {

            var callerName: String? = null

            val uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number)
            )
            val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

            val cursor: Cursor = context.contentResolver.query(uri, projection, null, null, null)!!

            if (cursor.moveToFirst()) {
                callerName =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
                cursor.close()
            }

            if (callerName == null) {
                callerName = "Unknown Caller" // Set a default or placeholder name
            }
            return callerName
        }
    }
}