package com.example.dailysummary.viewModel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.dailysummary.data.PrefRepository
import com.example.dailysummary.dto.AdviceOrForcing
import com.example.dailysummary.dto.Setting
import com.example.dailysummary.overlay.OverlayReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import javax.inject.Inject

class OverlayViewModel (
    private val prefRepository: PrefRepository,
) : ViewModel() {

    private val _adviceOrForcing = MutableStateFlow(AdviceOrForcing.Advice)
    val adviceOrForcing: StateFlow<AdviceOrForcing> = _adviceOrForcing.asStateFlow()

    fun setAdviceOrForcing(adviceOrForcing:AdviceOrForcing){
        _adviceOrForcing.value=adviceOrForcing
    }

    private val _textFieldValue = MutableStateFlow("")
    val textFieldValue: StateFlow<String> = _textFieldValue.asStateFlow()

    fun setTextFieldValue(text:String){
        _textFieldValue.value=text
    }


    fun getRefSetting(): Setting?{

        val refList=prefRepository.getPref("Setting")?.trimEnd()?.split(" ")?: emptyList()

        if(refList.isEmpty()) return null

        val adviceOrForcing= AdviceOrForcing.valueOf(refList[0])
        val sameEveryDay=refList[1].toBoolean()
        val alarmTimes=refList.drop(2).chunked(2).map{
                (first, second) -> Pair(first.toInt(), second.toInt())
        }

        return Setting(adviceOrForcing, sameEveryDay, alarmTimes)
    }

    fun initialize(){

        val setting = getRefSetting()
        setAdviceOrForcing(setting!!.adviceOrForcing)
    }

    fun saveDiary(){

    }

}

