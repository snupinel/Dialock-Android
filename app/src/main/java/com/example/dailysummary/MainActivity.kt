package com.example.dailysummary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dailysummary.pages.MainPage
import com.example.dailysummary.pages.StartPage
import com.example.dailysummary.ui.theme.DailySummaryTheme
import com.example.dailysummary.viewModel.InitialSettingViewModel
import com.example.dailysummary.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailySummaryTheme{
                //startService(Intent(this, MyService::class.java))
                // A surface container using the 'background' color from the theme
                if(viewModel.isSettingCompleted()){
                    Log.d("aaaa","completed")
                    MyApp(startDestination = "MainPage")
                }else{
                    MyApp()
                }
            }
        }
    }
}

@Composable
private fun MyApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "StartPage"
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
        }
    }

}