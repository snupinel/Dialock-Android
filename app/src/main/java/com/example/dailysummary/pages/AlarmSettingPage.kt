package com.example.dailysummary.pages

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dailysummary.components.AnimatedActionButton
import com.example.dailysummary.components.BackButton
import com.example.dailysummary.components.CalenderDate
import com.example.dailysummary.components.EditButton
import com.example.dailysummary.components.RoundedCornerButton
import com.example.dailysummary.components.SaveButton
import com.example.dailysummary.components.SettingOption
import com.example.dailysummary.components.TimePicker
import com.example.dailysummary.components.TimeSetting
import com.example.dailysummary.components.weekDayList
import com.example.dailysummary.dto.AdviceOrForcing
import com.example.dailysummary.dto.GroupedAlarmEntry
import com.example.dailysummary.viewModel.AlarmSettingPageState
import com.example.dailysummary.viewModel.MainPageViewModel
import com.example.dailysummary.viewModel.SettingPageViewModel



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmSettingPage(navController: NavController){

    val viewModel = hiltViewModel<SettingPageViewModel>()

    val alarmSettingPageState by viewModel.alarmSettingPageState.collectAsState()

    //val setting = viewModel.setting.collectAsState()


    LaunchedEffect(Unit){
        Log.d("AlarmSettingPage","LaunchedEffect act")

        viewModel.settingInitialize()

        viewModel.setShouldRefresh(true)

    }


    when(alarmSettingPageState){
        AlarmSettingPageState.Main -> AlarmSettingMainPage(navController)
        AlarmSettingPageState.group -> GroupSettingPage( )

    }
    /*
    LaunchedEffect(setting){
        viewModel.refreshGroupedAlarmList()
        Log.d("setting","refreshGroupedAlarmList called")
    }*/


}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmSettingMainPage(navController: NavController){

    val viewModel = hiltViewModel<SettingPageViewModel>()
    val shouldRefresh by viewModel.shouldRefresh.collectAsState()

    LaunchedEffect(shouldRefresh){
        if(shouldRefresh){
            viewModel.refreshGroupedAlarmList()
            viewModel.setShouldRefresh(false)
        }
    }

    Scaffold(
        topBar = {
            AlarmSettingToolBar{
                navController.popBackStack()
            }
        },
        bottomBar = {
            SettingSaveButton()
        }
    ){ paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
        ){

            SettingInstruction()

            Divider(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.Gray))

            LazyColumn{
                item {
                    SettingAdviceOrForcing()

                    SettingSameEveryDay()

                    DefaultTimePicker()

                    GroupedAlarms {
                        viewModel.changePage(AlarmSettingPageState.group,it)
                    }

                    Spacer(Modifier.height(8.dp))
                    AddGroupButton {
                        viewModel.changePage(AlarmSettingPageState.group)
                    }

                }
            }



        }

    }
}
@Composable
fun AddGroupButton(
    onClick:()->Unit,
){
    RoundedCornerButton(
        Modifier
            .fillMaxWidth()
            .height(50.dp),
        onClick=onClick
        ){
        Text("그룹 추가")
    }
}

@Composable
fun SettingSameEveryDay(){
    val viewModel= hiltViewModel<SettingPageViewModel>()

    val sameEveryDay = viewModel.setting.collectAsState().value.sameEveryDay
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
        Text("매일 동일")
        Checkbox(
            checked = sameEveryDay,
            onCheckedChange = {viewModel.setSameEveryDay(it)}
        )
    }
}

@Composable
fun GroupedAlarms(
    onEdit:(Int)->Unit,
){
    val viewModel = hiltViewModel<SettingPageViewModel>()

    val groupedAlarmList by viewModel.groupedAlarmList.collectAsState()

    Column {
        groupedAlarmList.forEachIndexed{groupIndex,entry ->
            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row{
                    weekDayList.forEachIndexed{ index, day->
                        CalenderDate(
                            modifier = Modifier.weight(1f),
                            text = if(entry.dayList.contains(index))day.second else "", color = day.first)
                    }
                }
                val timeFormat=(if(entry.alarmTime.isNextDay) "다음 날" else "")+
                    String.format("%02d:%02d",entry.alarmTime.hour,entry.alarmTime.minute)
                Text(timeFormat)
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Gray))
                EditButton {
                    onEdit(groupIndex)
                }
            }
        }
    }
}

@Composable
fun GroupedAlarm(entry:GroupedAlarmEntry){
    Column(
        Modifier
            .fillMaxWidth()
            .height(100.dp)) {

    }
}

@Composable
fun SettingInstruction(){
    Column (
        Modifier
            .fillMaxWidth()
            .height(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        Text("일기를 쓸 시간을 설정해 주세요", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("(잠자기 30분 전 정도가 좋아요!)")
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmSettingToolBar(onBack:()->Unit){
    TopAppBar(
        title = { /*TODO*/ },
        navigationIcon = {
            BackButton {
                onBack()
            }
        }
    )
}

@Composable
fun SettingAdviceOrForcing() {
    //Log.d("aaaa","aaaa")
    val viewModel = hiltViewModel<SettingPageViewModel>()
    val adviceOrForcing = viewModel.setting.collectAsState().value.adviceOrForcing

    SettingOption(
        adviceOrForcing = adviceOrForcing,
        onOptionSelected = { clickedIsAdvice ->
            viewModel.setAdviceOrForcing(
                if (clickedIsAdvice) AdviceOrForcing.Advice
                else AdviceOrForcing.Forcing
            )
        },)
}


@Composable
fun DefaultTimePicker(){

    val viewModel = hiltViewModel<SettingPageViewModel>()

    val time = viewModel.setting.collectAsState().value.defaultAlarmTime

    val changeToggle by viewModel.changeToggle.collectAsState()

    TimePicker(
        modifier = Modifier
            .fillMaxWidth(),
        height = 150.dp,
        selectedHour = time.hour,
        selectedMinute = time.minute,
        onHourChange = {viewModel.setDefaultAlarmTime(hour = it) },
        onMinuteChange = { viewModel.setDefaultAlarmTime(minute = it) },
        changeToggle = changeToggle,
    )
}
/*
@Composable
fun SettingTime() {
    val viewModel = hiltViewModel<SettingPageViewModel>()

    val myTime by viewModel.myTime.collectAsState()
    val sameEveryDay by viewModel.sameEveryDay.collectAsState()
    val currentMyTimeTab by viewModel.currentMyTimeTab.collectAsState()
    val isNextDay = viewModel.myTime.collectAsState().value[currentMyTimeTab].isNextDay

    val changeToggle by viewModel.changeToggle.collectAsState()


    //Log.d("aaaab",myTime.toString())
    //Log.d("aaaab",currentMyTimeTab.toString())
    Log.d("SettingTime","SettingTime recompositioned:\n" +
            "$myTime")
    TimeSetting(
        title = "알림을 받을 시간을 설정해 주세요.\n잠자기 30분 정도가 좋아요.",
        selectedHour = myTime[currentMyTimeTab].hour,
        selectedMinute = myTime[currentMyTimeTab].minute,
        onHourChange = { viewModel.setMyTime(hour = it) },
        onMinuteChange = { viewModel.setMyTime(minute = it) },
        sameEveryDay = sameEveryDay,
        onToggleSameEveryDay = { viewModel.setSameEveryDay(isToggle = true) },
        currentMyTimeTab = currentMyTimeTab,
        onDayTabClick = { viewModel.setCurrentMyTimeTab(it) },
        isNextDay = isNextDay,
        onToggleIsNextDay = {viewModel.setIsNextDay(!isNextDay)},
        changeToggle = changeToggle
    )
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingPreviewButton() {
    val viewModel = hiltViewModel<SettingPageViewModel>()
    val context = LocalContext.current

    AnimatedActionButton(
        text = "미리보기",
        onClick = {
            viewModel.previewSetting(context)
        },
        backgroundColor = MaterialTheme.colorScheme.primary,
        textColor = Color.White,
    )
}*/

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingSaveButton() {
    val viewModel = hiltViewModel<SettingPageViewModel>()

    val context = LocalContext.current

    //val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val (permissionRequested, setPermissionRequested) = remember { mutableStateOf(false) }


    /*
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (alarmManager.canScheduleExactAlarms()) {

        } else {

        }
    }*/

    LaunchedEffect(permissionRequested){
        if(permissionRequested){

            //launcher.launch()
            Toast.makeText(context, "스타트액티비티", Toast.LENGTH_SHORT).show()

            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            ContextCompat.startActivity(context,intent,null)

            setPermissionRequested(false)
        }
    }

    SaveButton(modifier = Modifier.padding(bottom =WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())) {
        viewModel.saveSetting()
        //viewModel.setRefSetting(viewModel.extractCurrentSetting())
        Toast.makeText(context, "설정 완료", Toast.LENGTH_SHORT).show()

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            scheduleOverlay(context)
            setPermissionRequested(true)
        } else {
            // Pre-Android 12, directly schedule the alarm

        }*/
        //scheduleOverlay(context,p)
        //setPermissionRequested(true)
        viewModel.scheduleOverlay()
    }

    /*

    AnimatedActionButton(
        text = "설정 완료",
        onClick = {
            viewModel.saveSetting()
            //viewModel.setRefSetting(viewModel.extractCurrentSetting())
            Toast.makeText(context, "설정 완료", Toast.LENGTH_SHORT).show()

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
                scheduleOverlay(context)
                setPermissionRequested(true)
            } else {
                // Pre-Android 12, directly schedule the alarm

            }*/
            //scheduleOverlay(context,p)
            //setPermissionRequested(true)
            viewModel.scheduleOverlay()
        },
        backgroundColor = MaterialTheme.colorScheme.primary,
        textColor = Color.White,
    )
    */

}
