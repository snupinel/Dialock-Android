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
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefRepository: PrefRepository
) {


    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleOverlay() {
        Log.d("alarm","scheduleOverlay activated")

        // SharedPreferences에서 데이터를 가져옴
        var setting = prefRepository.getRefSetting() ?: return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


        // 시간 정보가 없으면 종료
        //setting.alarmTimes
        //val timeParts = timeString.split(":")
        //if (timeParts.size != 2) return // 잘못된 시간 형식이면 종료
        if(setting.sameEveryDay) setting = setting.copy(alarmTimesByDay = List(7){setting.defaultAlarmTime})

        setting.alarmTimesByDay.forEachIndexed { index, time ->
            Log.d("alarmscheduler", "${index+1} 번째 요일 의 세팅 엔트리:\n" +
                    "$time")
            val now = Calendar.getInstance() // 현재 시간
            val futureLimit = Calendar.getInstance().apply { add(Calendar.DATE, 7) } // 현재 + 7일

            val calendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, index + 1)
                set(Calendar.HOUR_OF_DAY, time.hour)
                set(Calendar.MINUTE, time.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                // ✅ isNextDay가 true이면 하루 추가
                if (time.isNextDay) {
                    add(Calendar.DATE, 1)
                }

                // ✅ 설정 시간이 현재보다 이전이면 다음 주로 이동
                if (before(now)) {
                    add(Calendar.DATE, 7)
                }

                // ✅ 설정 시간이 현재 + 7일 이후라면 7일 빼기
                if (after(futureLimit)) {
                    add(Calendar.DATE, -7)
                }
            }


            val intent = Intent(context, SummaryService::class.java).apply {
                putExtra("year", calendar.get(Calendar.YEAR))
                putExtra("month", calendar.get(Calendar.MONTH)+1)
                putExtra("day", calendar.get(Calendar.DAY_OF_MONTH))
                putExtra("isNextDay", time.isNextDay)
            }

            //Log.d("aaaa","year:${calendar.get(Calendar.YEAR)}")

            val pendingIntent = PendingIntent.getService(
                context,
                index,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
                Log.d("alarm", "android version is 12+")
                if(alarmManager.canScheduleExactAlarms()){
                    Log.d("alarm", "can")
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )

                }else{
                    requestExactAlarmPermission(context)
                }
            } else {
                // Pre-Android 12, directly schedule the alarm
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }

            val dateFormat = DateFormat.getDateTimeInstance()
            val formattedDate = dateFormat.format(calendar.time)
            Log.d("alarm","${index+1}번째 요일 알람 설정 완료\n" +
                    "설정 시간:${formattedDate}")
            // PendingIntent 정보를 SharedPreferences에 저장
            //prefRepository.setPref("alarm_pending_intent", "0") // 고유 ID 또는 기타 정보 저장
        }


    }


    fun requestExactAlarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = android.net.Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // 플래그 추가
            }
            context.startActivity(intent)
        } else {
            Log.d("alarm", "Exact alarm permission is not required for this Android version.")
        }
    }
}