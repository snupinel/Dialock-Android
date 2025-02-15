package com.example.dailysummary.pages

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dailysummary.components.AnimatedActionButton
import com.example.dailysummary.components.BackButton
import com.example.dailysummary.components.SettingOption
import com.example.dailysummary.components.TimeSetting
import com.example.dailysummary.viewModel.MainPageViewModel
import com.example.dailysummary.viewModel.SettingPageViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmSettingPage(navController: NavController){

    val viewModel = hiltViewModel<SettingPageViewModel>()
    LaunchedEffect(Unit){
        viewModel.settingInitialize()

    }

    Scaffold(
        topBar = {
            AlarmSettingToolBar{
                navController.popBackStack()
            }
        }
    ){ paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            SettingAdviceOrForcing()

            SettingTime()

            SettingSaveButton()

            SettingPreviewButton()

        }
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
    val viewModel = hiltViewModel<SettingPageViewModel>()

    val myTime by viewModel.myTime.collectAsState()
    val sameEveryDay by viewModel.sameEveryDay.collectAsState()
    val currentMyTimeTab by viewModel.currentMyTimeTab.collectAsState()
    val isNextDay = viewModel.myTime.collectAsState().value[currentMyTimeTab].isNextDay

    //Log.d("aaaab",myTime.toString())
    //Log.d("aaaab",currentMyTimeTab.toString())

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
        onToggleIsNextDay = {viewModel.setIsNextDay(!isNextDay)}
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
}

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
