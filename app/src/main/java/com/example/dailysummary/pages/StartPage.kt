package com.example.dailysummary.pages

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dailysummary.dto.AnimationTarget
import com.example.dailysummary.viewModel.InitialSettingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.roundToInt


const val objectMaxIndex=8
public var durationMillis=0
@Composable
fun StartPage(){
    val viewModel = hiltViewModel<InitialSettingViewModel>()

    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    //var startPageAnimationState by remember { mutableIntStateOf(0) }
    val startPageAnimationState by viewModel.startPageAnimationState.collectAsState()
    //var adviceOrForcing by remember{ mutableStateOf(Pair(false,false)) }

    // 시작하자마자
    LaunchedEffect(Unit) {
        delay(300)

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

    /*
    //버튼누른 이후
    LaunchedEffect(startPageAnimationState) {
        //설정1 등장시작
        if(startPageAnimationState==5){
            delay(1000)
            //설정1 등장완료
            viewModel.setStartPageAnimationState(6)
        }
    }*/
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .onGloballyPositioned { layoutCoordinates ->
                // Box의 크기를 저장
                boxSize = layoutCoordinates.size
                //Log.d("offset", boxSize.toString())
            },
        contentAlignment = Alignment.TopCenter,)
    {
        if (startPageAnimationState in 0..5) Greeting(animatedValueList)

        if (startPageAnimationState in 3..5) SettingStartButton(animatedValueList)

        Setting1(animatedValueList)

        Setting2(animatedValueList)

        //GradientTransparencyExample()

        //SlideToSetNumber()

        //TimeSettingScreen()

        //SlideToSetTime()


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
fun Setting1(animatedValueList:List<AnimationTarget>){
    val viewModel= hiltViewModel<InitialSettingViewModel>()

    val startPageAnimationState by viewModel.startPageAnimationState.collectAsState()

    val adviceOrForcing by viewModel.adviceOrForcing.collectAsState()

    /*
        * 해야하는 설정이 뭐가있지
        * 1. 권유로받을지 강요로 받을지
        * 2. 몇시에 받을지 (매일 or 요일별 다름)
        * 3. 앱위에 표시 권한 받기
        * 일단 이정도?
        */
    Text(
        text = "권유 or 강요",
        fontSize = 20.sp,
        fontWeight = FontWeight.Light,
        color = Color.DarkGray,
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = animatedValueList[4].offsetY)
            .alpha(animatedValueList[4].alpha),
        textAlign = TextAlign.Left
    )
    Row (modifier = Modifier
        .fillMaxWidth()
        .height(80.dp)
        .offset(y = animatedValueList[5].offsetY)
        .alpha(animatedValueList[5].alpha)
    ){
        Box(modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .clip(shape = RoundedCornerShape(4.dp))
            .border(
                width = 2.dp,
                color = if (adviceOrForcing.first) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(4.dp),
            )
            .background(color = if (adviceOrForcing.first) MaterialTheme.colorScheme.primary else Color.White)
            .then(
                if (startPageAnimationState >= 5) {
                    Modifier.clickable {
                        viewModel.clickAdviceOrForcing(clickedIsLeft = true)
                        if (startPageAnimationState == 5) viewModel.setStartPageAnimationState(6)
                    }
                } else Modifier
            ),
            contentAlignment = Alignment.Center,
        ){
            Text(text = "권유")
        }
        Spacer(modifier = Modifier
            .fillMaxHeight()
            .width(16.dp))
        Box(modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .clip(shape = RoundedCornerShape(4.dp))
            .border(
                width = 2.dp,
                color = if (adviceOrForcing.second) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(4.dp),
            )
            .background(color = if (adviceOrForcing.second) MaterialTheme.colorScheme.primary else Color.White)
            .then(
                if (startPageAnimationState >= 5) {
                    Modifier.clickable {
                        viewModel.clickAdviceOrForcing(clickedIsLeft = false)
                        if (startPageAnimationState == 5) viewModel.setStartPageAnimationState(6)
                    }
                } else Modifier
            ),
            contentAlignment = Alignment.Center,
        ){
            Text(text = "강요")
        }

    }
}

@Composable
fun Setting2(animatedValueList: List<AnimationTarget>){
    val viewModel= hiltViewModel<InitialSettingViewModel>()

    val myTime by viewModel.myTime.collectAsState()
    val sameEveryDay by viewModel.sameEveryDay.collectAsState()

    //Log.d("myTime",myTime.toString())
    Text(
        text = "알림을 받을 시간을 설정해 주세요.\n" +
                "잠자기 30분 정도가 좋아요.",
        fontSize = 20.sp,
        fontWeight = FontWeight.Light,
        color = Color.DarkGray,
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = animatedValueList[6].offsetY)
            .alpha(animatedValueList[6].alpha),
        textAlign = TextAlign.Center
    )


    TimePicker(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .offset(y = animatedValueList[7].offsetY)
            .alpha(animatedValueList[7].alpha)
            .border(width = 1.dp, color = Color.Cyan),
        selectedHour = myTime.first,
        selectedMinute = myTime.second,
        onHourChange = { viewModel.setMyTime(hour = it) },
        onMinuteChange = { viewModel.setMyTime(minute = it) }
    )
    val interactionSource = remember { MutableInteractionSource() }
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .height(40.dp)
            .offset(y = animatedValueList[8].offsetY)
            .alpha(animatedValueList[8].alpha)
            .clickable (
                indication = null, // 파문 애니메이션을 제거
                interactionSource = interactionSource // 필요 시 상태를 관리
            ){
                viewModel.toggleSameEveryDay()
            }
    ) {
        Box(modifier = Modifier
            .size(20.dp)
            .border(width = 2.dp, color = Color.DarkGray)
            .then(
                if (sameEveryDay) Modifier.background(color = MaterialTheme.colorScheme.primary)
                else Modifier
            )
            , contentAlignment = Alignment.Center){
            if (sameEveryDay) Text(text = "V")
        }
        Spacer(modifier = Modifier
            .fillMaxHeight()
            .width(5.dp))
        Text(
            text = "매일 동일",
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = if(sameEveryDay) Color.DarkGray else Color.LightGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.height(IntrinsicSize.Min)
        )
    }
}



@Composable
fun TimePicker(
    modifier: Modifier= Modifier,
    selectedHour: Int,
    selectedMinute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    val hours = (0..23).toList()
    val minutes = (0..59).toList()

    Box(modifier = modifier){
        Row(modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            NumberScroller(
                numbers = hours,
                selectedNumber = selectedHour,
                onNumberChange = onHourChange,
                modifier = Modifier.weight(1f)
            )
            Text(text = ":", fontSize = 32.sp, modifier = Modifier.padding(horizontal = 8.dp))
            NumberScroller(
                numbers = minutes,
                selectedNumber = selectedMinute,
                onNumberChange = onMinuteChange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NumberScroller(
    numbers: List<Int>,
    selectedNumber: Int,
    onNumberChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val itemHeight = 50.dp
    val visibleItemsCount = 3
    /*
    LaunchedEffect(selectedNumber) {
        coroutineScope.launch {
            val initialIndex = numbers.size * 50 + selectedNumber
            lazyListState.scrollToItem(initialIndex)
        }
    }*/
    LaunchedEffect(Unit){
        val initialIndex = numbers.size * 50 + selectedNumber -1
        lazyListState.scrollToItem(initialIndex)
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier
            .height(itemHeight * visibleItemsCount)
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    coroutineScope.launch {
                        lazyListState.scrollBy(-dragAmount)
                    }
                    change.consume()
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(List(numbers.size * 100) { it % numbers.size }) { index, number ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    /*
                    .combinedClickable(
                        onClick = {
                            coroutineScope.launch {
                                onNumberChange(number)
                                lazyListState.animateScrollToItem(index)
                            }
                        }
                    ),*/,
                contentAlignment = Alignment.Center
            ) {
                Text(text = String.format("%02d", number), fontSize = 24.sp)
            }
        }
    }
    val density = LocalDensity.current
    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (!lazyListState.isScrollInProgress) {
            val centerIndex = (lazyListState.firstVisibleItemIndex + lazyListState.firstVisibleItemScrollOffset / with( density) { itemHeight.toPx() }).roundToInt()
            val actualIndex = centerIndex % numbers.size
            onNumberChange(numbers[((actualIndex+1)% numbers.size)])
            coroutineScope.launch {
                lazyListState.animateScrollToItem(centerIndex)
            }
            //Log.d("actualIndex",.toString())
        }

    }
}
@Composable
fun getTargetValue(obId:Int,startPageAnimationState:Int,boxSize:IntSize):AnimationTarget{
    if(obId<0||startPageAnimationState<0) return AnimationTarget(0f,0.dp)

    val durationMillisList= listOf(0,1000,1000,500,0,1000,1000)

    val targetLists= listOf(
        //제목
        listOf(
            AnimationTarget(0f,PxToDp(boxSize.height/2)-50.dp),
            AnimationTarget(1f,PxToDp(boxSize.height/2)-100.dp),
            AnimationTarget(1f,150.dp),
            AnimationTarget(1f,150.dp),
            AnimationTarget(1f,150.dp),
            AnimationTarget(0f,0.dp),
            ),

        //환영합니다
        listOf(
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(1f,PxToDp(boxSize.height/2)),
            AnimationTarget(1f,250.dp),
            AnimationTarget(1f,250.dp),
            AnimationTarget(1f,250.dp),
            AnimationTarget(0f,0.dp),
            ),

        //도와드릴게요!
        listOf(
            AnimationTarget(0f,PxToDp(boxSize.height)),
            AnimationTarget(0f,PxToDp(boxSize.height)),
            AnimationTarget(1f,PxToDp(boxSize.height/2)+50.dp),
            AnimationTarget(1f,PxToDp(boxSize.height/2)+50.dp),
            AnimationTarget(1f,PxToDp(boxSize.height/2)+50.dp),
            AnimationTarget(0f,0.dp),
            ),

        //버튼
        listOf(
            AnimationTarget(0f,PxToDp(boxSize.height)-60.dp),
            AnimationTarget(0f,PxToDp(boxSize.height)-60.dp),
            AnimationTarget(0f,PxToDp(boxSize.height)-60.dp),
            AnimationTarget(1f,PxToDp(boxSize.height)-60.dp),
            AnimationTarget(1f,PxToDp(boxSize.height)-60.dp),
            AnimationTarget(0f,PxToDp(boxSize.height)-60.dp),
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
        ),

        //설정 1번 선택
        listOf(
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(1f,PxToDp(boxSize.height/2)-100.dp),
            AnimationTarget(0.3f,50.dp)
        ),

        //설정 2번 설명
        listOf(
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(1f,150.dp)
        ),
        //설정 2번 시?계
        listOf(
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+300.dp),
            AnimationTarget(1f,240.dp)
        ),
        //설정 2번 매일동일체크박스
        listOf(
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+100.dp),
            AnimationTarget(0f,PxToDp(boxSize.height/2)+510.dp),
            AnimationTarget(1f,450.dp)
        ),
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