package com.example.dailysummary.pages

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dailysummary.R
import com.example.dailysummary.components.AlarmButton
import com.example.dailysummary.pages.mainPageTabs.CalenderTab
import com.example.dailysummary.components.MenuButton
import com.example.dailysummary.components.TabNavigationBar
import com.example.dailysummary.dto.BottomNavItem
import com.example.dailysummary.pages.mainPageTabs.HomeTab
import com.example.dailysummary.pages.mainPageTabs.MyTab
import com.example.dailysummary.viewModel.MainPageViewModel
import com.example.dailysummary.viewModel.Tab
import kotlinx.coroutines.launch

val homeTab = BottomNavItem(tag = "홈", title = "Home", selectedIcon = Icons.Filled.Home, unselectedIcon = Icons.Outlined.Home)
val calenderTab = BottomNavItem(tag="캘린더", title="Calender", selectedIcon = Icons.Filled.CalendarToday, unselectedIcon = Icons.Outlined.CalendarToday)

val myTab = BottomNavItem(tag="MY", title="My", selectedIcon = Icons.Filled.Person, unselectedIcon = Icons.Outlined.Person)


// creating a list of all the tabs
val tabBarItems = listOf(homeTab,calenderTab,myTab)

//@Inject lateinit var alarmScheduler: AlarmScheduler


@Composable
fun MainPage(navController: NavController){


    val viewModel = hiltViewModel<MainPageViewModel>()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()



    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(250.dp)
            ){
                Image(
                    painter = painterResource(id = R.drawable.dialock_banner),
                    contentDescription = "Dialock Banner",
                    modifier = Modifier
                        .fillMaxWidth()
                )
                NavigationDrawerItem(
                    label = { Text("홈") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    label = { Text("설정") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                TabNavigationBar(tabBarItems)
            },
            topBar = {
                MainPageToolbar(
                    title = tabBarItems[selectedTab.ordinal].tag
                ){
                    scope.launch {
                        drawerState.open() // ✅ Drawer 열기
                    }
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
                        MyTab(navController = navController)

                    }

                }

            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPageToolbar(
    title:String,
    onMenu:()->Unit,
) {
    //val interpolatedColor = lerp(Color.White, Color.Black, alpha)
    TopAppBar(

        title = { Text(title, fontWeight = FontWeight.ExtraBold)},
        actions = {
            MenuButton {
                onMenu()
            }
            /*
            AlarmButton{
                onSetting()
            }*/
        },


        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        ),
    )

}

