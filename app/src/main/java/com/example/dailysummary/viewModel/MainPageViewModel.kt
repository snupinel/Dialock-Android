package com.example.dailysummary.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailysummary.data.PrefRepository
import com.example.dailysummary.data.SummaryRepository
import com.example.dailysummary.dto.AdviceOrForcing
import com.example.dailysummary.dto.Setting
import com.example.dailysummary.dto.Summary
import com.example.dailysummary.model.CalenderEntry
import com.example.dailysummary.overlay.AlarmScheduler
import com.example.dailysummary.overlay.MyService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.StringBuilder
import java.time.LocalDate
import javax.inject.Inject

enum class Tab{
    Calender,Setting
}

@HiltViewModel
@SuppressLint("NewApi")
class MainPageViewModel @Inject constructor(
    private val prefRepository: PrefRepository,
    private val alarmScheduler: AlarmScheduler,
    private val summaryRepository: SummaryRepository,
    ):ViewModel(){

    private val _selectedTab = MutableStateFlow(Tab.Calender)
    val selectedTab = _selectedTab.asStateFlow()

    fun updateTab(tab:String){
        _selectedTab.value = Tab.valueOf(tab)
    }

    @SuppressLint("NewApi")
    val currentDate = LocalDate.now() // 현재 날짜 가져오기
    @SuppressLint("NewApi")
    val currentYear = currentDate.year // 현재 연도
    @SuppressLint("NewApi")
    val currentMonth = currentDate.monthValue // 현재 월 (숫자로)

    private val _selectedYearAndMonth = MutableStateFlow(Pair(currentYear,currentMonth))
    val selectedYearAndMonth = _selectedYearAndMonth.asStateFlow()

    fun setSelectedYearAndMonth(year:Int,month: Int){
        _selectedYearAndMonth.value= Pair(year,month)
        calenderRefresh()
    }

    fun prevMonth(){
        if(selectedYearAndMonth.value.second==1)
            setSelectedYearAndMonth(selectedYearAndMonth.value.first-1,12)
        else setSelectedYearAndMonth(selectedYearAndMonth.value.first,selectedYearAndMonth.value.second-1)
    }

    fun nextMonth(){
        if(selectedYearAndMonth.value.second==12)
            setSelectedYearAndMonth(selectedYearAndMonth.value.first+1,1)
        else setSelectedYearAndMonth(selectedYearAndMonth.value.first,selectedYearAndMonth.value.second+1)
    }

    /*
    val summarySamples = listOf(
        Summary(
            writtenTime = LocalDate.of(2024,12,26),
            date = LocalDate.of(2024,12,26),
            content = "박성학 접선"),
        Summary(
            writtenTime = LocalDate.of(2024,12,24),
            date = LocalDate.of(2024,12,24),
            content = "솔크"),
        Summary(
            writtenTime = LocalDate.of(2024,12,31),
            date = LocalDate.of(2024,12,31),
            content = "솔크"),
        Summary(
            writtenTime = LocalDate.of(2024,12,25),
            date = LocalDate.of(2024,12,25),
            content = "솔크"),
        Summary(
            writtenTime = LocalDate.of(2024,12,1),
            date = LocalDate.of(2024,12,1),
            content = "솔크"),


    )*/

    private val _currentMonthSummaries = MutableStateFlow(listOf<Summary>())
    //private val _currentMonthSummaries = MutableStateFlow(summarySamples)

    val currentMonthSummaries = _currentMonthSummaries.asStateFlow()

    fun setCurrentMonthSummaries(list:List<Summary>){
        _currentMonthSummaries.value=list
        Log.d("currentsum","list:$list")
    }

    private val _calenderEntries = MutableStateFlow(listOf<CalenderEntry>())

    val calenderEntries = _calenderEntries.asStateFlow()

    fun setCalenderEntries(){
        val list = mutableListOf<CalenderEntry>()
        val frontBlankCount=LocalDate.of(selectedYearAndMonth.value.first,selectedYearAndMonth.value.second,1).dayOfWeek.value%7
        repeat(frontBlankCount){
            list.add(CalenderEntry(isBlank = true,isWritten = false, day = 0 , summaryIndex = 0))
        }
        val daysInMonth = LocalDate.of(selectedYearAndMonth.value.first,selectedYearAndMonth.value.second,1).lengthOfMonth()
        repeat(daysInMonth){
            list.add(CalenderEntry(isBlank = false,isWritten = false, day = it+1 , summaryIndex = 0))
        }
        currentMonthSummaries.value.forEachIndexed{ index, summary ->
            list[frontBlankCount-1+summary.date.dayOfMonth] = CalenderEntry(isBlank = false,isWritten = true, day = summary.date.dayOfMonth , summaryIndex = index)
        }
        val backBlankCount = (7 - (frontBlankCount + daysInMonth) % 7) % 7
        repeat(backBlankCount){
            list.add(CalenderEntry(isBlank = true,isWritten = false, day = 0 , summaryIndex = 0))
        }

        _calenderEntries.value=list
    }



    private val _adviceOrForcing = MutableStateFlow(Pair(false,false))
    val adviceOrForcing: StateFlow<Pair<Boolean, Boolean>> = _adviceOrForcing.asStateFlow()

    fun clickAdviceOrForcing(clickedIsLeft:Boolean){
        _adviceOrForcing.value=Pair(clickedIsLeft,!clickedIsLeft)
    }

    private val _currentMyTimeTab = MutableStateFlow(0)
    val currentMyTimeTab:StateFlow<Int> = _currentMyTimeTab.asStateFlow()

    fun setCurrentMyTimeTab(tab:Int){
        _currentMyTimeTab.value=tab
        Log.d("aaaa","onTabCanged:$tab")
    }

    private val _myTime = MutableStateFlow(List(7){ Pair(23,0)})
    val myTime:StateFlow<List<Pair<Int,Int>>> = _myTime.asStateFlow()

    fun setMyTime(hour:Int?=null,minute:Int?=null,tab:Int?=null){
        val index=tab?:currentMyTimeTab.value

        _myTime.value=_myTime.value.toMutableList().apply {
            this[index]=Pair(
                hour ?: this[index].first,
                minute ?: this[index].second
            )
        }
        Log.d("setMyTime",myTime.value.joinToString(separator = ",") { "(${it.first},${it.second})" })
    }

    private val _sameEveryDay = MutableStateFlow(true)
    val sameEveryDay:StateFlow<Boolean> = _sameEveryDay.asStateFlow()

    fun setSameEveryDay( value:Boolean?=null,isToggle:Boolean=false,){
        if(isToggle) _sameEveryDay.value=!_sameEveryDay.value
        else if(value==null) return
        else _sameEveryDay.value=value

        if(_sameEveryDay.value) _currentMyTimeTab.value=0
        //Log.d("aaaa",sameEveryDay.value.toString())
    }



    fun isSettingCompleted():Boolean{
        return prefRepository.getRefSetting()!=null
    }

    fun settingInitialize(){
        if(!isSettingCompleted()) return
        val setting:Setting = prefRepository.getRefSetting()!!
        Log.d("ref","out:${setting.alarmTimes}")

        clickAdviceOrForcing(setting.adviceOrForcing==AdviceOrForcing.Advice)

        setting.alarmTimes.forEachIndexed { index, time ->
            setMyTime(time.first,time.second,index)
        }
        Log.d("aaaa",myTime.value.toString())
        setSameEveryDay(value=setting.sameEveryDay)
    }

    fun extractCurrentSetting():Setting{


        val adviceOrForcing= if(_adviceOrForcing.value.first)AdviceOrForcing.Advice else AdviceOrForcing.Forcing
        val sameEveryDay=_sameEveryDay.value
        val alarmTimes=myTime.value

        return Setting(adviceOrForcing, sameEveryDay, alarmTimes)
    }

    fun settingConfirm(){
        prefRepository.setRefSetting(extractCurrentSetting())
    }

    fun previewSetting(context: Context){
        context.startService(Intent(context, MyService::class.java))
    }

    fun scheduleOverlay(){
        alarmScheduler.scheduleOverlay()
    }

    fun setSummary(content: String, day:Int) {
        viewModelScope.launch{
            summaryRepository.insertSummary(
                Summary(
                    content = content,
                    date = LocalDate.of(
                        selectedYearAndMonth.value.first,
                        selectedYearAndMonth.value.second,
                        day
                    ),
                    writtenTime = LocalDate.now()
                )
            )
        }
        calenderRefresh()
    }

    fun getSummariesByMonth(yearMonth: String): Flow<List<Summary>> {
        return summaryRepository.getSummariesByMonth(yearMonth)
    }

    fun calenderRefresh(){
        val yearMonth = "%04d-%02d".format(
            selectedYearAndMonth.value.first,
            selectedYearAndMonth.value.second
        )
        setCurrentMonthSummaries(
            runBlocking {
                getSummariesByMonth(yearMonth).first()
            }
        )
        setCalenderEntries()
    }


}