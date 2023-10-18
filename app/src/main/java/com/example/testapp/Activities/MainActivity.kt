package com.example.testapp.Activities

import android.Manifest
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.callingapp.Utils.Utils
import com.example.testapp.Adapter.ContactAdapter
import com.example.testapp.CallProvides.CallManager
import com.example.testapp.Models.Contact
import com.example.testapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), ContactAdapter.number {

    lateinit var adapter: ContactAdapter
    val TAG = "MainActivity"
    private val REQUEST_ID: Int = 2
    lateinit var CallManager: CallManager
    private val PERMISSION_REQUEST_CODE = 100
    lateinit var _binding: ActivityMainBinding
    val binding: ActivityMainBinding
        get() = _binding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        CallManager = CallManager(this)
        requestRole()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun check(): Boolean {
        val manageOwnCallsPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_OWN_CALLS)
        return if (manageOwnCallsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.MANAGE_OWN_CALLS,
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.ANSWER_PHONE_CALLS,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.WRITE_CALL_LOG,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_CODE
            )
            Toast.makeText(this, "manage Own Calls is not Granted", Toast.LENGTH_SHORT).show()

            false
        } else {
            //createPhoneAccount()
            Log.i(TAG, "check: Permission is Granted...")
            displayData()
            true
        }
    }

    fun requestRole() {
        val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
        startActivityForResult(intent, REQUEST_ID)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ID) {
            if (resultCode == RESULT_OK) {
                check()
            } else {

            }
        }
    }

    fun displayData() {
        val items = Utils.getContactList(this)
        adapter = ContactAdapter(
            items, this
        )
        binding.contactList.adapter = adapter
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    @RequiresApi(34)
    override fun passdata(data: Contact) {
        CallManager.startOutgoingCall(data.number)
    }
}
