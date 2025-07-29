package com.example.dailysummary.overlay

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.dailysummary.data.PrefRepository
import com.example.dailysummary.dto.AdviceOrForcing
import com.example.dailysummary.dto.Setting
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.DateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefRepository: PrefRepository
) {


    fun scheduleOverlay() {
        Log.d("alarm", "scheduleOverlay activated")

        val setting = prefRepository.getRefSetting() ?: return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        setting.alarmTimesByDay.forEachIndexed { index, time ->
            Log.d("alarmscheduler", "${index + 1}번째 요일 세팅: $time")

            val now = Calendar.getInstance()
            val futureLimit = Calendar.getInstance().apply { add(Calendar.DATE, 7) }

            val calendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, index + 1)
                set(Calendar.HOUR_OF_DAY, time.hour)
                set(Calendar.MINUTE, time.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                if (time.isNextDay) add(Calendar.DATE, 1)
                if (before(now)) add(Calendar.DATE, 7)
                if (after(futureLimit)) add(Calendar.DATE, -7)
            }

            val broadcastIntent = Intent("com.example.dailysummary.ACTION_ALARM_TRIGGER").apply {
                setPackage(context.packageName)
                putExtra("year", calendar.get(Calendar.YEAR))
                putExtra("month", calendar.get(Calendar.MONTH) + 1)
                putExtra("day", calendar.get(Calendar.DAY_OF_MONTH))
                putExtra("isNextDay", time.isNextDay)
                putExtra("isInstant", false)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                index,
                broadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)

            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )

                val formattedDate = DateFormat.getDateTimeInstance().format(calendar.time)
                Log.d("alarm", "${index + 1}번째 요일 알람 설정 완료: $formattedDate")

            } catch (e: SecurityException) {
                Log.e("alarm", "정확한 알람 권한이 없어 예약 실패", e)
            }
        }
    }
    fun scheduleInstantOverlay(dateTime: LocalDateTime){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, dateTime.year)
            set(Calendar.MONTH, dateTime.monthValue - 1) // Calendar는 0-based month
            set(Calendar.DAY_OF_MONTH, dateTime.dayOfMonth)
            set(Calendar.HOUR_OF_DAY, dateTime.hour)
            set(Calendar.MINUTE, dateTime.minute)
            set(Calendar.SECOND, dateTime.second)
            set(Calendar.MILLISECOND, 0)
        }

        val broadcastIntent = Intent("com.example.dailysummary.ACTION_ALARM_TRIGGER").apply {
            setPackage(context.packageName)
            putExtra("year", calendar.get(Calendar.YEAR))
            putExtra("month", calendar.get(Calendar.MONTH) + 1)
            putExtra("day", calendar.get(Calendar.DAY_OF_MONTH))
            putExtra("isNextDay", calendar.get(Calendar.HOUR_OF_DAY)<12)
            putExtra("isInstant", true)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            7,//0~6에 포함되지 않는 채널
            broadcastIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            val formattedDate = DateFormat.getDateTimeInstance().format(calendar.time)
            Log.d("alarm", "비 요일 알람 설정 완료: $formattedDate")

        } catch (e: SecurityException) {
            Log.e("alarm", "정확한 알람 권한이 없어 예약 실패", e)
        }
    }
    fun scheduleInstantOverlayAfter(delayMillis: Long) {
        val futureDateTime = Instant
            .now()
            .plusMillis(delayMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

        scheduleInstantOverlay(futureDateTime)
    }



}