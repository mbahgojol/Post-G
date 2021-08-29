package com.blank.mydiary.utils

import android.content.Context
import android.os.Build
import androidx.core.os.bundleOf
import com.blank.mydiary.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticFirebase(private val context: Context) {
    private val eventListener = FirebaseAnalytics.getInstance(context)

    fun isNotifShow(isShow: Boolean) {
        eventListener.logEvent(
            "alarm", bundleOf(
                "notif" to isShow,
                "device" to context.getDeviceId(),
                "versionSDK" to Build.VERSION.SDK_INT,
                "versionApk" to BuildConfig.FLAVOR,
                "packageName" to BuildConfig.APPLICATION_ID
            )
        )
    }

    fun isBroadcastActive() {
        eventListener.logEvent(
            "broadcast", bundleOf(
                "device" to context.getDeviceId(),
                "versionSDK" to Build.VERSION.SDK_INT,
                "versionApk" to BuildConfig.FLAVOR,
                "packageName" to BuildConfig.APPLICATION_ID
            )
        )
    }
}