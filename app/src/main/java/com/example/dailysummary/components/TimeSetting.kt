package com.example.dailysummary.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dailysummary.dto.AnimationTarget
import com.example.dailysummary.viewModel.InitialSettingViewModel
import com.example.dailysummary.viewModel.MainPageViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun TimeSetting(
    modifier: Modifier=Modifier,
    animatedValues: List<AnimationTarget> = List(3) { AnimationTarget(1f, 0.dp) },
    title: String,
    selectedHour: Int,
    selectedMinute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    sameEveryDay: Boolean,
    onToggleSameEveryDay: () -> Unit,
    currentMyTimeTab: Int,
    isNextDay:Boolean,
    onToggleIsNextDay: () -> Unit,
    onDayTabClick: (Int) -> Unit,
    changeToggle: Boolean=true,
) {
    Column(modifier = modifier){
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Light,
            color = Color.DarkGray,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = animatedValues[0].offsetY)
                .alpha(animatedValues[0].alpha),
            textAlign = TextAlign.Center
        )
        TimePicker(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .offset(y = animatedValues[1].offsetY)
                .alpha(animatedValues[1].alpha)
                .border(width = 1.dp, color = Color.Cyan),
            selectedHour = selectedHour,
            selectedMinute = selectedMinute,
            onHourChange = onHourChange,
            onMinuteChange = onMinuteChange,
            currentMyTimeTab = currentMyTimeTab,
            changeToggle = changeToggle
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = animatedValues[2].offsetY)
                .alpha(animatedValues[2].alpha),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IsNextDayToggle(
                isNextDay = isNextDay,
                onToggle = onToggleIsNextDay
            )
            AnimatedVisibility(
                visible = !sameEveryDay,
                enter = fadeIn(animationSpec = tween(500)) + expandVertically(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(500)) + shrinkVertically(animationSpec = tween(500))
            ) {
                DayTabs(
                    modifier = Modifier.height(50.dp),
                    currentTab = currentMyTimeTab,
                    onDayTabClick = onDayTabClick
                )
            }

            SameEveryDayToggle(
                sameEveryDay = sameEveryDay,
                onToggle = onToggleSameEveryDay
            )
        }
    }
}



@Composable
fun DayTabs(
    modifier: Modifier=Modifier,
    currentTab: Int,
    onDayTabClick: (Int) -> Unit
) {
    Box(modifier = modifier){
        Row(modifier = Modifier.fillMaxSize()) {
            (0..6).forEach { index ->
                DayTab(modifier = Modifier.weight(1f), day = index, isSelected = currentTab == index, onClick = { onDayTabClick(index) })
            }
        }
    }

}

@Composable
fun DayTab(
    day:Int,
    modifier: Modifier=Modifier,
    isSelected:Boolean,
    onClick: (Int) -> Unit,
    ){
    val week= listOf("일","월","화","수","목","금","토")
    //val viewModel= hiltViewModel<InitialSettingViewModel>()
    //val currentMyTimeTab by viewModel.currentMyTimeTab.collectAsState()
    Box(modifier = modifier
        .fillMaxHeight()
        .clickable {
            onClick(day)
        }, contentAlignment = Alignment.Center){
        Text(text = week[day])
        if(isSelected) Divider(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(3.dp), color = MaterialTheme.colorScheme.primary)

    }
}

@Composable
fun TimePicker(
    modifier: Modifier= Modifier,
    selectedHour: Int,
    selectedMinute: Int,
    currentMyTimeTab: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    changeToggle: Boolean=true,
) {
    val hours = (0..23).toList()
    val minutes = (0..59).toList()


    Box(modifier = modifier,
        contentAlignment = Alignment.Center){
        Row(modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            NumberScroller(
                numbers = hours,
                selectedNumber = selectedHour,
                onNumberChange = onHourChange,
                modifier = Modifier.weight(1f),
                currentMyTimeTab = currentMyTimeTab,
                changeToggle =changeToggle
            )
            Text(text = ":", fontSize = 32.sp, modifier = Modifier.padding(horizontal = 8.dp))
            NumberScroller(
                numbers = minutes,
                selectedNumber = selectedMinute,
                onNumberChange = onMinuteChange,
                modifier = Modifier.weight(1f),
                currentMyTimeTab = currentMyTimeTab,
                changeToggle =changeToggle

                )
        }
    }
}

@Composable
fun SameEveryDayToggle(
    sameEveryDay: Boolean,
    onToggle: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .height(40.dp)
            .clickable(
                indication = null,
                interactionSource = interactionSource,
                onClick = onToggle
            )
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .border(width = 2.dp, color = Color.DarkGray)
                .then(
                    if (sameEveryDay) Modifier.background(color = MaterialTheme.colorScheme.primary)
                    else Modifier
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            if (sameEveryDay) Text(text = "V")
        }
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "매일 동일",
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = if (sameEveryDay) Color.DarkGray else Color.LightGray,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .height(IntrinsicSize.Min)
        )
    }
}
@Composable
fun IsNextDayToggle(
    isNextDay: Boolean,
    onToggle: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .height(40.dp)
            .clickable(
                indication = null,
                interactionSource = interactionSource,
                onClick = onToggle
            )
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .border(width = 2.dp, color = Color.DarkGray)
                .then(
                    if (isNextDay) Modifier.background(color = MaterialTheme.colorScheme.primary)
                    else Modifier
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            if (isNextDay) Text(text = "V")
        }
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "다음 날",
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = if (isNextDay) Color.DarkGray else Color.LightGray,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .height(IntrinsicSize.Min)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NumberScroller(
    numbers: List<Int>,
    selectedNumber: Int,
    onNumberChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    currentMyTimeTab:Int,
    changeToggle:Boolean=true,
) {
    Log.d("aaaab",selectedNumber.toString())

    //val viewModel= hiltViewModel<MainPageViewModel>()
    //val currentMyTimeTab by viewModel.currentMyTimeTab.collectAsState()
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
    LaunchedEffect(key1=currentMyTimeTab,key2=changeToggle){
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
    LaunchedEffect(Unit) {
        snapshotFlow { lazyListState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { isScrolling -> !isScrolling }
            .collect {
                // 스크롤이 멈춘 순간에 실행할 작업
                println("Scrolling stopped")
            }
    }
    var wasScrolling by remember { mutableStateOf(false) }

    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (wasScrolling && !lazyListState.isScrollInProgress) {
            val centerIndex = (lazyListState.firstVisibleItemIndex + lazyListState.firstVisibleItemScrollOffset / with(density) { itemHeight.toPx() }).roundToInt()
            val actualIndex = centerIndex % numbers.size
            onNumberChange(numbers[(actualIndex + 1) % numbers.size])
            coroutineScope.launch {
                lazyListState.animateScrollToItem(centerIndex)
            }
            Log.d("aaaa", "!lazyListState.isScrollInProgress called")
        }
        wasScrolling = lazyListState.isScrollInProgress
    }
}