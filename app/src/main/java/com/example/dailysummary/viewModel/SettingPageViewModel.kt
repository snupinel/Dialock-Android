package com.example.dailysummary.viewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.dailysummary.data.PrefRepository
import com.example.dailysummary.dto.AdviceOrForcing
import com.example.dailysummary.dto.AlarmTime
import com.example.dailysummary.dto.GroupedAlarmEntry
import com.example.dailysummary.dto.SAMPLE_ALARM_TIME
import com.example.dailysummary.dto.SAMPLE_GROUPED_ALARM_ENTRY
import com.example.dailysummary.dto.SAMPLE_SETTING
import com.example.dailysummary.dto.Setting
import com.example.dailysummary.overlay.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

enum class AlarmSettingPageState{
    Main,group
}

@HiltViewModel
class SettingPageViewModel @Inject constructor(
    private val prefRepository: PrefRepository,
    private val alarmScheduler: AlarmScheduler,
): ViewModel(){

    private val _alarmSettingPageState = MutableStateFlow(AlarmSettingPageState.Main)
    val alarmSettingPageState: StateFlow<AlarmSettingPageState> = _alarmSettingPageState.asStateFlow()

    fun setAlarmSettingPageState(it:AlarmSettingPageState){
        _alarmSettingPageState.value=it
    }

    private val _groupIndex = MutableStateFlow<Int?>(null)
    val groupIndex: StateFlow<Int?> = _groupIndex.asStateFlow()

    fun setGroupIndex(value:Int?){
        _groupIndex.value=value
    }


    private val _setting = MutableStateFlow(SAMPLE_SETTING)
    val setting: StateFlow<Setting> = _setting.asStateFlow()

    fun setSetting(it:Setting){
        _setting.value=it
        Log.d("setting" ,setting.value.toString())
    }

    fun isGrouped(day:Int):Boolean{
        return setting.value.alarmTimesByDay[day].isGrouped
    }

    fun setAdviceOrForcing(it:AdviceOrForcing){
        setSetting(setting.value.copy(adviceOrForcing = it))
    }

    fun setDefaultAlarmTime(
        hour:Int?=null,
        minute:Int?=null,
        isGrouped:Boolean?=null
    ){
        val alarmTime = setting.value.defaultAlarmTime.let {
            AlarmTime(
                hour ?: it.hour,
                minute ?: it.minute,
                isGrouped=isGrouped ?: it.isGrouped,
            )
        }
        setSetting(setting.value.copy(defaultAlarmTime = alarmTime))
    }
    fun setDefaultAlarmTime(it:AlarmTime){
        setSetting(setting.value.copy(defaultAlarmTime = it))
    }

    fun setAlarmTimeByDay(
        day:Int,
        hour:Int?=null,
        minute:Int?=null,
        isNextDay:Boolean?=null,
        isGrouped:Boolean?=null,
    ){
        val alarmTimes=setting.value.alarmTimesByDay.toMutableList().apply {
            this[day]= AlarmTime(
                hour ?: this[day].hour,
                minute ?: this[day].minute,
                isNextDay ?: this[day].isNextDay,
                isGrouped ?: this[day].isGrouped,
            )
        }
        setSetting(setting.value.copy(alarmTimesByDay = alarmTimes))
    }

    fun setAlarmTimeByDay(it:List<AlarmTime>){
        val newContent = setting.value.copy(alarmTimesByDay =it)
        Log.d("setAlarmTimeByDay",newContent.toString())
        setSetting(newContent)

            /*
            if (it.size > 7) {
                it.take(7) // ✅ 리스트 길이가 7보다 크면 처음 7개 요소 유지
            } else {
                it + List(7 - it.size) { SAMPLE_ALARM_TIME} // ✅ 7보다 작으면 0을 추가
            }
        )*/
    }


    fun setSameEveryDay(value:Boolean){
        setSetting(setting.value.copy(
            sameEveryDay = value
        ))
    }


    fun isSettingCompleted():Boolean{
        return prefRepository.getRefSetting()!=null
    }

    private val _groupedAlarmList = MutableStateFlow<List<GroupedAlarmEntry>>(emptyList())
    val groupedAlarmList: StateFlow<List<GroupedAlarmEntry>> = _groupedAlarmList.asStateFlow()

    fun refreshGroupedAlarmList(){
        _groupedAlarmList.value=setting.value.alarmTimesByDay
            .filter { it.isGrouped }
            .mapIndexed { index, alarmTime -> alarmTime to index } // 인덱스와 함께 매핑
            .groupBy({ it.first }, { it.second }) // alarmTime 기준으로 그룹화
            .map { (alarmTime, dayList) -> GroupedAlarmEntry(alarmTime, dayList, true) } // GroupedAlarmEntry 생성
        //Log.d("aaaa",setting.value.alarmTimesByDay.toString())
    }


    fun settingInitialize(){
        if(!isSettingCompleted()) return

        val setting: Setting = prefRepository.getRefSetting()!!

        setAdviceOrForcing(setting.adviceOrForcing)
        setSameEveryDay(setting.sameEveryDay)
        setDefaultAlarmTime(setting.defaultAlarmTime)
        setAlarmTimeByDay(setting.alarmTimesByDay)

        refreshGroupedAlarmList()



    }



    fun saveSetting(){
        prefRepository.setRefSetting(setting.value)
    }

    /*
    @RequiresApi(Build.VERSION_CODES.O)
    fun previewSetting(context: Context){
        context.startService(Intent(context, SummaryService::class.java))
    }*/

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleOverlay(){
        alarmScheduler.scheduleOverlay()
    }

    private val _changeToggle = MutableStateFlow(true)
    val changeToggle: StateFlow<Boolean> = _changeToggle.asStateFlow()

    fun toggleChangeToggle(){
        _changeToggle.value=!changeToggle.value
    }

//////////////////////////////////////////////////////////////////////


    //여기부터는 groupsetting

    //////////////////////////////////////////


    private val _groupingAlarm = MutableStateFlow(SAMPLE_GROUPED_ALARM_ENTRY)
    val groupingAlarm:StateFlow<GroupedAlarmEntry> = _groupingAlarm.asStateFlow()

    fun initializeGroupingAlarm(index:Int){
        _groupingAlarm.value=groupedAlarmList.value[index]
    }

    fun setGroupingAlarm(value:GroupedAlarmEntry){
        _groupingAlarm.value=value
    }

    fun setGroupingAlarmTime(value:AlarmTime){
        setGroupingAlarm(groupingAlarm.value.copy(alarmTime = value))
    }


    fun setGroupingAlarmTime(
        hour:Int?=null,
        minute:Int?=null,
        ){

        val alarmTime = groupingAlarm.value.alarmTime.let {
            AlarmTime(
                hour ?: it.hour,
                minute ?: it.minute,
                isGrouped = it.isGrouped,
            )
        }
        setGroupingAlarmTime(alarmTime)

    }

    fun setIsWritten(value:Boolean){
        setGroupingAlarm(groupingAlarm.value.copy(isWritten = value))
    }

    fun setDayList(value:List<Int>){
        setGroupingAlarm(groupingAlarm.value.copy(dayList = value))
    }
    fun appendDayInGroup(day:Int){
        setDayList(
            groupingAlarm.value.dayList.toMutableList().apply {
                this.add(day)
            }
        )
    }

    fun removeDayInGroup(day: Int){
        setDayList(
            groupingAlarm.value.dayList.toMutableList().apply {
                this.remove(day)
            }
        )
    }

    fun saveGroup(){
        val newTimes = setting.value.alarmTimesByDay.toMutableList().apply {
            groupingAlarm.value.dayList.forEach { day->
                this[day]=groupingAlarm.value.alarmTime.copy(isGrouped = true)
            }
        }
        Log.d("saveGroup",newTimes.toString())
        setAlarmTimeByDay(newTimes)
    }

    /*
    private val _daySelects = MutableStateFlow(emptyList<Boolean>())
    val daySelects:StateFlow<List<Boolean>> = _daySelects.asStateFlow()

    fun setDaySelects(day:Int,value:Boolean){
        _daySelects.value=daySelects.value.toMutableList().apply {
            this[day]=value
        }
    }*/
}