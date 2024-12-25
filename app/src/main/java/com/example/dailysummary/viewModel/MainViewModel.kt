package com.example.dailysummary.viewModel

import androidx.lifecycle.ViewModel
import com.example.dailysummary.data.PrefRepository
import com.example.dailysummary.dto.AdviceOrForcing
import com.example.dailysummary.dto.Setting
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.StringBuilder
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val prefRepository: PrefRepository,
):ViewModel(){
    fun setRefSetting(setting: Setting){
        val builder= StringBuilder()
        builder.append(setting.adviceOrForcing.name+" ")
        builder.append(setting.sameEveryDay.toString()+" ")
        setting.alarmTimes.forEach{
            builder.append("${it.first} ${it.second} ")
        }
        prefRepository.setPref("Setting",builder.toString())
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

    fun isSettingCompleted():Boolean{
        return getRefSetting()!=null
    }
}