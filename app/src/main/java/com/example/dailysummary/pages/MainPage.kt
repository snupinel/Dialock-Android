package com.example.dailysummary.pages

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dailysummary.components.AnimatedActionButton
import com.example.dailysummary.components.DSCalender
import com.example.dailysummary.components.SettingOption
import com.example.dailysummary.components.TabNavigationBar
import com.example.dailysummary.components.TimeSetting
import com.example.dailysummary.model.BottomNavItem
import com.example.dailysummary.viewModel.MainPageViewModel
import com.example.dailysummary.viewModel.Tab

val homeTab = BottomNavItem(tag = "홈", title = "Home", selectedIcon = Icons.Filled.Home, unselectedIcon = Icons.Outlined.Home)
val myTab = BottomNavItem(tag="설정", title="My", selectedIcon = Icons.Filled.Person, unselectedIcon = Icons.Outlined.Person)
val socialTab = BottomNavItem(tag="소셜", title="Social", selectedIcon = Icons.Filled.Groups, unselectedIcon = Icons.Outlined.Groups)


// creating a list of all the tabs
val tabBarItems = listOf(socialTab,homeTab, myTab,)

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
                .padding(all = 8.dp)
        ) {
            when (selectedTab){
                Tab.Home -> {
                    DSCalender()
                    //SameEveryDayToggle(sameEveryDay = false, onToggle = {viewModel.setSameEveryDay(isToggle = true)})
                }
                Tab.My -> {
                    //
                    Column {
                        SettingAdviceOrForcing()

                        SettingTime()

                        SettingSaveButton()

                        SettingPreviewButton()

                        //Setting2(animatedValueList)
                    }
                }
                Tab.Social-> {

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
            ContextCompat.startActivity(context,intent,null)

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

