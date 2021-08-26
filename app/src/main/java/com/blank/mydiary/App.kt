package com.blank.mydiary

import android.app.Application
import com.blank.mydiary.service.AlarmReceiver
import com.google.firebase.FirebaseApp

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)

        val alarmReceiver = AlarmReceiver()
        if (!alarmReceiver.isAlarmOn(this))
            alarmReceiver.setRepeatingAlarm(this)
    }
}