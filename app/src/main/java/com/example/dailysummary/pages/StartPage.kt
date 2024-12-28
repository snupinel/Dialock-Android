package com.example.dailysummary.pages

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dailysummary.components.DayTabs
import com.example.dailysummary.components.SettingOption
import com.example.dailysummary.components.TimePicker
import com.example.dailysummary.components.TimeSetting
import com.example.dailysummary.dto.AnimationTarget
import com.example.dailysummary.viewModel.InitialSettingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.roundToInt


const val objectMaxIndex=11
public var durationMillis=0
@Composable
fun StartPage(navController: NavController){
    val viewModel = hiltViewModel<InitialSettingViewModel>()

    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    val startPageAnimationState by viewModel.startPageAnimationState.collectAsState()

    // 시작하자마자
    LaunchedEffect(Unit) {
        delay(500)

        //올라오면서 인사 등장
        viewModel.setStartPageAnimationState(1)
        delay(1500)

        //도와드릴게요 등장
        viewModel.setStartPageAnimationState(2)
        delay(1500)

        //버튼 등장
        viewModel.setStartPageAnimationState(3)
        delay(500)

        //버튼 이제 누를 수 있음
        viewModel.setStartPageAnimationState(4)
    }


    LaunchedEffect(startPageAnimationState) {
        if(startPageAnimationState==7){
            delay(500)
            viewModel.setStartPageAnimationState(8)
        }
    }
    val animatedValueList = (0..objectMaxIndex).map { obId ->
        val alpha by animateFloatAsState(
            targetValue = getTargetValue(obId, startPageAnimationState, boxSize).alpha,
            animationSpec = tween(durationMillis = durationMillis), label = ""
        )
        val offsetY by animateDpAsState(
            targetValue = getTargetValue(obId, startPageAnimationState, boxSize).offsetY,
            animationSpec = tween(durationMillis = durationMillis), label = ""
        )
        AnimationTarget(alpha, offsetY)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .onGloballyPositioned { layoutCoordinates ->
                // Box의 크기를 저장
                boxSize = layoutCoordinates.size
                //Log.d("offset", boxSize.toString())
            },
        horizontalAlignment = Alignment.CenterHorizontally,)
    {
        if (startPageAnimationState in 0..5) Greeting(animatedValueList)

        if (startPageAnimationState in 3..5) SettingStartButton(animatedValueList)

        if (startPageAnimationState in 3..8) Setting1(animatedValueList)

        if (startPageAnimationState in 3..8) Setting2(animatedValueList)

        if (startPageAnimationState in 3..8) SettingEndButton(animatedValueList)

        if (startPageAnimationState in 6..12) PermissionGuide(animatedValueList)

        if (startPageAnimationState in 6..12) PermissionButton(animatedValueList,navController)
    }

}

@Composable
fun Greeting(animatedValueList:List<AnimationTarget>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .offset(y = animatedValueList[0].offsetY)
            .alpha(animatedValueList[0].alpha)
    ) {
        Text(
            text = "Daily",
            fontSize = 50.sp,
            fontWeight = FontWeight.Light,
            color = Color.LightGray
        )
        Text(
            text = "Summary",
            fontSize = 50.sp,
            fontWeight = FontWeight.Light,
            color = Color.Gray
        )
    }
    Text(
        text = "에 오신 것을 환영합니다!",
        fontSize = 20.sp,
        fontWeight = FontWeight.Light,
        color = Color.DarkGray,
        modifier = Modifier
            .offset(y = animatedValueList[1].offsetY)
            .alpha(animatedValueList[1].alpha)
    )
    Text(
        text = "당신의 멋진 하루하루를 쉽고 간편하게 보관할 수 있도록 저희가 도와드릴게요!",
        fontSize = 20.sp,
        fontWeight = FontWeight.Light,
        color = Color.DarkGray,
        modifier = Modifier
            .offset(y = animatedValueList[2].offsetY)
            .alpha(animatedValueList[2].alpha),
        textAlign = TextAlign.Center
    )

}

@Composable
fun SettingStartButton(animatedValueList: List<AnimationTarget>){
    val viewModel = hiltViewModel<InitialSettingViewModel>()

    val startPageAnimationState by viewModel.startPageAnimationState.collectAsState()

    Box(modifier = Modifier
        .offset(y = animatedValueList[3].offsetY)
        .alpha(animatedValueList[3].alpha)
        .height(60.dp)
        .fillMaxWidth()
        .clip(shape = RoundedCornerShape(4.dp))
        .background(color = MaterialTheme.colorScheme.background)
        .then(
            if (startPageAnimationState == 4) {
                Modifier.clickable {
                    viewModel.setStartPageAnimationState(5)
                }
            } else Modifier
        ), contentAlignment = Alignment.Center
    ) {
        Text("초기 설정 시작하기!", color = Color.White)
    }
}

@Composable
fun Setting1(animatedValueList: List<AnimationTarget>) {
    val viewModel = hiltViewModel<InitialSettingViewModel>()
    val startPageAnimationState by viewModel.startPageAnimationState.collectAsState()
    val adviceOrForcing by viewModel.adviceOrForcing.collectAsState()

    SettingOption(
        animatedValue = animatedValueList[4],
        title = "권유 or 강요",
        adviceOrForcing = adviceOrForcing,
        onOptionSelected = { clickedIsLeft ->
            viewModel.clickAdviceOrForcing(clickedIsLeft)
        },
        isEnabled = startPageAnimationState >= 5,
        startPageAnimationState = startPageAnimationState,
        onNextState = { viewModel.setStartPageAnimationState(6) }
    )
}






@Composable
fun Setting2(animatedValueList: List<AnimationTarget>) {
    val viewModel = hiltViewModel<InitialSettingViewModel>()

    val myTime by viewModel.myTime.collectAsState()
    val sameEveryDay by viewModel.sameEveryDay.collectAsState()
    val currentMyTimeTab by viewModel.currentMyTimeTab.collectAsState()

    TimeSetting(
        animatedValues = animatedValueList.subList(6, 9),
        title = "알림을 받을 시간을 설정해 주세요.\n잠자기 30분 정도가 좋아요.",
        selectedHour = myTime[currentMyTimeTab].first,
        selectedMinute = myTime[currentMyTimeTab].second,
        onHourChange = { viewModel.setMyTime(hour = it) },
        onMinuteChange = { viewModel.setMyTime(minute = it) },
        sameEveryDay = sameEveryDay,
        onToggleSameEveryDay = { viewModel.toggleSameEveryDay() },
        currentMyTimeTab = currentMyTimeTab,
        onDayTabClick = { viewModel.setCurrentMyTimeTab(it) }
    )
}


@Composable
fun SettingEndButton(animatedValueList: List<AnimationTarget>){
    val viewModel = hiltViewModel<InitialSettingViewModel>()

    val startPageAnimationState by viewModel.startPageAnimationState.collectAsState()

    Box(modifier = Modifier
        .offset(y = animatedValueList[9].offsetY)
        .alpha(animatedValueList[9].alpha)
        .height(60.dp)
        .fillMaxWidth()
        .clip(shape = RoundedCornerShape(4.dp))
        .background(color = MaterialTheme.colorScheme.background)
        .then(
            if (startPageAnimationState == 6) {
                Modifier.clickable {
                    viewModel.setStartPageAnimationState(7)
                }
            } else Modifier
        ), contentAlignment = Alignment.Center
    ) {
        Text("설정 완료", color = Color.White)
    }
}






@Composable
fun PermissionGuide(animatedValueList: List<AnimationTarget>){
    Text(
        text = "권한을 설정해라 애송이",
        fontSize = 20.sp,
        fontWeight = FontWeight.Light,
        color = Color.DarkGray,
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = animatedValueList[10].offsetY)
            .alpha(animatedValueList[10].alpha),
        textAlign = TextAlign.Center
    )
}

@Composable
fun PermissionButton(animatedValueList: List<AnimationTarget>,navController: NavController){
    val viewModel = hiltViewModel<InitialSettingViewModel>()

    val context=LocalContext.current

    val startPageAnimationState by viewModel.startPageAnimationState.collectAsState()

    val (permissionRequested, setPermissionRequested) = remember { mutableStateOf(false) }
    /*
    val allPermissionsGranted = viewModel.neededPermissions().all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

     */

    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        // 권한 요청 결과 처리. permissions는 Map<String, Boolean> 형태입니다.
        if(permissions.entries.all { it.value }){
            //
        }
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (Settings.canDrawOverlays(context)) {
            Toast.makeText(context, "Overlay permission granted", Toast.LENGTH_SHORT).show()
            viewModel.saveSetting()
            navController.navigate("MainPage")
        } else {
            Toast.makeText(context, "Overlay permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    /*
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시멜로우(API 23)부터 적용
        if (!Settings.canDrawOverlays(context)) {

        }
    }*/
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:${context.packageName}")
    }
    //launcher.launch(intent)
    LaunchedEffect(permissionRequested) {
        if (permissionRequested) {
            if (false) {
                // 모든 권한이 이미 부여되었을 경우의 처리
                Log.d("aaaa","already_granted")
            } else {
                // 하나 이상의 권한이 부여되지 않았을 경우 권한 요청 로직
                launcher.launch(intent)
            }

            setPermissionRequested(false) // 상태를 다시 초기화
        }
    }




    Box(modifier = Modifier
        .offset(y = animatedValueList[11].offsetY)
        .alpha(animatedValueList[11].alpha)
        .height(60.dp)
        .fillMaxWidth()
        .clip(shape = RoundedCornerShape(4.dp))
        .background(color = MaterialTheme.colorScheme.background)
        .then(
            if (startPageAnimationState == 8) {
                Modifier.clickable {
                    setPermissionRequested(true)
                }
            } else Modifier
        ), contentAlignment = Alignment.Center
    ) {
        Text("권한 설정하기", color = Color.White)
    }
}

@Composable
fun RequestOverlayPermission() {

}
@Composable
fun getTargetValue(obId:Int,startPageAnimationState:Int,boxSize:IntSize):AnimationTarget{
    if(obId<0||startPageAnimationState<0) return AnimationTarget(0f,0.dp)

    val durationMillisList= listOf(0,1000,1000,500,0,1000,1000,500,1000)

    val targetLists= listOf(
        //제목
        listOf(
            AnimationTarget(0f,PxToDp(boxSize.height/2)-50.dp),
            AnimationTarget(1f,PxToDp(boxSize.height/2)-100.dp),
            AnimationTarget(1f,0.dp),
            AnimationTarget(1f,0.dp),
            AnimationTarget(1f,0.dp),
            AnimationTarget(0f,0.dp),
            ),

        //환영합니다
        listOf(
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(1f,PxToDp(boxSize.height/2)),
            AnimationTarget(1f,0.dp),
            AnimationTarget(1f,0.dp),
            AnimationTarget(1f,0.dp),
            AnimationTarget(0f,0.dp),
            ),

        //도와드릴게요!
        listOf(
            AnimationTarget(0f,PxToDp(boxSize.height)),
            AnimationTarget(0f,PxToDp(boxSize.height)),
            AnimationTarget(1f,0.dp),
            AnimationTarget(1f,0.dp),
            AnimationTarget(1f,0.dp),
            AnimationTarget(0f,0.dp),
            ),

        //버튼
        listOf(
            AnimationTarget(0f,0.dp),
            AnimationTarget(0f,0.dp),
            AnimationTarget(0f,0.dp),
            AnimationTarget(1f,0.dp),
            AnimationTarget(1f,0.dp),
            AnimationTarget(0f,0.dp),
        ),

        //설정 1번 설명
        listOf(
            AnimationTarget(0f,PxToDp(boxSize.height/2)-50.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)-50.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)-50.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)-50.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)-50.dp),
            AnimationTarget(1f,PxToDp(boxSize.height/2)-150.dp),
            AnimationTarget(0.3f,0.dp),
            AnimationTarget(0f,0.dp),
        ),

        //설정 1번 선택
        listOf(
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(1f,PxToDp(boxSize.height/2)-100.dp),
            AnimationTarget(0.3f,50.dp),
            AnimationTarget(0.0f,0.dp),
        ),

        //설정 2번 설명
        listOf(
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(1f,0.dp),
            AnimationTarget(0f,0.dp),
        ),

        //설정 2번 시?계
        listOf(
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+300.dp),
            AnimationTarget(1f,0.dp),
            AnimationTarget(0f,0.dp),
        ),

        //설정 2번 매일동일체크박스
        listOf(
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+510.dp),
            AnimationTarget(1f,0.dp),
            AnimationTarget(0f,0.dp),
        ),

        //설정 완료 버튼
        listOf(
            AnimationTarget(0f,0.dp),
            AnimationTarget(0f,0.dp),
            AnimationTarget(0f,0.dp),
            AnimationTarget(0f,0.dp),
            AnimationTarget(0f,0.dp),
            AnimationTarget(0f,0.dp),
            AnimationTarget(1f,0.dp),
            AnimationTarget(0f,0.dp),
        ),

        //권한 필요하다고 설명
        List(8){AnimationTarget(0f,0.dp)}+
                AnimationTarget(1f,0.dp),

        //권한 설정 시작 버튼
        List(8){AnimationTarget(0f,0.dp)}+
                AnimationTarget(1f,0.dp),
    )

    durationMillis=durationMillisList[startPageAnimationState]
    val listNum=if(obId in targetLists.indices) obId else 0
    val stateIndex=min(startPageAnimationState,targetLists[listNum].size-1)
    return targetLists[listNum][stateIndex]
}
@Composable
fun PxToDp(px:Int): Dp {
    val density = LocalDensity.current
    return  with(density) { px.toDp() }
}