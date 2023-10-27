package com.example.testapp.Activities

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.role.RoleManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
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
                showPermissionDialog("PERMISSION", "permission is required")

            } else {
                Toast.makeText(this, "All Permission is Granted", Toast.LENGTH_SHORT).show()
                displayData()
            }

        }


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
        permissionsList = ArrayList()
        permissionsList!!.addAll(listOf(*permissionsStr))
    }

    private fun hasPermission(context: Context, permissionStr: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permissionStr
        ) == PackageManager.PERMISSION_GRANTED
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
                    Manifest.permission.CAPTURE_AUDIO_OUTPUT,
                    WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
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
        CallManager.startOutgoingCall(data.number)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun askForPermissions(permissionsList: ArrayList<String>) {
        val newPermissionStr = arrayOfNulls<String>(permissionsList.size)
        for (i in newPermissionStr.indices) {
            newPermissionStr[i] = permissionsList[i]
        }
        if (newPermissionStr.isNotEmpty()) {
            permissionLaunder.launch(newPermissionStr as Array<String>)
        } else {
            showPermissionDialog("PERMISSION", "Permission is Empty")
        }
    }

    var alertDialog: AlertDialog? = null

    private fun showPermissionDialog(title: String, messages: String) {
        val builder = AlertDialog.Builder(this)
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

}
