package com.example.dailysummary.overlay

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.dailysummary.R
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
class MyService  : Service() {

    @Inject
    lateinit var prefRepository : PrefRepository

    @Inject
    lateinit var summaryRepository: SummaryRepository


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
        //Log.d("aaaa",repository.getPref("hi")?:"null but success")
        showOverlay()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showOverlay() {

        val layoutFlag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
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
                saveDiary = {
                    serviceScope.launch {
                        summaryRepository.insertSummary(Summary(
                            writtenTime = LocalDate.now(),
                            date = LocalDate.of(year,month,day),
                            content = it
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




    fun saveDiary(){

    }
}

@Composable
fun Overlay(
    //viewModel: OverlayViewModel,
    close: () -> Unit,
    //adviceOrForcing: AdviceOrForcing,
    getSetting: () -> Setting,
    //textFieldValue: String,
    //setTextFieldValue: (String) -> Unit,
    saveDiary : (String) -> Unit,
) {
    //val viewModel = hiltViewModel<OverlayViewModel>()

    var adviceOrForcing by remember {
        mutableStateOf(AdviceOrForcing.Advice)
    }

    var textFieldValue by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        adviceOrForcing = getSetting().adviceOrForcing
    }

    DailySummaryTheme(isOverlay = true) {
        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 상단의 글 쓰는 박스
                TextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it},
                    label = { Text("Write something...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .focusable()
                )

                // 하단의 버튼 영역
                when (adviceOrForcing) {
                    AdviceOrForcing.Advice -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(onClick = { close() }) {
                                Text("취소")
                            }
                            Button(onClick = {
                                saveDiary(textFieldValue)
                                close()
                            }) {
                                Text("저장")
                            }
                        }
                    }

                    AdviceOrForcing.Forcing -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(onClick = {
                                saveDiary(textFieldValue)
                                close()
                            }) {
                                Text("저장")
                            }
                        }
                    }
                }
            }
        }
    }
}


