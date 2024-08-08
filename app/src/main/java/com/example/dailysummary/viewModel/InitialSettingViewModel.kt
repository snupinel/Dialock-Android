package com.example.dailysummary.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.dailysummary.data.PrefRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _myTime = MutableStateFlow(Pair(23,0))
    val myTime:StateFlow<Pair<Int,Int>> = _myTime.asStateFlow()

    fun setMyTime(hour:Int?=null,minute:Int?=null){
        _myTime.value=Pair(
            hour ?: _myTime.value.first,
            minute ?: _myTime.value.second
        )
        Log.d("setMyTime",_myTime.value.toString())
    }

    private val _sameEveryDay = MutableStateFlow(true)
    val sameEveryDay:StateFlow<Boolean> = _sameEveryDay.asStateFlow()

    fun toggleSameEveryDay(){
        _sameEveryDay.value=!_sameEveryDay.value
    }
}