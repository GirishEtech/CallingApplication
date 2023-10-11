package com.example.testapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.role.RoleManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.testapp.databinding.ActivityMainBinding
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    private val REQUEST_ID: Int = 2
    private val PERMISSION_REQUEST_CODE = 100
    lateinit var _binding: ActivityMainBinding
    val binding: ActivityMainBinding
        get() = _binding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                    Manifest.permission.CALL_PHONE
                ),
                PERMISSION_REQUEST_CODE
            )
            Toast.makeText(this, "manage Own Calls is not Granted", Toast.LENGTH_SHORT).show()

            false
        } else {
            //createPhoneAccount()
            Toast.makeText(this, "manage Own Calls is Granted", Toast.LENGTH_SHORT).show()
            createPhoneAccount()
            true
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun createPhoneAccount() {
        val manager = getSystemService(TelecomManager::class.java)
        val componentName =
            ComponentName(packageName, CallService::class.java.name)
        val phoneAccountHandle = PhoneAccountHandle(componentName, "UUID")
        val phoneAccount = PhoneAccount.Builder(
            phoneAccountHandle,
            "MY LABEL"
        )
            .setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED)
            .setIcon(Icon.createWithResource(this, R.drawable.ic_launcher_background))
            .build()
        manager.registerPhoneAccount(phoneAccount)

        showPhoneAccount()

        val ac =
            manager.getPhoneAccount(phoneAccountHandle)
        var permission = manager.isOutgoingCallPermitted(
            phoneAccountHandle

        )
        Log.i(TAG, "createPhoneAccount: PERMISSION FOR OUTGOING CALL $permission")
        permission = manager.isIncomingCallPermitted(phoneAccountHandle)
        Log.i(TAG, "createPhoneAccount: PERMISSION FOR INCOMING CALL $permission")
        if (ac != null && ac.isEnabled) {
            PlaceCall("+919106559673", phoneAccountHandle)
        } else {
            Toast.makeText(this, "Permission is not Granted", Toast.LENGTH_SHORT)
                .show()
        }

    }

    @SuppressLint("MissingPermission")
    fun PlaceCall(Number: String, phoneAccountHandle: PhoneAccountHandle) {
        val manager = getSystemService(TelecomManager::class.java)
        val bundle = Bundle()
        bundle.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true)
        bundle.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
        binding.btnCall.setOnClickListener {
            val uri= Uri.parse("tel:$Number")
            manager.placeCall(uri, bundle)
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
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

    fun showPhoneAccount() {
        val intent = Intent()
        intent.component = ComponentName(
            "com.android.server.telecom",
            "com.android.server.telecom.settings.EnableAccountPreferenceActivity"
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}