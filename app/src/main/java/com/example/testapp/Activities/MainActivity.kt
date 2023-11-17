package com.example.testapp.Activities

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.callingapp.Utils.Messages
import com.example.callingapp.Utils.Utils
import com.example.testapp.Adapter.ContactAdapter
import com.example.testapp.CallProvides.CallManager
import com.example.testapp.Fragments.ContactFragment
import com.example.testapp.Fragments.RecentCallsFragment
import com.example.testapp.PreferenceManager
import com.example.testapp.R
import com.example.testapp.RoomDatabase.Contact
import com.example.testapp.Utils.NotificationManager
import com.example.testapp.Utils.RingtoneManage
import com.example.testapp.databinding.ActivityMainBinding


class MainActivity : BaseActivity(), ContactAdapter.number {

    lateinit var preferences: PreferenceManager
    var permissionsList: ArrayList<String>? = null
    private val handler = Handler(Looper.getMainLooper())

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    var permissionsStr = arrayOf(
        Manifest.permission.MANAGE_OWN_CALLS,
        Manifest.permission.READ_PHONE_NUMBERS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.ANSWER_PHONE_CALLS,
        Manifest.permission.MODIFY_AUDIO_SETTINGS,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_CONTACTS,
        WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
    )
    var permissionsCount = 0

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private var permissionLaunder =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val list: ArrayList<Any?> = ArrayList(it.values)
            permissionsList = ArrayList()
            permissionsCount = 0

            for (i in list.indices) {
                if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                    permissionsList!!.add(permissionsStr[i])
                } else if (!hasPermission(this@MainActivity, permissionsStr[i])) {
                    permissionsCount++
                }
            }
            if (permissionsList!!.size > 0) {
                askForPermissions(permissionsList!!)

            } else if (permissionsCount > 0) {
                Messages.showPermissionDialog(this, "PERMISSION", "permission is required")

            } else {
                Toast.makeText(this, "All Permission is Granted", Toast.LENGTH_SHORT).show()
                displayData()
            }

        }

    val TAG = "MainActivity"
    private val REQUEST_ID: Int = 2
    lateinit var CallManager: CallManager
    lateinit var _binding: ActivityMainBinding
    val binding: ActivityMainBinding
        get() = _binding

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = PreferenceManager(
            this
        )

        val isLog = intent.getBooleanExtra("isLog", false)
        if (isLog != null) {
            if (isLog) {
                deleteLog()
            }
        }
        if (intent!!.action == "${packageName}.DECLINE") {
            deleteLog()
            RingtoneManage.getInstance(this).StopRing()
            val telecomManager = getSystemService(TelecomManager::class.java)
            val notificationManager = NotificationManager(this)
            notificationManager.dismiss()
            telecomManager.endCall()
        }
        CallManager = CallManager(this)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            requestRole()
        } else {
            offerReplacingDefaultDialer()
        }
        permissionsList = ArrayList()
        permissionsList!!.addAll(listOf(*permissionsStr))
    }

    private fun offerReplacingDefaultDialer() {
        if (getSystemService(TelecomManager::class.java).defaultDialerPackage !== packageName) {
            val ChangeDialer = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
            ChangeDialer.putExtra(
                TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                packageName
            )
            startActivity(ChangeDialer)
        }
    }

    private fun hasPermission(context: Context, permissionStr: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permissionStr
        ) == PackageManager.PERMISSION_GRANTED
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun requestRole() {
        val roleManager = getSystemService(RoleManager::class.java)
        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
        startActivityForResult(intent, REQUEST_ID)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ID) {
            if (resultCode == RESULT_OK) {
                permissionLaunder.launch(permissionsStr)
            }
        }
    }

    fun displayData() {
        loadFragment(RecentCallsFragment())
        binding.buttomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itemContacts -> {
                    loadFragment(ContactFragment())
                }

                R.id.itemRecents -> {
                    loadFragment(RecentCallsFragment())
                }
            }
            true
        }

    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }


    @RequiresApi(34)
    override fun passdata(data: Contact) {
        Messages.showAlertForNextStep(
            this,
            "Information",
            "Are You sure you want Call",
        ) {
            CallManager.startOutgoingCall(data.number)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun askForPermissions(permissionsList: ArrayList<String>) {
        val newPermissionStr = arrayOfNulls<String>(permissionsList.size)
        for (i in newPermissionStr.indices) {
            newPermissionStr[i] = permissionsList[i]
        }
        if (newPermissionStr.isNotEmpty()) {
            permissionLaunder.launch(newPermissionStr as Array<String>)
        } else {
            Messages.showPermissionDialog(this, "PERMISSION", "Permission is Empty")
        }
    }

    private fun deleteLog() {
        if (checkPermission()) {
            Utils.deleteLastCallLogEntry(
                this@MainActivity, handler
            )

        } else {
            checkPermission()
        }
    }

    fun checkPermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_CALL_LOG, Manifest.permission.READ_CALL_LOG),
                10
            )
            false
        } else {
            true
        }
    }


}
