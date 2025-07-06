package com.example.dailysummary.viewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.dailysummary.data.PrefRepository
import com.example.dailysummary.dto.AdviceOrForcing
import com.example.dailysummary.dto.AlarmTime
import com.example.dailysummary.dto.SAMPLE_ALARM_TIME
import com.example.dailysummary.dto.SAMPLE_SETTING
import com.example.dailysummary.dto.Setting
import com.example.dailysummary.overlay.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class SettingPageViewModel @Inject constructor(
    private val prefRepository: PrefRepository,
    private val alarmScheduler: AlarmScheduler,
): ViewModel() {




    private val _shouldRefresh = MutableStateFlow(true)
    val shouldRefresh: StateFlow<Boolean> = _shouldRefresh.asStateFlow()

    fun setShouldRefresh(value: Boolean) {
        _shouldRefresh.value = value
    }


    private val _setting = MutableStateFlow(SAMPLE_SETTING)
    val setting: StateFlow<Setting> = _setting.asStateFlow()

    fun setSetting(it: Setting) {
        _setting.value = it
        updateSettingChanged()
    }

    private val _beforeSetting = MutableStateFlow(SAMPLE_SETTING)
    val beforeSetting: StateFlow<Setting> = _beforeSetting.asStateFlow()

    fun updateBeforeSetting() {
        _beforeSetting.value = setting.value
        updateSettingChanged()
    }

    private val _isSettingChanged = MutableStateFlow(false)
    val isSettingChanged: StateFlow<Boolean> = _isSettingChanged.asStateFlow()

    fun updateSettingChanged(){

        _isSettingChanged.value = setting.value != beforeSetting.value

    }

    fun revertSetting(){
        setSetting(beforeSetting.value)
    }

    fun setAdviceOrForcing(isForcing: Boolean) {
        val mode= when (isForcing){
            true-> AdviceOrForcing.Forcing
            false-> AdviceOrForcing.Advice

        }
        setSetting(setting.value.copy(adviceOrForcing = mode))
    }






    fun setAlarmTimeByDay(it: List<AlarmTime>) {
        val newContent = setting.value.copy(alarmTimesByDay = it)
        Log.d("setAlarmTimeByDay", newContent.toString())
        setSetting(newContent)

    }


    fun setSameEveryDay(value: Boolean) {
        setSetting(
            setting.value.copy(
                sameEveryDay = value
            )
        )
    }


    fun isSettingCompleted(): Boolean {
        return prefRepository.getRefSetting() != null
    }

    private var settingInitialized = false
    fun settingInitialize() {
        if(settingInitialized) return
        settingInitialized = true
        if (!isSettingCompleted()) return

        val setting: Setting = prefRepository.getRefSetting()!!

        setAdviceOrForcing(isForcing = setting.adviceOrForcing==AdviceOrForcing.Forcing)
        setSameEveryDay(setting.sameEveryDay)
        setAlarmTimeByDay(setting.alarmTimesByDay)
        updateBeforeSetting()
    }


    fun saveSetting() {
        prefRepository.setRefSetting(setting.value)
    }

    /*
    @RequiresApi(Build.VERSION_CODES.O)
    fun previewSetting(context: Context){
        context.startService(Intent(context, SummaryService::class.java))
    }*/

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleOverlay() {
        alarmScheduler.scheduleOverlay()
    }

    private val _changeToggle = MutableStateFlow(true)
    val changeToggle: StateFlow<Boolean> = _changeToggle.asStateFlow()

    fun toggleChangeToggle() {
        _changeToggle.value = !changeToggle.value
    }
    ///////////////////////////////////////////////////////////

    private val _pickerTime = MutableStateFlow(SAMPLE_ALARM_TIME)
    val pickerTime: StateFlow<AlarmTime> = _pickerTime.asStateFlow()

    fun setPickerTime(value:AlarmTime){
        _pickerTime.value = value
        updatePickerApplicablity()
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
    fun pickerInitialize(index:Int){
        setPickerTime(setting.value.alarmTimesByDay[index])
    }

    private val _chosenDays = MutableStateFlow(List(7){false})
    val chosenDays: StateFlow<List<Boolean>> = _chosenDays.asStateFlow()

    fun clearChosenDays(){
        _chosenDays.value = List(7){false}
    }
    fun setChosenDay(index: Int, value: Boolean) {
        _chosenDays.update { current ->
            current.mapIndexed { i, oldValue ->
                if (i == index) value else oldValue
            }
        }
    }

    fun clickChosenDay(index: Int){
        _chosenDays.update { current ->
            current.mapIndexed { i, oldValue ->
                if (i == index) !oldValue else oldValue
            }
        }
        updatePickerApplicablity()
    }

    fun applyChosenDay(){
        val alarmTimes = setting.value.alarmTimesByDay.toMutableList()
        chosenDays.value.forEachIndexed() { i, chosen ->
            if (chosen) {
                alarmTimes[i] = pickerTime.value
            }
        }
        setAlarmTimeByDay(alarmTimes)
    }

    private val _isPickerApplicable = MutableStateFlow(false)
    val isPickerApplicable: StateFlow<Boolean> = _isPickerApplicable.asStateFlow()

    fun updatePickerApplicablity(){

        _isPickerApplicable.value = chosenDays.value.withIndex().any { (index, value) ->
            value && setting.value.alarmTimesByDay[index]!=pickerTime.value
        }

    }

}