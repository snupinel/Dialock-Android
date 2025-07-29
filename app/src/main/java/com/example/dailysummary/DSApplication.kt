package com.example.dailysummary

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.example.dailysummary.overlay.AlarmScheduler
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DSApplication :Application(){

    @Inject
    lateinit var alarmScheduler:AlarmScheduler


    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this) // 초기화
        alarmScheduler.scheduleOverlay()
    }
}