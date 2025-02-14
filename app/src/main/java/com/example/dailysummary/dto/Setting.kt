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
    val isNextDay:Boolean,
)

val DEFAULT_ALARMTIME = AlarmTime(0,0,false)