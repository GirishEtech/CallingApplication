package com.example.testapp.Activities

import android.accessibilityservice.AccessibilityButtonController
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {
    private var mAccessibilityButtonController: AccessibilityButtonController? = null
    private var accessibilityButtonCallback:
            AccessibilityButtonController.AccessibilityButtonCallback? = null
    private var mIsAccessibilityButtonAvailable: Boolean = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        mAccessibilityButtonController = accessibilityButtonController
        mIsAccessibilityButtonAvailable =
            mAccessibilityButtonController?.isAccessibilityButtonAvailable ?: false

        if (!mIsAccessibilityButtonAvailable) return

        serviceInfo = serviceInfo.apply {
            flags = flags or AccessibilityServiceInfo.FLAG_REQUEST_ACCESSIBILITY_BUTTON
        }

        accessibilityButtonCallback =
            object : AccessibilityButtonController.AccessibilityButtonCallback() {
                override fun onClicked(controller: AccessibilityButtonController) {
                    Log.d("MY_APP_TAG", "Accessibility button pressed!")

                    // Add custom logic for a service to react to the
                    // accessibility button being pressed.
                }

                override fun onAvailabilityChanged(
                    controller: AccessibilityButtonController,
                    available: Boolean
                ) {
                    if (controller == mAccessibilityButtonController) {
                        mIsAccessibilityButtonAvailable = available
                    }
                }
            }

        accessibilityButtonCallback?.also {
            mAccessibilityButtonController?.registerAccessibilityButtonCallback(it, null!!)
        }
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {

    }

    override fun onInterrupt() {

    }
}