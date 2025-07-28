package com.example.dailysummary.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.OrientationEventListener
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
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
import com.example.dailysummary.dto.Summary
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
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
    private var day =1
    private var isNextDay =false



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        year = intent?.getIntExtra("year", 0) ?: 0
        month = intent?.getIntExtra("month", 0) ?: 0
        day = intent?.getIntExtra("day", 0) ?: 0
        isNextDay = intent?.getBooleanExtra("isNextDay", false) ?: false

        serviceScope.launch {
            val summaries = summaryRepository.getSummariesByDate(
                LocalDate.of(year, month, day).let {
                    if (isNextDay) it.minusDays(1) else it
                }
            )
            Log.d("summaryservice", summaries.toString())

            alarmScheduler.scheduleOverlay()
            if (summaries.isEmpty() || summaries.all { !it.shouldBlockAlarm }) {
                withContext(Dispatchers.Main) {
                    showOverlay(isWritten = summaries.isNotEmpty())
                }
            }
            else{
                stopSelf()
            }


        }

        return START_NOT_STICKY
    }




    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.Theme_DailySummary)
        Log.d("summaryservice","SummaryService activated")
    }



    private fun showOverlay(isWritten:Boolean) {
        Log.d("summaryservice", "showOverlay activated")

        fun createParams():WindowManager.LayoutParams {
            return WindowManager.LayoutParams(
                (Resources.getSystem().displayMetrics.widthPixels * 0.8).toInt(),
                (Resources.getSystem().displayMetrics.heightPixels * 0.6).toInt(),
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
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
        }

        val composeView = ComposeView(this)

        val listener = object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                windowManager.updateViewLayout(composeView, createParams())
            }
        }
        listener.enable()

        composeView.setContent {

            Overlay(
                close = {
                    listener.disable()
                    windowManager.removeView(composeView)
                    stopSelf()
                },
                isWritten = isWritten,
                getSetting = { prefRepository.getRefSetting()!!},
                saveDiary = { content, isLikeChecked, dayRating ->
                    serviceScope.launch {
                        summaryRepository.insertSummary(Summary(
                            writtenTime = LocalDateTime.now(),
                            date = LocalDate.of(year,month,day).let{
                                if (isNextDay) it.minusDays(1) else it
                            },
                            title = content,
                            content = "",
                            isBookmarked = isLikeChecked,
                            dayRating = dayRating,
                            imageUris = emptyList(),
                            shouldBlockAlarm = false,
                        ))
                    }

                }

                //setTextFieldValue =
            )
        }


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



        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        windowManager.addView(composeView, createParams())
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


}






