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
import android.telecom.TelecomManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.example.callingapp.Utils.Messages
import com.example.callingapp.Utils.Utils
import com.example.testapp.Adapter.ContactAdapter
import com.example.testapp.CallProvides.CallManager
import com.example.testapp.Models.Contact
import com.example.testapp.Utils.NotificationManager
import com.example.testapp.databinding.ActivityMainBinding


class MainActivity : BaseActivity(), ContactAdapter.number {

    var permissionsList: ArrayList<String>? = null

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


    lateinit var items: List<Contact>
    lateinit var adapter: ContactAdapter
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
        if (intent!!.action == "${packageName}.DECLINE") {
            val telecomManager = getSystemService(TelecomManager::class.java)
            val notificationManager = NotificationManager(this)
            notificationManager.dismiss()
            telecomManager.endCall()
            finishAffinity()
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
        val items = Utils.getContactList(this)
        if (items.isEmpty()) {
            Log.e(TAG, "displayData: item is Empty")
            val data = Utils.getContactList(this)
            setData(data)
        } else {
            setData(items)
        }

    }

    private fun setData(items: List<Contact>) {
        if (items.isEmpty()) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            this.items = items
            binding.progressBar.visibility = View.INVISIBLE
            adapter = ContactAdapter(
                items, this
            )
            binding.contactList.adapter = adapter
            binding.searchView.setIconifiedByDefault(false)
            binding.contactList.visibility = View.VISIBLE
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

    }

    @RequiresApi(34)
    override fun passdata(data: Contact) {
        Messages.showAlertForNextStep(
            this,
            "Information",
            "Are You sure you want Call",
            data.number
        )
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


}
