package com.example.dailysummary.pages

import android.util.Log
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dailysummary.components.BackButton
import com.example.dailysummary.components.RoundedCornerButton
import com.example.dailysummary.components.TimePicker
import com.example.dailysummary.components.weekDayList
import com.example.dailysummary.viewModel.SettingPageViewModel

@Composable
fun TimeSettingPage(navController: NavController,viewModel: SettingPageViewModel){
    val chosenDays by viewModel.chosenDays.collectAsState()
    val applicable by viewModel.isPickerApplicable.collectAsState()

    Scaffold(
        topBar = {TimeSettingToolBar{
            navController.popBackStack()
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("매일", style = MaterialTheme.typography.titleMedium)

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
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF3D8BFF))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("매일", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDayList.forEachIndexed { index, (color, label) ->
                val isSelected = chosenDays.getOrNull(index) == true
                val background = if (isSelected) Color(0xFF3D8BFF) else Color(0xFFE0E0E0)
                val textColor = color ?: if (isSelected) Color.White else Color.Black

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(background)
                        .clickable { onDayToggle(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = textColor,
                        fontWeight = FontWeight.Medium
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

    RoundedCornerButton(
        modifier = Modifier
            .padding(
                bottom = WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            )
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 12.dp),
        onClick = onApply,
        enabled = enabled,
        ){
        Text(text = "완료", color = MaterialTheme.colorScheme.onPrimary)
    }


}

@Composable
fun MyTimePicker(viewModel: SettingPageViewModel){


    val time by viewModel.pickerTime.collectAsState()

    val changeToggle by viewModel.changeToggle.collectAsState()

    TimePicker(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
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