package com.example.dailysummary.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.dailysummary.data.YearMonth
import com.example.dailysummary.model.CalenderOnePage
import com.example.dailysummary.viewModel.MainPageViewModel
import java.time.LocalDate

val weekDayList= listOf(
    Pair(Color.Red,"S"),
    Pair(null,"M"),
    Pair(null,"T"),
    Pair(null,"W"),
    Pair(null,"T"),
    Pair(null,"F"),
    Pair(Color.Blue,"S"),
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DSCalender(
    modifier: Modifier=Modifier,
    navController: NavController,
){
    val viewModel = hiltViewModel<MainPageViewModel>()

    val lazyPagingItems = viewModel.pager.collectAsLazyPagingItems()







    val pagerState = rememberPagerState (
        pageCount = {lazyPagingItems.itemCount},
    )

    LaunchedEffect(pagerState.currentPage){
        if(lazyPagingItems.loadState.refresh is LoadState.NotLoading){
            viewModel.setCurrentYMPage(lazyPagingItems[pagerState.currentPage]!!.yearMonth)
        }
    }

    val currentYMPage by viewModel.currentYMPage.collectAsState()
    val isCurrentYear = currentYMPage.year==LocalDate.now().year



    Box(){
        Column(modifier = modifier) {
            CalenderMonth(
                modifier = Modifier.align(Alignment.Start),
                isCurrentYear=isCurrentYear,
                year = currentYMPage.year,
                month = currentYMPage.month
            )
            Row {
                weekDayList.forEach { day ->
                    CalenderDate(modifier= Modifier.weight(1f),day.first,day.second)
                }
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .height(300.dp),
                verticalAlignment = Alignment.Top,
                key = lazyPagingItems.itemKey { it.yearMonth.toPageNum() },
                beyondViewportPageCount = 2,
                pageSpacing = 8.dp

            ) {page ->
                val calenderOnePage = if(lazyPagingItems.loadState.refresh is LoadState.NotLoading) lazyPagingItems[page]!! else null

                CalenderDayGrid(
                    calenderOnePage = calenderOnePage,
                    setClickedBoxIndex = {},
                    setPopupPosition = {},
                    setShowPopup = {},
                    onNav = { year, month, day ->
                        navController.navigate("SummaryPage/${year}/${month}/${day}")
                    },
                )
            }

            /*
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .height(300.dp)
                    .border(width = 1.dp, color = Color.Cyan),
                flingBehavior = customFlingBehavior,
                verticalAlignment = Alignment.Top) { page->
                val pageYm= pageNumToYearAndMonth(page)
                val summaries = viewModel.getSummaries(pageYm.first,pageYm.second)
                val calenderEntries = viewModel.getCalenderEntries(pageYm.first,pageYm.second,summaries)

                CalenderDayGrid(
                    calenderEntries = calenderEntries,
                    showPopup = showPopup,
                    setClickedBoxIndex = {viewModel.setClickedBoxIndex(it)},
                    setPopupPosition = {viewModel.setPopupPosition(it)},
                    setShowPopup = {viewModel.setShowPopup(it)},
                    year = selectedYear,
                    month = selectedMonth,
                    onNav = { year, month, day ->
                        navController.navigate("SummaryPage/${year}/${month}/${day}")
                    },
                )
            }*/

        }
        /*
        if (showPopup) {
            Popup(
                onDismissRequest = { viewModel.setShowPopup(false) },
                popupPositionProvider = object : PopupPositionProvider {
                    override fun calculatePosition(anchorBounds: IntRect, windowSize: IntSize, layoutDirection: LayoutDirection, popupContentSize: IntSize): IntOffset {
                        return IntOffset(
                            x = popupPosition.x,
                            y = popupPosition.y - popupContentSize.height - 16  // 버튼 위에 위치
                        )
                    }
                }
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.Black, shape = RoundedCornerShape(10.dp))
                        .padding(12.dp)
                        .clickable {
                            navController.navigate("SummaryPage/${selectedYearAndMonth.first}/${selectedYearAndMonth.second}/${calenderEntries[clickedBoxIndex].day}")
                        }
                ) {
                    Text(viewModel.readSummary(calenderEntries[clickedBoxIndex].summaryIndex).title, color = Color.White)
                }
            }
        }*/
    }
}


fun yearAndMonthToPageNum(year:Int,month:Int):Int{
    return (year-1970)*12 + (month -1) //1970년 1월이 0번
}

fun pageNumToYearAndMonth(pageNum:Int):Pair<Int,Int>{
    return Pair(1970+pageNum/12,pageNum%12+1)
}

@Composable
fun CalenderMonth(
    modifier: Modifier = Modifier,
    isCurrentYear: Boolean,
    year:Int,
    month: Int
){
    Column(
        modifier = modifier.size(70.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        if(!isCurrentYear) Text(text = year.toString())
        Text(text = month.toString(), fontSize =36.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview
@Composable
fun SuperSmoothCalendarPager() {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = {1200})

    // 부드러운 스크롤을 위한 Custom FlingBehavior
    val customFlingBehavior = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(1), // 최대 1개의 페이지 이동
        decayAnimationSpec = splineBasedDecay(LocalDensity.current), // 자연스러운 감속
        snapAnimationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, // 부드럽고 살짝 튕기는 효과
            stiffness = Spring.StiffnessLow // 느슨한 감속 (부드러움 강화)
        ),
        snapPositionalThreshold = 0.3f // 페이지의 30%만 넘겨도 다음 페이지로
    )

    HorizontalPager(
        state = pagerState,
        flingBehavior = customFlingBehavior,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val year = 1925 + page / 12
        val month = (page % 12) + 1

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$year 년 $month 월",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}




@Composable
fun CalenderDate(
    modifier: Modifier=Modifier,
    color:Color?=null,
    text:String,
){
    val resolvedColor = color ?: MaterialTheme.colorScheme.primary
    Text(text = text, modifier = modifier,color=resolvedColor, textAlign = TextAlign.Center)
}

@Composable
fun CalenderBox(
    modifier: Modifier=Modifier,
    isBlank:Boolean = false,
    isNotBlank: Boolean = !isBlank,
    isWritten:Boolean = false,
    isNotWritten:Boolean = !isWritten,
    day:Int=0,
    onClick: (IntOffset) -> Unit,
){

    var buttonPosition by remember { mutableStateOf(IntOffset(0, 0)) }

    Box(modifier = modifier
        .aspectRatio(1f)
        .clip(shape = RoundedCornerShape(12.dp))
        .then(
            if (isBlank) Modifier.background(color = Color.Transparent)
            else {
                Modifier
                    .clickable { onClick(buttonPosition) }
                    .background(
                        color =
                        if (isWritten)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.primaryContainer
                    )
                    .onGloballyPositioned { coordinates ->
                        buttonPosition = coordinates
                            .positionInWindow()
                            .round()
                    }

            }
        ),
        contentAlignment = Alignment.Center

    ){
        if(isNotBlank)
            Text(
                text = "$day",
                color = if(isWritten) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.primary
            )
    }
}

@Composable
fun AdjustMonthButton(
    isPrev:Boolean=true,
    modifier: Modifier = Modifier,
    onClick : () ->Unit,
    ){

    IconButton(
        modifier = modifier
            .fillMaxHeight(0.5f)
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.primary),
        onClick = onClick) {
        Icon(
            imageVector =
            if(isPrev) Icons.Outlined.KeyboardArrowLeft
            else Icons.Outlined.KeyboardArrowRight,
            contentDescription = if(isPrev) "Prev" else "Next",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalenderDayGrid(
    calenderOnePage:CalenderOnePage?,
    showPopup:Boolean = false,
    setClickedBoxIndex:(Int)->Unit,
    setPopupPosition:(IntOffset)->Unit,
    setShowPopup:(Boolean)->Unit,
    onNav:(Int,Int,Int)->Unit,
){
    if(calenderOnePage == null) return

    val year = calenderOnePage.year
    val month = calenderOnePage.month

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(0.dp)
    ){


        itemsIndexed(calenderOnePage.calenderEntries){ index, calenderEntry->
            CalenderBox(
                isBlank = calenderEntry.isBlank,
                isWritten = calenderEntry.isWritten,
                day = calenderEntry.day,
            ){intOffset->
                if(calenderEntry.isWritten){
                    //Toast.makeText(context,viewModel.readSummary(it.summaryIndex).content,Toast.LENGTH_SHORT).show()
                    if(!showPopup){
                        setClickedBoxIndex(index)
                        setPopupPosition(intOffset)
                        setShowPopup(true)

                    }
                }
                else{
                    val targetDate = LocalDate.of(year,month,calenderEntry.day)

                    if(targetDate.isAfter(LocalDate.now())){

                    }
                    else {
                        onNav(year,month,calenderEntry.day)
                    }
                }

            }
        }
    }
}
