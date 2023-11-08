package com.example.callingapp.Utils

import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Messages {
    companion object {
        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun showPermissionDialog(context: Context, title: String, messages: String) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
                .setMessage(messages)
                .setPositiveButton("Ok") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            if (Utils.alertDialog == null) {
                Utils.alertDialog = builder.create()
                if (!Utils.alertDialog!!.isShowing) {
                    Utils.alertDialog!!.show()
                }
            }
        }

        @RequiresApi(34)
        fun showAlertForNextStep(
            context: Context,
            title: String,
            messages: String,
            number: String,
            function: () -> Unit
        ) {
            MaterialAlertDialogBuilder(context).setMessage(messages)
                .setTitle(title)
                .setPositiveButton(
                    "Ok"
                ) { dialog, which ->

                    function.invoke()
                }
                .setNegativeButton(
                    "Cencel"
                ) { dialog, which ->

                    dialog.dismiss()
                }.show()
        }
    }
}