package com.example.dailysummary.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.dailysummary.data.PrefRepository
import com.example.dailysummary.dto.AdviceOrForcing
import com.example.dailysummary.dto.AlarmTime
import com.example.dailysummary.dto.DEFAULT_ALARMTIME
import com.example.dailysummary.dto.Setting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.StringBuilder
import javax.inject.Inject

@HiltViewModel
class InitialSettingViewModel @Inject constructor(
    private val prefRepository: PrefRepository,
):ViewModel(){



    private val _startPageAnimationState = MutableStateFlow(0)
    val startPageAnimationState: StateFlow<Int> = _startPageAnimationState.asStateFlow()

    fun setStartPageAnimationState(num:Int){
        _startPageAnimationState.value=num
    }

    private val _adviceOrForcing = MutableStateFlow(Pair(false,false))
    val adviceOrForcing:StateFlow<Pair<Boolean,Boolean>> = _adviceOrForcing.asStateFlow()

    fun clickAdviceOrForcing(clickedIsLeft:Boolean){
        _adviceOrForcing.value=Pair(clickedIsLeft,!clickedIsLeft)
    }

    private val _currentMyTimeTab = MutableStateFlow(0)
    val currentMyTimeTab:StateFlow<Int> = _currentMyTimeTab.asStateFlow()

    fun setCurrentMyTimeTab(tab:Int){
        _currentMyTimeTab.value=tab
    }

    private val _myTime = MutableStateFlow(List(7){ DEFAULT_ALARMTIME })
    val myTime:StateFlow<List<AlarmTime>> = _myTime.asStateFlow()

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
    val sameEveryDay:StateFlow<Boolean> = _sameEveryDay.asStateFlow()

    fun toggleSameEveryDay(){
        _sameEveryDay.value=!_sameEveryDay.value
        if(_sameEveryDay.value) _currentMyTimeTab.value=0
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

    fun saveSetting(){
        prefRepository.setRefSetting(
            Setting(
                adviceOrForcing = if(_adviceOrForcing.value.first)AdviceOrForcing.Advice else AdviceOrForcing.Forcing,
                sameEveryDay = _sameEveryDay.value,
                alarmTimesByDay = _myTime.value,
            )
        )
    }


}