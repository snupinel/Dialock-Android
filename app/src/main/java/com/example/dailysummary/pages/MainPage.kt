package com.example.dailysummary.pages

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

    val selectedTab by viewModel.selectedTab.collectAsState()

    Scaffold(bottomBar = { TabNavigationBar(tabBarItems) }, topBar = {}) {paddingValues->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues)
        ) {
            when (selectedTab){
                Tab.Calender -> {
                    DSCalender()
                }
                Tab.Setting -> {

                }
            }

        }
    }
}

@Composable
fun DSCalender(){


    val viewModel = hiltViewModel<MainPageViewModel>()

    LaunchedEffect(Unit){
        viewModel.setCalenderEntries()
    }

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
                        if(it.isBlank) Modifier.background(color = Color.Gray)
                        else if(it.isWritten) Modifier.background(color = Color.Green)
                        else Modifier
                    )){
                    if(!it.isBlank)Text(text = it.day.toString())
                }
            }
        }
    }
}

@Composable
fun TabNavigationBar(tabBarItems: List<BottomNavItem>) {
    val viewModel = hiltViewModel<MainPageViewModel>()

    val selectedTabPage by viewModel.selectedTab.collectAsState()
    NavigationBar(
        containerColor = Color.White) {
        // looping over each tab to generate the views and navigation for each item
        tabBarItems.forEach{ tabBarItem ->
            NavigationBarItem(
                selected = selectedTabPage == Tab.valueOf(tabBarItem.title),
                onClick = {
                    viewModel.updateTab(tabBarItem.title)
                },
                icon = {
                    TabBarIconView(
                        isSelected = selectedTabPage == Tab.valueOf(tabBarItem.title),
                        selectedIcon = tabBarItem.selectedIcon,
                        unselectedIcon = tabBarItem.unselectedIcon,
                        title = tabBarItem.title,
                        badgeAmount = tabBarItem.badgeAmount
                    )
                },
                label = {Text(tabBarItem.tag)})
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabBarIconView(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    title: String,
    badgeAmount: Int? = null
) {
    BadgedBox(badge = { TabBarBadgeView(badgeAmount) }) {
        Icon(
            imageVector = if (isSelected) {selectedIcon} else {unselectedIcon},
            contentDescription = title
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TabBarBadgeView(count: Int? = null) {
    if (count != null) {
        Badge {
            Text(count.toString())
        }
    }
}

