package com.example.dailysummary

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DSApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this) // 초기화
    }
}