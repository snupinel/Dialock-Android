package com.example.dailysummary.pages

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dailysummary.components.AnimatedActionButton
import com.example.dailysummary.components.SettingOption
import com.example.dailysummary.components.TabNavigationBar
import com.example.dailysummary.components.TimeSetting
import com.example.dailysummary.model.BottomNavItem
import com.example.dailysummary.viewModel.MainPageViewModel
import com.example.dailysummary.viewModel.Tab

val calenderTab = BottomNavItem(tag = "캘린더", title = "Calender", selectedIcon = Icons.Filled.Home, unselectedIcon = Icons.Outlined.Home)
val settingTab = BottomNavItem(tag="설정", title="Setting", selectedIcon = Icons.Filled.Settings, unselectedIcon = Icons.Outlined.Settings)


// creating a list of all the tabs
val tabBarItems = listOf(calenderTab, settingTab)

//@Inject lateinit var alarmScheduler: AlarmScheduler


@Composable
fun MainPage(navController: NavController){

    val viewModel = hiltViewModel<MainPageViewModel>()

    LaunchedEffect(Unit){
        viewModel.calenderRefresh()
        viewModel.settingInitialize()
        //viewModel.setCalenderEntries()
    }

    val selectedTab by viewModel.selectedTab.collectAsState()

    Scaffold(bottomBar = { TabNavigationBar(tabBarItems) }, topBar = {}) {paddingValues->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues)
        ) {
            when (selectedTab){
                Tab.Calender -> {
                    DSCalender()
                    //SameEveryDayToggle(sameEveryDay = false, onToggle = {viewModel.setSameEveryDay(isToggle = true)})
                }
                Tab.Setting -> {
                    //
                    Column {
                        SettingAdviceOrForcing()

                        SettingTime()

                        SettingSaveButton()

                        SettingPreviewButton()

                        //Setting2(animatedValueList)
                    }
                }
            }

        }
    }
}

@Composable
fun SettingAdviceOrForcing() {
    //Log.d("aaaa","aaaa")
    val viewModel = hiltViewModel<MainPageViewModel>()
    val adviceOrForcing by viewModel.adviceOrForcing.collectAsState()

    SettingOption(
        title = "권유 or 강요",
        adviceOrForcing = adviceOrForcing,
        onOptionSelected = { clickedIsLeft ->
            viewModel.clickAdviceOrForcing(clickedIsLeft)
        },)
}

@Composable
fun SettingTime() {
    val viewModel = hiltViewModel<MainPageViewModel>()

    val myTime by viewModel.myTime.collectAsState()
    val sameEveryDay by viewModel.sameEveryDay.collectAsState()
    val currentMyTimeTab by viewModel.currentMyTimeTab.collectAsState()

    //Log.d("aaaab",myTime.toString())
    //Log.d("aaaab",currentMyTimeTab.toString())

    TimeSetting(
        title = "알림을 받을 시간을 설정해 주세요.\n잠자기 30분 정도가 좋아요.",
        selectedHour = myTime[currentMyTimeTab].first,
        selectedMinute = myTime[currentMyTimeTab].second,
        onHourChange = { viewModel.setMyTime(hour = it) },
        onMinuteChange = { viewModel.setMyTime(minute = it) },
        sameEveryDay = sameEveryDay,
        onToggleSameEveryDay = { viewModel.setSameEveryDay(isToggle = true) },
        currentMyTimeTab = currentMyTimeTab,
        onDayTabClick = { viewModel.setCurrentMyTimeTab(it) }
    )
}

@Composable
fun DSCalender(){


    val viewModel = hiltViewModel<MainPageViewModel>()



    val selectedYearAndMonth by viewModel.selectedYearAndMonth.collectAsState()
    val calenderEntries by viewModel.calenderEntries.collectAsState()

    Column {
        Row{
            PrevButton { viewModel.prevMonth()}
            Text(text = selectedYearAndMonth.second.toString())
            NextButton {viewModel.nextMonth()}
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
        ){
            items(calenderEntries){
                Box(modifier = Modifier
                    .height(30.dp)
                    .padding(1.dp)
                    .border(width = 1.dp, color = Color.Gray)
                    .then(
                        if (it.isBlank) Modifier.background(color = Color.Gray)
                        else if (it.isWritten) Modifier.background(color = Color.Green)
                        else Modifier
                    )
                    .clickable {
                        viewModel.setSummary(content = "Dummy",day= it.day)
                    }
                ){
                    if(!it.isBlank)Text(text = it.day.toString())
                }
            }
        }
    }
}

@Composable
fun PrevButton(onClick : () ->Unit){
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Outlined.KeyboardArrowLeft,
            contentDescription = "Prev"
        )
    }
}

@Composable
fun NextButton(onClick : () ->Unit){
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Outlined.KeyboardArrowRight,
            contentDescription = "Next"
        )
    }
}

@Composable
fun SettingSaveButton() {
    val viewModel = hiltViewModel<MainPageViewModel>()

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
            startActivity(context,intent,null)

            setPermissionRequested(false)
        }
    }

    AnimatedActionButton(
        text = "설정 완료",
        onClick = {
            viewModel.settingConfirm()
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


}

@Composable
fun SettingPreviewButton() {
    val viewModel = hiltViewModel<MainPageViewModel>()
    val context = LocalContext.current

    AnimatedActionButton(
        text = "미리보기",
        onClick = {
            viewModel.previewSetting(context)
        },
        backgroundColor = MaterialTheme.colorScheme.primary,
        textColor = Color.White,
    )
}



