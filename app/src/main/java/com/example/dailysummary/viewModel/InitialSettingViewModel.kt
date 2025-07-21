package com.example.dailysummary.viewModel

import androidx.lifecycle.ViewModel
import com.example.dailysummary.data.PrefRepository
import com.example.dailysummary.data.SummaryRepository
import com.example.dailysummary.dto.AdviceOrForcing
import com.example.dailysummary.dto.AlarmTime
import com.example.dailysummary.dto.SAMPLE_ALARM_TIME
import com.example.dailysummary.dto.Setting
import com.example.dailysummary.overlay.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class InitialSettingPageViewModel @Inject constructor(
    private val prefRepository: PrefRepository,
    private val alarmScheduler: AlarmScheduler,
): ViewModel() {

    private val _pickerTime = MutableStateFlow(SAMPLE_ALARM_TIME)
    val pickerTime: StateFlow<AlarmTime> = _pickerTime.asStateFlow()

    fun setPickerTime(value: AlarmTime){
        _pickerTime.value = value
    }
    fun setPickerTime(
        hour: Int? = null,
        minute: Int? = null,
    ) {
        val alarmTime = pickerTime.value.let {
            AlarmTime(
                hour ?: it.hour,
                minute ?: it.minute,
            )
        }
        setPickerTime(alarmTime)
    }
    fun saveSetting(){
        prefRepository.setRefSetting(Setting(
            adviceOrForcing = AdviceOrForcing.Forcing,
            sameEveryDay = true,
            alarmTimesByDay = List(7){pickerTime.value }
        ))
    }

    fun scheduleOverlay() {
        alarmScheduler.scheduleOverlay()
    }

}