package com.example.dailysummary.viewModel

import android.util.Log
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

    fun isSettingCompleted():Boolean{
        return prefRepository.getRefSetting()!=null
    }
}