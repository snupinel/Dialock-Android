package com.example.dailysummary.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dailysummary.components.BackButton
import com.example.dailysummary.components.RoundedCornerButton
import com.example.dailysummary.components.SaveButton
import com.example.dailysummary.components.TimePicker
import com.example.dailysummary.components.weekDayList
import com.example.dailysummary.viewModel.AlarmSettingPageState
import com.example.dailysummary.viewModel.SettingPageViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GroupSettingPage(){

    val viewModel = hiltViewModel<SettingPageViewModel>()

    val groupIndex by viewModel.groupIndex.collectAsState()

    val groupingAlarm by viewModel.groupingAlarm.collectAsState()

    val alarmTime = groupingAlarm.alarmTime

    // 0, 2, 3
    val dayList = groupingAlarm.dayList

    //true false true true false false false
    val daySelects = MutableList(7){false}.apply {
        dayList.forEach{
            this[it]=true
        }
    }


    LaunchedEffect(Unit){
        if(groupIndex!=null){
            viewModel.initializeGroupingAlarm(groupIndex!!)
            viewModel.setIsWritten(true)
        }
        else viewModel.setIsWritten(false)


    }







    Scaffold(
        topBar = {
            GroupSettingToolBar{
                viewModel.setAlarmSettingPageState(AlarmSettingPageState.Main)
            }
        },
        bottomBar = {
            SaveButton(modifier = Modifier.padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())) {
                viewModel.saveGroup()
                viewModel.setAlarmSettingPageState(AlarmSettingPageState.Main)
            }
        }
    ){ paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
        ){

            Row(Modifier.fillMaxWidth()) {

                weekDayList.forEachIndexed { index, day ->
                    val isNotGrouped = !viewModel.isGrouped(index)
                    val isSelected = daySelects[index]
                    RoundedCornerButton(
                        modifier = Modifier
                            .weight(1f)
                            .then(
                                if (isSelected) Modifier
                                else Modifier.border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                )
                            ),
                        color = if(isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        enabled = isNotGrouped,
                        onClick = {
                            if (isSelected) viewModel.removeDayInGroup(index)
                            else viewModel.appendDayInGroup(index)
                        }
                    ) {
                        Text(text = day.second, color = day.first?:MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
            TimePicker(
                selectedHour = alarmTime.hour,
                selectedMinute = alarmTime.minute,
                onHourChange = {viewModel.setGroupingAlarmTime(hour = it)},
                onMinuteChange = {viewModel.setGroupingAlarmTime(minute = it)},
                height = 150.dp
            )



        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSettingToolBar(
    onBack:()->Unit,
){
    TopAppBar(
        title = { /*TODO*/ },
        navigationIcon = {
            BackButton {
                onBack()
            }

        }
    )
}