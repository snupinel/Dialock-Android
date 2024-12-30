package com.example.dailysummary.viewModel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.dailysummary.data.PrefRepository
import com.example.dailysummary.overlay.OverlayReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class OverlayViewModel @Inject constructor(
    private val prefRepository: PrefRepository
) : ViewModel() {

    fun scheduleOverlayTask(context: Context) {
        //scheduleOverlay(context, prefRepository)
    }
}

/*
fun scheduleOverlay(context: Context, prefRepository: PrefRepository) {
    // AlarmManager 생성
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // 23시 00분을 설정
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        // 현재 시간이 이미 23시를 지난 경우 다음 날로 예약
        if (before(Calendar.getInstance())) {
            add(Calendar.DATE, 1)
        }
    }



    // PendingIntent 설정 (BroadcastReceiver를 호출)
    val intent = Intent(context, OverlayReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
        if(alarmManager.canScheduleExactAlarms()){
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

        }
    } else {
        // Pre-Android 12, directly schedule the alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
    // AlarmManager에 작업 예약
    //prefRepository.setPref("")

}*/