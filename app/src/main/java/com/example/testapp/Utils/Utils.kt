package com.example.callingapp.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import com.example.testapp.Models.Contact


class Utils {
    companion object {
        private val TAG = "Utils"

        @SuppressLint("Range")
        fun getContactList(context: Context): ArrayList<Contact> {
            val contentResolver = context.contentResolver
            val contactsUri = ContactsContract.Contacts.CONTENT_URI
            val sortOrder = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}  ASC"
            val projection = arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
            )
            val contactList = ArrayList<Contact>()
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
                    Log.d(TAG, "getContactList: Name : $contactName \n Number : $number")
                    if (contactName == null) {
                        Log.e(TAG, "getContactList: one contact is null")
                    } else {
                        contactList.add(Contact(contactName, number))

                    }
                }
                cursor.close()
            }
            return contactList
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