package com.example.dailysummary.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dailysummary.components.BackButton
import com.example.dailysummary.components.TimePicker
import com.example.dailysummary.pages.mainPageTabs.weekDayList
import com.example.dailysummary.utils.popBackStackExclusive
import com.example.dailysummary.viewModel.SettingPageViewModel
import kotlinx.coroutines.launch

@Composable
fun TimeSettingPage(navController: NavController,viewModel: SettingPageViewModel){
    val chosenDays by viewModel.chosenDays.collectAsState()
    val applicable by viewModel.isPickerApplicable.collectAsState()

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {TimeSettingToolBar{
            scope.launch {
                navController.popBackStackExclusive()
            }
        }},
        bottomBar = {
                    TimeApplyButton(applicable){
                        viewModel.applyChosenDay()
                        navController.popBackStack()
                    }
        },

    ) {paddingValues ->

        Column(modifier = Modifier
            .padding(paddingValues)){
            WeekdaySelectorRow(chosenDays){
                viewModel.clickChosenDay(it)
            }
            Spacer(modifier = Modifier.height(16.dp))
            MyTimePicker(viewModel)
        }
    }
}

@Composable
fun WeekdaySelectorRow(
    chosenDays: List<Boolean>,
    onDayToggle: (Int) -> Unit
) {
    val allChecked = chosenDays.all { it }

    Column {
        SettingInstruction()
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = allChecked,
                    onCheckedChange = { checked ->
                        // 전체 선택 or 해제
                        repeat(7) { index ->
                            if (chosenDays[index] != checked) {
                                onDayToggle(index)
                            }
                        }
                    },
                )
                Text("매일", color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDayList.forEachIndexed { index, (color, label) ->
                val isSelected = chosenDays.getOrNull(index) == true
                val background = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else color ?: MaterialTheme.colorScheme.onSurface

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(background)
                        .clickable { onDayToggle(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = textColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSettingToolBar(
    onBack:()->Unit,
){
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                //Text("시간 설정", fontSize = 18.sp, fontWeight = FontWeight.Light)
            } },
        navigationIcon = {
            BackButton {
                onBack()
            }
        },

        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
fun TimeApplyButton(enabled:Boolean=true,onApply:()->Unit) {

    Button(
        modifier = Modifier
            .padding(
                bottom = WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            )
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = { onApply() },
        enabled = enabled,
        ){
        Text(text = "완료")
    }


}

@Composable
fun MyTimePicker(viewModel: SettingPageViewModel){


    val time by viewModel.pickerTime.collectAsState()

    val changeToggle by viewModel.changeToggle.collectAsState()

    TimePicker(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        height = 300.dp,
        selectedHour = time.hour,
        selectedMinute = time.minute,
        onHourChange = {viewModel.setPickerTime(hour = it) },
        onMinuteChange = { viewModel.setPickerTime(minute = it) },
        changeToggle = changeToggle,
    )
}

@Composable
fun SettingInstruction(){
    Column (
        Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        Text("일기를 쓸 시간을 설정해 주세요", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("(잠자기 30분 전 정도가 좋아요!)")
    }

}