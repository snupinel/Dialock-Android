package com.example.dailysummary.pages.initialPages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dailysummary.components.TimePicker
import com.example.dailysummary.pages.SettingInstruction
import com.example.dailysummary.pages.TimeApplyButton
import com.example.dailysummary.viewModel.InitialSettingPageViewModel


@Composable
fun InitialTimeSettingPage(
    navController: NavController,
    viewModel: InitialSettingPageViewModel = hiltViewModel()){


    Scaffold(
        bottomBar = {
            TimeApplyButton{
                viewModel.saveSetting()
                viewModel.scheduleOverlay()
                navController.navigate("MainPage") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true // ✅ startDestination까지도 제거
                    }
                    launchSingleTop = true
                }
            }
        },

        ) {paddingValues ->

        Column(modifier = Modifier
            .padding(paddingValues)){
            SettingInstruction()
            Spacer(modifier = Modifier.height(16.dp))
            MyInitialTimePicker(viewModel)
        }
    }
}

@Composable
fun MyInitialTimePicker(viewModel: InitialSettingPageViewModel){

    val time by viewModel.pickerTime.collectAsState()

    TimePicker(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        height = 300.dp,
        selectedHour = time.hour,
        selectedMinute = time.minute,
        onHourChange = {viewModel.setPickerTime(hour = it) },
        onMinuteChange = { viewModel.setPickerTime(minute = it) },
    )
}