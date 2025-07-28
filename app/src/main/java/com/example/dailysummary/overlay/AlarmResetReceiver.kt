package com.example.dailysummary.overlay

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.dailysummary.data.PrefRepository
import com.example.dailysummary.data.SummaryRepository
import javax.inject.Inject

class AlarmResetReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                Log.d("AlarmResetReceiver", "앱 업데이트 또는 부팅 후 알람 재설정 시작")
                alarmScheduler.scheduleOverlay()
            }

            "com.example.dailysummary.ACTION_ALARM_TRIGGER" -> {
                Log.d("AlarmResetReceiver", "알람 트리거 도착")

                val activityIntent = Intent(context, MyOverlayActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK  // 필수
                    putExtra("year", intent.getIntExtra("year", 0))
                    putExtra("month", intent.getIntExtra("month", 0))
                    putExtra("day", intent.getIntExtra("day", 0))
                    putExtra("isNextDay", intent.getBooleanExtra("isNextDay", false))
                }

                context.startActivity(activityIntent)
            }
        }
    }
}
