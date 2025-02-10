package com.example.dailysummary

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.dailysummary.overlay.AlarmScheduler
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DSApplication :Application(){

    @Inject
    lateinit var alarmScheduler:AlarmScheduler

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this) // 초기화
        alarmScheduler.scheduleOverlay()
    }
}