package com.example.dailysummary.dto

enum class AdviceOrForcing {
    Advice,Forcing
}
data class Setting(
    val adviceOrForcing:AdviceOrForcing,
    val sameEveryDay:Boolean,
    val alarmTimesByDay:List<AlarmTime>,

)
data class AlarmTime(
    val hour:Int,
    val minute:Int,
    val isNextDay:Boolean = hour<12,
)
data class GroupedAlarmEntry(
    val alarmTime: AlarmTime,
    val dayList: List<Int>,
)

val SAMPLE_ALARM_TIME = AlarmTime(0,0)
val SAMPLE_SETTING = Setting(
    AdviceOrForcing.Advice,
    false,
    List(7){ SAMPLE_ALARM_TIME}
)
val SAMPLE_GROUPED_ALARM_ENTRY = GroupedAlarmEntry(
    alarmTime = SAMPLE_ALARM_TIME,
    dayList = emptyList(),
)

