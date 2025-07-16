package com.example.dailysummary.components

import android.util.Log
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun TimePicker(
    modifier: Modifier= Modifier,
    selectedHour: Int,
    selectedMinute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    changeToggle: Boolean=true,
    height:Dp
) {
    val strings = listOf("당일","다음 날")
    val hours = (0..23).toList()
    val minutes = (0..59).toList()

    val visibleItemsCount =5

    val selectedStringIndex = 1-selectedHour/12


    //val itemHeight = (parentHeight/visibleItemsCount).dp



    Box(modifier = modifier
        .clip(RoundedCornerShape(8.dp))
        .height(height),
        contentAlignment = Alignment.Center,
        ){
        Box(Modifier
            .fillMaxWidth()
            .fillMaxHeight(1f/visibleItemsCount)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary)
        )
        Row(modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            StringScrolled(
                modifier= Modifier.weight(1f).fillMaxHeight(),
                strings = strings,
                selectedIndex = selectedStringIndex,
                visibleItemsCount= visibleItemsCount,
                maxHeight=height)
            Text(text = ":", fontSize = 24.sp, color = MaterialTheme.colorScheme.onPrimary)
            NumberScroller(
                numbers = hours,
                selectedNumber = selectedHour,
                onNumberChange = onHourChange,
                modifier = Modifier.weight(1f).fillMaxHeight(),
                changeToggle =changeToggle,
                visibleItemsCount=visibleItemsCount,
                maxHeight=height
            )
            Text(text = ":", fontSize = 24.sp, color = MaterialTheme.colorScheme.onPrimary)
            NumberScroller(
                numbers = minutes,
                selectedNumber = selectedMinute,
                onNumberChange = onMinuteChange,
                modifier = Modifier.weight(1f).fillMaxHeight(),
                changeToggle =changeToggle,
                visibleItemsCount=visibleItemsCount,
                maxHeight=height
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
fun NumberScroller(
    numbers: List<Int>,
    selectedNumber: Int,
    onNumberChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    changeToggle:Boolean=true,
    visibleItemsCount:Int,
    maxHeight:Dp,
) {
    Log.d("aaaab",selectedNumber.toString())

    //val viewModel= hiltViewModel<MainPageViewModel>()
    //val currentMyTimeTab by viewModel.currentMyTimeTab.collectAsState()
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current




    val itemHeight = maxHeight/visibleItemsCount
    /*
    LaunchedEffect(selectedNumber) {
        coroutineScope.launch {
            val initialIndex = numbers.size * 50 + selectedNumber
            lazyListState.scrollToItem(initialIndex)
        }
    }*/
    LaunchedEffect(changeToggle){
        val initialIndex = numbers.size * 50 + selectedNumber -(visibleItemsCount/2)
        lazyListState.scrollToItem(initialIndex)
        Log.d("NumberScroller",selectedNumber.toString())
    }



    LazyColumn(
        state = lazyListState,
        modifier = modifier
            .fillMaxHeight()
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
            val itemInfo = lazyListState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.index == index }

            val centerOffset = (lazyListState.layoutInfo.viewportEndOffset + lazyListState.layoutInfo.viewportStartOffset) / 2
            val itemCenter = itemInfo?.let { it.offset + it.size / 2 } ?: 0
            val distance = abs(itemCenter - centerOffset).toFloat()

            // 최대 거리 기준값 설정 (화면에서 멀어지는 기준)
            val maxDistance = with(LocalDensity.current) { itemHeight.toPx() } * (visibleItemsCount / 2f)
            val norm = (distance / maxDistance).coerceIn(0f, 1f)

            // alpha, scale 계산 (가까울수록 1에 가까움)
            val alpha = 1f - norm
            val scale = 1f - norm * 0.5f  // 최소 스케일 0.7
            val cNorm = (distance / (maxDistance/(visibleItemsCount/2+1))).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .graphicsLayer {
                        this.alpha = alpha
                        this.scaleX = scale
                        this.scaleY = scale
                    },
                contentAlignment = Alignment.Center
            ) {
                val interpolatedColor = lerp(
                    MaterialTheme.colorScheme.onPrimary,
                    MaterialTheme.colorScheme.onBackground,
                    cNorm
                )

                Text(
                    text = String.format("%02d", number),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Light,
                    color = interpolatedColor
                )
            }
        }
    }

    var wasScrolling by remember { mutableStateOf(false) }

    val itemHeightPx = with(density) { itemHeight.toPx() }

    LaunchedEffect(Unit) {
        snapshotFlow { lazyListState.firstVisibleItemIndex to lazyListState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collect { (firstIndex, offset) ->
                // 실시간으로 중앙 아이템 계산
                val topIndex = (firstIndex + offset / itemHeightPx).roundToInt()
                val actualTopIndex = topIndex % numbers.size
                onNumberChange(numbers[(actualTopIndex + (visibleItemsCount / 2)) % numbers.size])
            }
    }

    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (wasScrolling && !lazyListState.isScrollInProgress) {
            val topIndex = (lazyListState.firstVisibleItemIndex +
                    lazyListState.firstVisibleItemScrollOffset / itemHeightPx).roundToInt()
            coroutineScope.launch {
                lazyListState.animateScrollToItem(topIndex)
            }
            Log.d("aaaa", "Snapped to $topIndex")
        }
        wasScrolling = lazyListState.isScrollInProgress
    }
}
@Composable
fun StringScrolled(
    strings: List<String>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    visibleItemsCount:Int,
    maxHeight: Dp
) {
    val dummyList = List(visibleItemsCount / 2) { "" }
    val dummyAddedList = dummyList + strings + dummyList

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val itemHeight = maxHeight / visibleItemsCount
    val density = LocalDensity.current

    LaunchedEffect(selectedIndex) {
        lazyListState.animateScrollToItem(selectedIndex)
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        userScrollEnabled = false
    ) {
        itemsIndexed(dummyAddedList) { index, str ->
            val itemInfo = lazyListState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.index == index }

            val centerOffset = (lazyListState.layoutInfo.viewportStartOffset +
                    lazyListState.layoutInfo.viewportEndOffset) / 2
            val itemCenter = itemInfo?.let { it.offset + it.size / 2 } ?: 0

            val distance = abs(itemCenter - centerOffset).toFloat()
            val maxDistance = with(density) { itemHeight.toPx() } * (visibleItemsCount / 2f)
            val norm = (distance / maxDistance).coerceIn(0f, 1f)

            val color = lerp(
                MaterialTheme.colorScheme.onPrimary,
                MaterialTheme.colorScheme.onBackground,
                norm
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = str,
                    fontSize = 16.sp,
                    color = color
                )
            }
        }
    }
}

