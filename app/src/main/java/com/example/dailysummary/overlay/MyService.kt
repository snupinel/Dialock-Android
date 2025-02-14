package com.example.dailysummary.overlay

import android.app.Service
import android.content.Intent
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.dailysummary.R
import com.example.dailysummary.components.Overlay
import com.example.dailysummary.data.PrefRepository
import com.example.dailysummary.data.SummaryRepository
import com.example.dailysummary.dto.AdviceOrForcing
import com.example.dailysummary.dto.Setting
import com.example.dailysummary.dto.Summary
import com.example.dailysummary.ui.theme.DailySummaryTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class SummaryService  : Service() {

    @Inject
    lateinit var prefRepository : PrefRepository

    @Inject
    lateinit var summaryRepository: SummaryRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler


    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    //private val viewModel: OverlayViewModel by viewModels()

    private val windowManager get() = getSystemService(WINDOW_SERVICE) as WindowManager
    //private val viewModelStore = ViewModelStore()

    private var year=2000
    private var month =1
    var day =1


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
         year = intent?.getIntExtra("year",0)!!
         month = intent?.getIntExtra("month",0)!!
         day = intent?.getIntExtra("day",0)!!

        // 전달받은 매개변수 처리
        //Log.d("MyService", "Value1: $value1, Value2: $value2")

        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.Theme_DailySummary)
        Log.d("summaryservice","SummaryService activated")
        showOverlay()
        alarmScheduler.scheduleOverlay()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showOverlay() {


        val layoutFlag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            (Resources.getSystem().displayMetrics.widthPixels * 0.8).toInt(),
            (Resources.getSystem().displayMetrics.heightPixels * 0.6).toInt(),
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND,  // 배경 흐리게
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER // 중앙 배치
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            dimAmount = 0.5f  // 배경 흐림 정도 설정

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                blurBehindRadius = 20 // API 31 이상에서 블러 효과 추가
                flags = flags or WindowManager.LayoutParams.FLAG_BLUR_BEHIND
            }
        }




        val composeView = ComposeView(this)


        // Trick The ComposeView into thinking we are tracking lifecycle
        val viewModelStoreOwner = object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore
                get() = ViewModelStore()
        }
        val lifecycleOwner = MyLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)



        composeView.setContent {

            Overlay(
                close = {
                    windowManager.removeView(composeView)
                    stopSelf()
                },
                getSetting = { prefRepository.getRefSetting()!!},
                saveDiary = { content, isLikeChecked, isThumbUp ->
                    serviceScope.launch {
                        summaryRepository.insertSummary(Summary(
                            writtenTime = LocalDate.now(),
                            date = LocalDate.of(year,month,day),
                            title = content,
                            content = "",
                            isLikeChecked = isLikeChecked,
                            isThumbUp = isThumbUp,
                        ))
                    }

                }
                //setTextFieldValue =
            )
        }

        // This is required or otherwise the UI will not recompose
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        windowManager.addView(composeView, params)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


}






