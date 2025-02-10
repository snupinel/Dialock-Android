package com.example.dailysummary.overlay

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import javax.inject.Inject

class AlarmResetReceiver @Inject constructor(
    private val alarmScheduler: AlarmScheduler,
) : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_MY_PACKAGE_REPLACED || intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("AlarmResetReceiver", "앱 업데이트 또는 부팅 후 알람 재설정 시작")
            alarmScheduler.scheduleOverlay()  // ✅ AlarmScheduler의 기존 설정을 사용하여 알람 재설정
        }
    }
}