package com.example.callingapp.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.ContactsContract
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.testapp.CallProvides.CallManager
import com.example.testapp.Models.Contact
import com.example.testapp.Utils.DatabaseHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.IOException


class Utils {

    companion object {
        var alertDialog: AlertDialog? = null
        private val TAG = "Utils"
        var count = 0

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

        fun showPermissionDialog(context: Context, title: String, messages: String) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
                .setMessage(messages)
                .setPositiveButton("Ok") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            if (alertDialog == null) {
                alertDialog = builder.create()
                if (!alertDialog!!.isShowing) {
                    alertDialog!!.show()
                }
            }
        }

        @RequiresApi(34)
        fun showAlertForNextStep(
            context: Context,
            title: String,
            messages: String,
            number: String
        ) {
            MaterialAlertDialogBuilder(context).setMessage(messages)
                .setTitle(title)
                .setPositiveButton(
                    "Ok"
                ) { dialog, which ->
                    val manager = CallManager(context)
                    manager.startOutgoingCall(number)
                }
                .setNegativeButton(
                    "Cencel"
                ) { dialog, which ->

                    dialog.dismiss()
                }.show()
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