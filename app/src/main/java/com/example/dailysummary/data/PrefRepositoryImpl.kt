package com.example.dailysummary.data

import android.content.Context
import android.util.Log
import com.example.dailysummary.dto.AdviceOrForcing
import com.example.dailysummary.dto.AlarmTime
import com.example.dailysummary.dto.SAMPLE_ALARM_TIME
import com.example.dailysummary.dto.Setting
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.StringBuilder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
): PrefRepository {

    private val sharedPref = context.getSharedPreferences(
        "DS",
        Context.MODE_PRIVATE
    )
    override fun getPref(key: String): String? {
        return sharedPref.getString(key, null)
    }
    override fun setPref(key: String,data: String) {
        with(sharedPref.edit()){
            putString(key,data)
            apply()
        }
    }

    override fun clearPref(key: String) {
        with(sharedPref.edit()){
            this.remove(key)
            apply()
        }
    }

    override fun setRefSetting(setting: Setting){
        val builder= StringBuilder()
        builder.append(setting.adviceOrForcing.name+" ")
        builder.append(setting.sameEveryDay.toString()+" ")

        builder.append("${setting.defaultAlarmTime.hour} ${setting.defaultAlarmTime.minute} ")
        setting.alarmTimesByDay.forEach{
            builder.append("${it.hour} ${it.minute} ${it.isGrouped} ")
        }
        setPref("Setting",builder.toString())

        Log.d("spref","setting set:${setting}")
    }
    override fun getRefSetting(): Setting?{
        val refList=getPref("Setting")?.trimEnd()?.split(" ")?: emptyList()

        Log.d("spref","setting :${refList}")

        if(refList.isEmpty()) return null

        val adviceOrForcing= AdviceOrForcing.valueOf(refList[0])
        val sameEveryDay=refList[1].toBoolean()
        val defaultAlarmTime = AlarmTime(refList[2].toInt(),refList[3].toInt(), isGrouped = false)

        var alarmTimes=refList.drop(4).chunked(3).map{
                (first, second, third) -> AlarmTime(first.toInt(),second.toInt(), isGrouped = third.toBoolean())
        }

        if(alarmTimes.size<7) alarmTimes= alarmTimes+List(7-alarmTimes.size){ SAMPLE_ALARM_TIME}

        return Setting(adviceOrForcing, sameEveryDay, defaultAlarmTime,alarmTimes)
    }

}