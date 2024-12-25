package com.example.dailysummary.dto

enum class AdviceOrForcing {
    Advice,Forcing
}
data class Setting(
    val adviceOrForcing:AdviceOrForcing,
    val sameEveryDay:Boolean,
    val alarmTimes:List<Pair<Int,Int>>,

)