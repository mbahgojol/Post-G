package com.blank.mydiary

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.blank.mydiary.service.AlarmReceiver
import com.blank.mydiary.utils.TypefaceUtil
import com.google.firebase.FirebaseApp

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        TypefaceUtil.overrideFont(applicationContext)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        FirebaseApp.initializeApp(applicationContext)

        val alarmReceiver = AlarmReceiver()
        if (!alarmReceiver.isAlarmOn(this))
            alarmReceiver.setRepeatingAlarm(this)
    }
}