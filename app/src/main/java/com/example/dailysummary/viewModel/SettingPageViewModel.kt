package com.example.dailysummary.viewModel

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.dailysummary.data.PrefRepository
import com.example.dailysummary.dto.AdviceOrForcing
import com.example.dailysummary.dto.AlarmTime
import com.example.dailysummary.dto.DEFAULT_ALARMTIME
import com.example.dailysummary.dto.Setting
import com.example.dailysummary.overlay.AlarmScheduler
import com.example.dailysummary.overlay.SummaryService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingPageViewModel @Inject constructor(
    private val prefRepository: PrefRepository,
    private val alarmScheduler: AlarmScheduler,
): ViewModel(){
    private val _adviceOrForcing = MutableStateFlow(Pair(false,false))
    val adviceOrForcing: StateFlow<Pair<Boolean, Boolean>> = _adviceOrForcing.asStateFlow()

    fun clickAdviceOrForcing(clickedIsLeft:Boolean){
        _adviceOrForcing.value=Pair(clickedIsLeft,!clickedIsLeft)
    }

    private val _currentMyTimeTab = MutableStateFlow(0)
    val currentMyTimeTab: StateFlow<Int> = _currentMyTimeTab.asStateFlow()

    fun setCurrentMyTimeTab(tab:Int){
        _currentMyTimeTab.value=tab
        Log.d("aaaa","onTabCanged:$tab")
    }

    private val _myTime = MutableStateFlow(List(7){ DEFAULT_ALARMTIME })
    val myTime: StateFlow<List<AlarmTime>> = _myTime.asStateFlow()

    fun setMyTime(hour:Int?=null,minute:Int?=null,tab:Int?=null){
        val index=tab?:currentMyTimeTab.value

        _myTime.value=_myTime.value.toMutableList().apply {
            this[index]= AlarmTime(
                hour ?: this[index].hour,
                minute ?: this[index].minute,
                this[index].isNextDay,
            )
        }
        Log.d("setMyTime",myTime.value.joinToString(separator = ",") { "(${it.hour},${it.minute})" })
    }

    private val _sameEveryDay = MutableStateFlow(true)
    val sameEveryDay: StateFlow<Boolean> = _sameEveryDay.asStateFlow()

    fun setSameEveryDay( value:Boolean?=null,isToggle:Boolean=false,){
        if(isToggle) _sameEveryDay.value=!_sameEveryDay.value
        else if(value==null) return
        else _sameEveryDay.value=value

        if(_sameEveryDay.value) _currentMyTimeTab.value=0
        //Log.d("aaaa",sameEveryDay.value.toString())
    }


    fun setIsNextDay(value:Boolean,index:Int =currentMyTimeTab.value){
        _myTime.value=myTime.value.toMutableList().apply {
            this[index]= AlarmTime(
                hour = this[index].hour,
                minute = this[index].minute,
                isNextDay = value
            )
        }
    }




    fun isSettingCompleted():Boolean{
        return prefRepository.getRefSetting()!=null
    }

    fun settingInitialize(){
        if(!isSettingCompleted()) return
        val setting: Setting = prefRepository.getRefSetting()!!
        //Log.d("ref","out:${setting.alarmTimes}")

        clickAdviceOrForcing(setting.adviceOrForcing== AdviceOrForcing.Advice)

        setting.alarmTimesByDay.forEachIndexed { index, time ->
            setMyTime(time.hour,time.minute,index)
            setIsNextDay(time.isNextDay,index)
        }
        Log.d("aaaa",myTime.value.toString())
        setSameEveryDay(value=setting.sameEveryDay)
    }

    fun extractCurrentSetting(): Setting {


        val adviceOrForcing= if(_adviceOrForcing.value.first) AdviceOrForcing.Advice else AdviceOrForcing.Forcing
        val sameEveryDay=_sameEveryDay.value
        val alarmTimes=myTime.value

        return Setting(adviceOrForcing, sameEveryDay, alarmTimes)
    }

    fun settingConfirm(){
        prefRepository.setRefSetting(extractCurrentSetting())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun previewSetting(context: Context){
        context.startService(Intent(context, SummaryService::class.java))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleOverlay(){
        alarmScheduler.scheduleOverlay()
    }
}