package com.example.dailysummary.pages.mainPageTabs

import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dailysummary.dto.PageYearMonth
import com.example.dailysummary.dto.Summary
import com.example.dailysummary.model.CalenderEntry
import com.example.dailysummary.model.CalenderOnePage
import com.example.dailysummary.viewModel.MainPageViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

val weekDayList= listOf(
    Pair(Color.Red,"S"),
    Pair(null,"M"),
    Pair(null,"T"),
    Pair(null,"W"),
    Pair(null,"T"),
    Pair(null,"F"),
    Pair(Color.Blue,"S"),
)

@Composable
fun CalenderTab(
    navController: NavController,
) {
    Log.d("recompose", "DSCalender recomposition")

    val backStackEntry = navController.currentBackStackEntryAsState().value
    val viewModel = hiltViewModel<MainPageViewModel>()
    //val lazyPagingItems = viewModel.pager.collectAsLazyPagingItems()
    //val hasData = lazyPagingItems.itemCount > 0

    val pagerState = rememberPagerState(
        initialPage = PageYearMonth().toPageNum(),
        pageCount = { 1200 }
    )

    val density = LocalDensity.current


    val cache = viewModel.pageCache


    val currentPageYM = remember(pagerState.currentPage) {
        PageYearMonth(pagerState.currentPage)
    }
    val isCurrentYear = currentPageYM.year == LocalDate.now().year

    val customFlingBehavior = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(1),
        decayAnimationSpec = splineBasedDecay(LocalDensity.current),
        snapAnimationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        snapPositionalThreshold = 0.5f // 50% 이상 넘기면 다음 페이지로 이동
    )

    val clickedDay by viewModel.clickedDay.collectAsState()
    val clickedEntry by viewModel.clickedEntry.collectAsState()

    CalenderMonth(
        modifier = Modifier
            .padding(horizontal = 12.dp),
        isCurrentYear = isCurrentYear,
        year = currentPageYM.year,
        month = currentPageYM.month
    )
    Row(modifier = Modifier.padding(horizontal = 8.dp)) {
        weekDayList.forEach { d ->
            CalenderDate(modifier = Modifier.weight(1f), d.first, d.second)
        }
    }
    var pagerHeight by remember { mutableStateOf(1000.dp) }
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(pagerHeight),
        verticalAlignment = Alignment.Top,
        beyondViewportPageCount = 1,
        pageSpacing = 40.dp,
        flingBehavior = customFlingBehavior
    ) { page ->
        val pageData = cache[page]

        // 없으면 로딩 요청
        LaunchedEffect(pageData) {
            if (pageData == null) {
                viewModel.loadPageIfAbsent(page)
            }
        }


        Box(
            modifier = Modifier.onGloballyPositioned { coordinates ->
                val heightPx = coordinates.size.height
                pagerHeight = with(density) { heightPx.toDp() }
            }
        ) {
            val pageContent: @Composable () -> Unit = remember(pageData) {
                {
                    CalenderDayGrid(
                        calenderOnePage = pageData ?: CalenderOnePage.dummy(page),
                        clickedDay = clickedDay,
                        onDayClick = { d, c ->
                            viewModel.clickDay(
                                d,
                                c
                            )
                        }
                    )
                }
            }
            pageContent()
        }

    }
    Row {
        Text(
            text = clickedDay.format(
                DateTimeFormatter.ofPattern(
                    "yyyy.MM.dd (E)",
                    Locale.KOREA
                )
            ),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        IconButton(onClick = {
            navController.navigate("WriteDiaryPage/${clickedDay.year}/${clickedDay.monthValue}/${clickedDay.dayOfMonth}")
        }) {
            Icon(imageVector = Icons.Outlined.Add, contentDescription ="Add")
        }
    }

    if(clickedEntry!=null){
        if(clickedEntry!!.summaries.isEmpty()){
            Text(text = "작성된 일기가 없어요.")
        }
        else{
            clickedEntry!!.summaries.forEach {
                DiaryPreviewCard(
                    summary = it
                ){
                    navController.navigate("DiaryPage/${it.id}")
                }
            }
        }

    }
    else{

    }
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



@Composable
fun CalenderDate(
    modifier: Modifier=Modifier,
    color:Color?=null,
    text:String,
){
    val resolvedColor = color ?: Color.Gray
    Text(text = text, modifier = modifier,color=resolvedColor, textAlign = TextAlign.Center)
}

@Composable
fun CalenderBox(
    modifier: Modifier=Modifier,
    isWritten:Boolean = false,
    isClicked:Boolean = false,
    isToday:Boolean = false,
    isFuture:Boolean = false,
    day:Int=0,
    onClick: () -> Unit,
){


    Box(modifier = modifier
        .aspectRatio(1f)
        .clip(shape = CircleShape)
        .clickable { onClick() }
        .then(
            if (isClicked)
                Modifier.border(
                    3.dp,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            else Modifier
        )
        .then(
            if (isToday)
                Modifier
                    .padding(3.dp)
                    .clip(shape = CircleShape)
                    .background(color = MaterialTheme.colorScheme.primary)
            else Modifier
        ),
        contentAlignment = Alignment.Center
    ){
        if (isWritten) {
            Box(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .height(2.dp)
                    .width(16.dp)
                    .background(
                        shape = RoundedCornerShape(1.dp),
                        color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    )
            )
        }
        Text(
            text = "$day",
            color = if(isFuture) Color.Gray
            else if(isToday) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun CalenderDayGrid(
    calenderOnePage:CalenderOnePage,
    clickedDay:LocalDate?=null,
    onDayClick: (date: LocalDate,c:CalenderEntry) -> Unit,
){
    Log.d("recompose", "CalenderDayGrid recomposed with page:")


    val year = calenderOnePage.year
    val month = calenderOnePage.month

    LazyVerticalGrid(
        modifier = Modifier.padding(horizontal = 12.dp),
        columns = GridCells.Fixed(7),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(0.dp),
        userScrollEnabled = false,
    ){


        itemsIndexed(calenderOnePage.calenderEntries, key = {index, item -> "$index-${item.date.dayOfMonth}"}){ index, calenderEntry->
            if(calenderEntry.isBlank){
                Box(modifier = Modifier
                    .aspectRatio(1f)
                    .alpha(0.15f),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "${calenderEntry.date.dayOfMonth}",
                        color = Color.Gray
                    )
                }
            } else{
                CalenderBox(
                    isWritten = calenderEntry.isWritten,
                    isClicked = clickedDay!=null && clickedDay.isEqual(LocalDate.of(year,month,calenderEntry.date.dayOfMonth)),
                    isToday = calenderEntry.isToday,
                    isFuture = calenderEntry.isFuture,
                    day = calenderEntry.date.dayOfMonth,
                ){
                    val targetDate = LocalDate.of(year,month,calenderEntry.date.dayOfMonth)

                    if(targetDate.isAfter(LocalDate.now())){

                    }
                    else {
                        onDayClick(calenderEntry.date,calenderEntry)
                    }

                }
            }

        }
    }
}
@Composable
fun DiaryPreviewCard(
    modifier: Modifier = Modifier,
    summary: Summary,
    onClickDetail: () -> Unit,

) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .clickable {
                onClickDetail()
            }
            .padding(16.dp)
        ){
            Text(
                text = summary.writtenTime.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = summary.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = summary.content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
