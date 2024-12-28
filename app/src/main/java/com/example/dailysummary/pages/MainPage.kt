package com.example.dailysummary.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dailysummary.components.SameEveryDayToggle
import com.example.dailysummary.components.SettingOption
import com.example.dailysummary.components.TabNavigationBar
import com.example.dailysummary.components.TimeSetting
import com.example.dailysummary.dto.AnimationTarget
import com.example.dailysummary.model.BottomNavItem
import com.example.dailysummary.viewModel.InitialSettingViewModel
import com.example.dailysummary.viewModel.MainPageViewModel
import com.example.dailysummary.viewModel.Tab

val calenderTab = BottomNavItem(tag = "캘린더", title = "Calender", selectedIcon = Icons.Filled.Home, unselectedIcon = Icons.Outlined.Home)
val settingTab = BottomNavItem(tag="설정", title="Setting", selectedIcon = Icons.Filled.Settings, unselectedIcon = Icons.Outlined.Settings)


// creating a list of all the tabs
val tabBarItems = listOf(calenderTab, settingTab)

@Composable
fun MainPage(navController: NavController){

    val viewModel = hiltViewModel<MainPageViewModel>()

    LaunchedEffect(Unit){
        viewModel.settingInitialize()
        viewModel.setCalenderEntries()
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
        Text(text = selectedYearAndMonth.second.toString())
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
                    )){
                    if(!it.isBlank)Text(text = it.day.toString())
                }
            }
        }
    }
}



