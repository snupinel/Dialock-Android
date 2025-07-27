package com.example.dailysummary

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.dailysummary.components.PortraitLikeWrapper
import com.example.dailysummary.pages.AlarmSettingPage
import com.example.dailysummary.pages.BookmarkedDiariesPage
import com.example.dailysummary.pages.DiaryPage
import com.example.dailysummary.pages.MainPage
import com.example.dailysummary.pages.TimeSettingPage
import com.example.dailysummary.pages.WriteDiaryPage
import com.example.dailysummary.pages.initialPages.FeatureIntroPage
import com.example.dailysummary.pages.initialPages.GreetingPage
import com.example.dailysummary.pages.initialPages.InitialTimeSettingPage
import com.example.dailysummary.pages.initialPages.PermissionRequestPage
import com.example.dailysummary.ui.theme.DailySummaryTheme
import com.example.dailysummary.viewModel.MainViewModel
import com.example.dailysummary.viewModel.SettingPageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isTablet()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        setContent {
            DailySummaryTheme{
                SideEffect {
                    window?.let {
                        WindowCompat.setDecorFitsSystemWindows(it, false) // ✅ 시스템 UI가 콘텐츠를 덮지 않도록 설정
                        it.decorView.setOnApplyWindowInsetsListener { view, windowInsets ->
                            val statusBarInsets = windowInsets.getInsets(WindowInsets.Type.statusBars())
                            view.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                            //view.setPadding(0,statusBarInsets.top,0,0)
                            windowInsets

                        }

                    }
                }

                //startService(Intent(this, MyService::class.java))
                // A surface container using the 'background' color from the theme


                val isCompleted = remember { viewModel.isSettingCompleted() }
                Log.d("oncreate","DailySummaryTheme가...$isCompleted")

                if(isTablet()){
                    PortraitLikeWrapper{
                        if (isCompleted) {
                            MyApp(startDestination = "MainPage")
                        } else {
                            MyApp()
                        }
                    }
                }else{
                    if (isCompleted) {
                        MyApp(startDestination = "MainPage")
                    } else {
                        MyApp()
                    }
                }


            }
        }
    }
    private fun isTablet(): Boolean {
        val metrics = resources.displayMetrics
        val widthDp = metrics.widthPixels / metrics.density
        return widthDp >= 600 // 600dp 이상이면 태블릿으로 간주
    }

}

@Composable
private fun MyApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "GreetingPage",
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = {
                // ✅ navigate() 시: 새 화면이 아래에서 위로 올라옴
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight }, // 화면 아래에서 시작
                    animationSpec = tween(
                        durationMillis = 350,
                        easing = FastOutSlowInEasing // ✅ 자연스러운 가속/감속
                    )
                )
            },
            exitTransition = {
                             ExitTransition.None
            },
            popEnterTransition = {
                // ✅ 뒤로가기(pop): 이전 화면 그대로 나타남
                EnterTransition.None
            },
            popExitTransition = {
                // ✅ 뒤로가기(pop): 현재 화면이 아래로 내려가며 사라짐
                slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(
                        durationMillis = 350,
                        easing = FastOutSlowInEasing
                    )
                )
            }
            ) {
            composable("MainPage") {
                MainPage(navController)
            }
            /*
            composable("SummaryPage/{year}/{month}/{day}"){
                val year = it.arguments!!.getString("year")!!.toInt()
                val month = it.arguments!!.getString("month")!!.toInt()
                val day = it.arguments!!.getString("day")!!.toInt()
                Log.d("aaaa",year.toString())
                //SummaryPage(navController,year,month,day)
            }*/
            composable(
                route = "WriteDiaryPage/{year}/{month}/{day}?id={id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.IntType
                        defaultValue = -1 // ✅ id가 없으면 -1로 설정
                    }
                )
            ) { backStackEntry ->
                val year = backStackEntry.arguments!!.getString("year")!!.toInt()
                val month = backStackEntry.arguments!!.getString("month")!!.toInt()
                val day = backStackEntry.arguments!!.getString("day")!!.toInt()

                val rawId = backStackEntry.arguments?.getInt("id") ?: -1
                val id: Int? = if (rawId == -1) null else rawId

                Log.d("WriteDiaryPage navigate", "id=$id")
                WriteDiaryPage(navController, year, month, day, id)
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
            composable("DiaryPage/{id}") {
                val id = it.arguments!!.getString("id")!!.toInt()
                DiaryPage(navController = navController, id = id)
            }
            composable("GreetingPage") {
                GreetingPage(navController = navController)
            }
            composable("FeatureIntroPage") {
                FeatureIntroPage(navController = navController)
            }
            composable("PermissionRequestPage"){
                PermissionRequestPage(navController)
            }
            composable("InitialTimeSettingPage"){
                InitialTimeSettingPage(navController)
            }
            composable("BookmarkedDiariesPage") {
                BookmarkedDiariesPage(navController)
            }
        }
    }

}
