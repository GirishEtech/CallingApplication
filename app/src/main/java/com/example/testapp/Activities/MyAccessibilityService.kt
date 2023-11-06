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
    val TAG = "MyAccessibilityService"

    override fun onServiceConnected() {
        super.onServiceConnected()
        mAccessibilityButtonController = accessibilityButtonController
        mIsAccessibilityButtonAvailable =
            mAccessibilityButtonController?.isAccessibilityButtonAvailable ?: false

        if (!mIsAccessibilityButtonAvailable) return

        serviceInfo = serviceInfo.apply {
            flags = flags or AccessibilityServiceInfo.FLAG_REQUEST_ACCESSIBILITY_BUTTON
            eventTypes =
                AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_VIEW_FOCUSED
            packageNames =
                arrayOf("com.example.testapp")
            feedbackType = AccessibilityServiceInfo.FEEDBACK_AUDIBLE

        }

        accessibilityButtonCallback =
            object : AccessibilityButtonController.AccessibilityButtonCallback() {
                override fun onClicked(controller: AccessibilityButtonController) {
                    Log.d("MY_APP_TAG", "Accessibility button pressed!")
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

    override fun onAccessibilityEvent(AccessibilityEvent: AccessibilityEvent?) {
        Log.i(TAG, "onAccessibilityEvent: Event $AccessibilityEvent ")
    }

    override fun onInterrupt() {
        Log.i(TAG, "onInterrupt: on Interrupt Called")
    }
}