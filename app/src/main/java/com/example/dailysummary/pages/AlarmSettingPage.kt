package com.example.dailysummary.pages

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.dailysummary.components.BackButton

import com.example.dailysummary.components.RevertButton
import com.example.dailysummary.components.WeekdayVerticalList
import com.example.dailysummary.dto.AdviceOrForcing
import com.example.dailysummary.utils.popBackStackExclusive

import com.example.dailysummary.viewModel.SettingPageViewModel
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmSettingPage(navController: NavController,viewModel: SettingPageViewModel){

    val shouldRefresh by viewModel.shouldRefresh.collectAsState()
    val setting by viewModel.setting.collectAsState()
    val weekdayItems = setting.alarmTimesByDay
    val isForceMode = setting.adviceOrForcing == AdviceOrForcing.Forcing
    val isSettingChanged by viewModel.isSettingChanged.collectAsState()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current


    LaunchedEffect(Unit){
        Log.d("AlarmSettingPage","LaunchedEffect act")
        viewModel.settingInitialize()
        viewModel.setShouldRefresh(true)
        viewModel.toggleChangeToggle()
    }
    LaunchedEffect(shouldRefresh){
        if(shouldRefresh){
            viewModel.setShouldRefresh(false)
        }
    }
    Scaffold(
        topBar = {
            AlarmSettingToolBar(
                onBack = {
                    scope.launch {
                        navController.popBackStackExclusive()
                    }
                         },
                onRevert = {viewModel.revertSetting()},
                revertEnable = isSettingChanged
            )
        },
        bottomBar = {
            SettingSaveButton(enabled = isSettingChanged){
                viewModel.saveSetting()
                Toast.makeText(context, "설정 완료", Toast.LENGTH_SHORT).show()
                viewModel.scheduleOverlay()
                viewModel.updateBeforeSetting()
            }
        }
    ){ paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(12.dp)
        ){


            WeekdayVerticalList(
                modifier = Modifier.weight(1f),
                items = weekdayItems,
                onItemClick = { clicked ->
                    viewModel.clearChosenDays()
                    viewModel.setChosenDay(clicked,true)
                    viewModel.pickerInitialize(clicked)
                    navController.navigate("TimeSettingPage")
                }
            )

            WritingModeSelector(
                isForceMode = isForceMode,
                onToggle = { viewModel.setAdviceOrForcing(!isForceMode) }
            )



        }

    }


}






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmSettingToolBar(
    onBack:()->Unit,
    onRevert:()->Unit,
    revertEnable:Boolean
){
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text("루틴 설정", fontSize = 18.sp, fontWeight = FontWeight.Light)
            } },
        navigationIcon = {
            BackButton {
                onBack()
            }
        },
        actions = {
            RevertButton(enabled = revertEnable) {
                onRevert()
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingSaveButton(enabled:Boolean,onclick:()->Unit) {

    val context = LocalContext.current
    val (permissionRequested, setPermissionRequested) = remember { mutableStateOf(false) }

    LaunchedEffect(permissionRequested){
        if(permissionRequested){

            //launcher.launch()
            Toast.makeText(context, "스타트액티비티", Toast.LENGTH_SHORT).show()

            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            ContextCompat.startActivity(context,intent,null)

            setPermissionRequested(false)
        }
    }
    Button(
        modifier = Modifier
            .padding(
                bottom = WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            )
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        enabled = enabled,
        onClick = onclick,
    ){
        Text("설정 저장")
    }
}

@Composable
fun WritingModeSelector(
    modifier: Modifier = Modifier,
    isForceMode: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "작성 유도 방식",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("권유", color = if (!isForceMode) MaterialTheme.colorScheme.onBackground else Color.Gray)
                Spacer(modifier = Modifier.width(12.dp))
                Switch(checked = isForceMode, onCheckedChange = onToggle)
                Spacer(modifier = Modifier.width(12.dp))
                Text("강요", color = if (isForceMode) MaterialTheme.colorScheme.onBackground else Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isForceMode)
                "일기를 작성하지 않는다면, 뒷 일은 책임 못 져요!"
            else
                "일기 쓸 시간이에요!",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
