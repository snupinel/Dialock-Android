package com.example.dailysummary.pages

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dailysummary.components.AlarmButton
import com.example.dailysummary.components.AnimatedActionButton
import com.example.dailysummary.components.DSCalender
import com.example.dailysummary.components.MenuButton
import com.example.dailysummary.components.SettingOption
import com.example.dailysummary.components.TabNavigationBar
import com.example.dailysummary.components.TimeSetting
import com.example.dailysummary.model.BottomNavItem
import com.example.dailysummary.viewModel.MainPageViewModel
import com.example.dailysummary.viewModel.Tab
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

val homeTab = BottomNavItem(tag = "홈", title = "Home", selectedIcon = Icons.Filled.Home, unselectedIcon = Icons.Outlined.Home)
val myTab = BottomNavItem(tag="MY", title="My", selectedIcon = Icons.Filled.Person, unselectedIcon = Icons.Outlined.Person)
val socialTab = BottomNavItem(tag="소셜", title="Social", selectedIcon = Icons.Filled.Groups, unselectedIcon = Icons.Outlined.Groups)


// creating a list of all the tabs
val tabBarItems = listOf(homeTab,socialTab,myTab)

//@Inject lateinit var alarmScheduler: AlarmScheduler


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainPage(navController: NavController){

    val backStackEntry = navController.currentBackStackEntryAsState().value
    val shouldRefresh = backStackEntry?.savedStateHandle?.get<Boolean>("shouldRefresh")?:false

    val viewModel = hiltViewModel<MainPageViewModel>()



    LaunchedEffect(shouldRefresh){
        //viewModel.setShowPopup(false)
        //viewModel.calenderRefresh()
        //viewModel.setCalenderEntries()

        backStackEntry?.savedStateHandle?.set("shouldRefresh", false)
    }

    val selectedTab by viewModel.selectedTab.collectAsState()

    Scaffold(
        modifier = Modifier.height(100.dp),
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
                Tab.Home -> {
                    DSCalender(navController = navController)
                    //SameEveryDayToggle(sameEveryDay = false, onToggle = {viewModel.setSameEveryDay(isToggle = true)})
                }
                Tab.My -> {
                    //

                }
                Tab.Social-> {
                    SocialTab()
                }
            }

        }
    }
}

@Composable
fun AlarmSettingFloating(onClick:()->Unit){
    ExtendedFloatingActionButton(
        modifier = Modifier.size(60.dp),
        onClick = onClick,
        shape = CircleShape,
        contentColor = Color.White,
        containerColor = MaterialTheme.colorScheme.primaryContainer,

    ){
        Icon(imageVector = Icons.Filled.Alarm, contentDescription = "AlarmSetting")
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

