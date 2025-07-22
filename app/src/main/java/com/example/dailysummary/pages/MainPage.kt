package com.example.dailysummary.pages

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dailysummary.components.AlarmButton
import com.example.dailysummary.pages.mainPageTabs.CalenderTab
import com.example.dailysummary.components.MenuButton
import com.example.dailysummary.components.TabNavigationBar
import com.example.dailysummary.dto.BottomNavItem
import com.example.dailysummary.pages.mainPageTabs.HomeTab
import com.example.dailysummary.viewModel.MainPageViewModel
import com.example.dailysummary.viewModel.Tab

val homeTab = BottomNavItem(tag = "홈", title = "Home", selectedIcon = Icons.Filled.Home, unselectedIcon = Icons.Outlined.Home)
val calenderTab = BottomNavItem(tag="캘린더", title="Calender", selectedIcon = Icons.Filled.CalendarToday, unselectedIcon = Icons.Outlined.CalendarToday)

val myTab = BottomNavItem(tag="MY", title="My", selectedIcon = Icons.Filled.Person, unselectedIcon = Icons.Outlined.Person)


// creating a list of all the tabs
val tabBarItems = listOf(homeTab,calenderTab,myTab)

//@Inject lateinit var alarmScheduler: AlarmScheduler


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainPage(navController: NavController){

    val backStackEntry = navController.currentBackStackEntryAsState().value
    val shouldRefresh = backStackEntry?.savedStateHandle?.get<Boolean>("shouldRefresh")?:false

    val viewModel = hiltViewModel<MainPageViewModel>()



    LaunchedEffect(shouldRefresh){
        if(shouldRefresh){
            Log.d("MainPage","Main Page Refreshed")
            viewModel.calenderRefresh()
            backStackEntry?.savedStateHandle?.set("shouldRefresh", false)
        }
    }

    val selectedTab by viewModel.selectedTab.collectAsState()

    Scaffold(
        bottomBar = {
            TabNavigationBar(tabBarItems)
                    },
        topBar = {
            MainPageToolbar {
                navController.navigate("AlarmSettingPage")
            }
                 },
        ){ paddingValues->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues)
        ) {
            when (selectedTab){
                Tab.Home-> {
                    HomeTab(navController)
                }
                Tab.Calender -> {
                    CalenderTab(navController = navController)
                    //SameEveryDayToggle(sameEveryDay = false, onToggle = {viewModel.setSameEveryDay(isToggle = true)})
                }
                Tab.My -> {
                    //

                }

            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPageToolbar(
    onSetting:()->Unit,
) {
    //val interpolatedColor = lerp(Color.White, Color.Black, alpha)
    TopAppBar(

        title = { },
        navigationIcon = {
            MenuButton {

            }
        },
        actions = {
            AlarmButton{
                onSetting()
            }
        },


        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        ),
    )

}

