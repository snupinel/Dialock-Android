package com.example.dailysummary

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.dailysummary.pages.AlarmSettingPage
import com.example.dailysummary.pages.MainPage
import com.example.dailysummary.pages.StartPage
import com.example.dailysummary.pages.SummaryPage
import com.example.dailysummary.pages.TimeSettingPage
import com.example.dailysummary.pages.initialPages.FeatureIntroPage
import com.example.dailysummary.pages.initialPages.GreetingPage
import com.example.dailysummary.pages.initialPages.InitialSettingPage
import com.example.dailysummary.pages.initialPages.PermissionRequestPage
import com.example.dailysummary.ui.theme.DailySummaryTheme
import com.example.dailysummary.viewModel.InitialSettingViewModel
import com.example.dailysummary.viewModel.MainViewModel
import com.example.dailysummary.viewModel.SettingPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailySummaryTheme{

                window?.let {
                    WindowCompat.setDecorFitsSystemWindows(it, false) // ✅ 시스템 UI가 콘텐츠를 덮지 않도록 설정
                    it.decorView.setOnApplyWindowInsetsListener { view, windowInsets ->
                        val statusBarInsets = windowInsets.getInsets(WindowInsets.Type.statusBars())
                        view.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        //view.setPadding(0,statusBarInsets.top,0,0)
                        windowInsets

                    }

                        //window.decorView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    //it.statusBarColor = Color.Transparent.toArgb()
                    //it.navigationBarColor = Color.Transparent.toArgb()// ✅ 상태바 배경을 투명하게 설정
                    Log.d("ac","activate WindowCompat 그뭐시기")
                }

                //startService(Intent(this, MyService::class.java))
                // A surface container using the 'background' color from the theme


                if(viewModel.isSettingCompleted()){
                    Log.d("aaaa","completed")
                    MyApp(startDestination = "MainPage")
                }else{
                    MyApp()
                    //MyApp(startDestination = "MainPage")
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MyApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "GreetingPage"
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable("StartPage") {
                StartPage(navController)
            }
            composable("MainPage") {
                MainPage(navController)
            }
            composable("SummaryPage/{year}/{month}/{day}"){
                val year = it.arguments!!.getString("year")!!.toInt()
                val month = it.arguments!!.getString("month")!!.toInt()
                val day = it.arguments!!.getString("day")!!.toInt()
                Log.d("aaaa",year.toString())
                SummaryPage(navController,year,month,day)
            }

            navigation(
                startDestination = "AlarmSettingPage",
                route = "alarm_graph"
            ){
                composable("AlarmSettingPage"){
                    val parentEntry = remember(it) {
                        navController.getBackStackEntry("alarm_graph")
                    }
                    val viewModel = hiltViewModel<SettingPageViewModel>(parentEntry)
                    AlarmSettingPage(navController,viewModel)
                }
                composable("TimeSettingPage") {
                    val parentEntry = remember(it) {
                        navController.getBackStackEntry("alarm_graph")
                    }
                    val viewModel = hiltViewModel<SettingPageViewModel>(parentEntry)

                    TimeSettingPage(navController, viewModel)
                }

            }
            composable("GreetingPage") {
                GreetingPage(navController = navController)
            }
            composable("FeatureIntroPage") {
                FeatureIntroPage(navController = navController)
            }
            composable("InitialSettingPage") {
                InitialSettingPage(navController = navController)
            }
            composable("PermissionRequestPage"){
                PermissionRequestPage(navController)
            }
        }
    }

}