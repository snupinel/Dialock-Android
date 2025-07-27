package com.example.dailysummary.pages.mainPageTabs

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.dailysummary.dto.PageYearMonth
import com.example.dailysummary.dto.Summary
import com.example.dailysummary.dto.CalenderEntry
import com.example.dailysummary.dto.CalenderOnePage
import com.example.dailysummary.viewModel.MainPageViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
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
    val scope = rememberCoroutineScope()
    var showPicker by remember { mutableStateOf(false) }


    Column(Modifier.fillMaxSize()){
        CalenderMonth(
            modifier = Modifier
                .padding(horizontal =20.dp),
            isCurrentYear = isCurrentYear,
            year = currentPageYM.year,
            month = currentPageYM.month,
        ){
            showPicker = true
        }
        if (showPicker) {
            YearMonthPickerDialog(
                initialYear = currentPageYM.year,
                initialMonth = currentPageYM.month,
                onConfirm = { y, m ->
                    showPicker = false
                    val newPage = PageYearMonth(y, m).toPageNum()
                    scope.launch {
                        pagerState.scrollToPage(newPage)
                    }
                },
                onDismiss = { showPicker = false }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
            weekDayList.forEach { d ->
                CalenderDate(modifier = Modifier.weight(1f), d.first, d.second)
            }
        }
        var pagerHeight by remember { mutableStateOf(1000.dp) }
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(pagerHeight),
            verticalAlignment = Alignment.Top,
            beyondViewportPageCount = 0,
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
        Spacer(modifier = Modifier.height(12.dp))
        Divider(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 4.dp)
                .background(MaterialTheme.colorScheme.surface),)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = clickedDay.format(
                    DateTimeFormatter.ofPattern(
                        "yyyy.MM.dd (E)",
                        Locale.KOREA
                    )
                ),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Light,
            )
            IconButton(onClick = {
                navController.navigate("WriteDiaryPage/${clickedDay.year}/${clickedDay.monthValue}/${clickedDay.dayOfMonth}")
            }) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription ="Add")
            }
        }
        Divider(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 4.dp)
                .background(MaterialTheme.colorScheme.surface),)
        if(clickedEntry!=null){
            if(clickedEntry!!.summaries.isEmpty()){
                Box(Modifier.fillMaxSize(),contentAlignment = Alignment.Center){
                    Text(text = "작성된 일기가 없어요.",color = Color.Gray)
                }
            }
            else{
                LazyColumn(modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ){

                    items(clickedEntry!!.summaries){
                        DiaryPreviewCard(
                            summary = it
                        ){
                            navController.navigate("DiaryPage/${it.id}")
                        }
                    }
                }
            }

        }
    }
}



@Composable
fun CalenderMonth(
    modifier: Modifier = Modifier,
    isCurrentYear: Boolean,
    year:Int,
    month: Int,
    onClick: () -> Unit,
){
    Row(
        modifier = modifier.clickable {
            onClick()
        },
        verticalAlignment = Alignment.CenterVertically,
    ){
        Text(text = month.toString(), fontSize =48.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.width(8.dp))
        if(!isCurrentYear) Text(text = year.toString(), fontWeight = FontWeight.Light)
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
    calenderOnePage: CalenderOnePage,
    clickedDay: LocalDate? = null,
    onDayClick: (date: LocalDate, c: CalenderEntry) -> Unit,
) {
    Log.d("recompose", "CalenderDayGrid recomposed with page:")

    val year = calenderOnePage.year
    val month = calenderOnePage.month
    val entries = calenderOnePage.calenderEntries

    // ✅ 7일씩 끊어서 6줄(Row)로 나누기
    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        entries.chunked(7).forEach { weekEntries ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                weekEntries.forEach { entry ->
                    if (entry.isBlank) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .alpha(0.15f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${entry.date.dayOfMonth}",
                                color = Color.Gray
                            )
                        }
                    } else {
                        CalenderBox(
                            modifier = Modifier.weight(1f),
                            isWritten = entry.isWritten,
                            isClicked = clickedDay != null &&
                                    clickedDay.isEqual(LocalDate.of(year, month, entry.date.dayOfMonth)),
                            isToday = entry.isToday,
                            isFuture = entry.isFuture,
                            day = entry.date.dayOfMonth
                        ) {
                            val targetDate = LocalDate.of(year, month, entry.date.dayOfMonth)
                            if (!targetDate.isAfter(LocalDate.now())) {
                                onDayClick(entry.date, entry)
                            }
                        }
                    }
                }

                // ✅ 마지막 줄이 7칸 미만일 경우 빈 공간 채우기
                repeat(7 - weekEntries.size) {
                    Spacer(modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f))
                }
            }
        }
    }
}

@Composable
fun DiaryPreviewCard(
    modifier: Modifier = Modifier,
    summary: Summary,
    showDate:Boolean = false,
    onClickDetail: () -> Unit,

) {
    val timeText=
        if (showDate) formatRelativeDateTime(summary.date)
        else formatWrittenTime(summary.writtenTime, summary.date)

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth(),
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
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ){
            Text(
                text = timeText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = summary.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if(summary.content!=""){
                Text(
                    text = summary.content,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
fun formatWrittenTime(writtenTime: LocalDateTime, date: LocalDate): String {
    val timePart = writtenTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    val writtenDate = writtenTime.toLocalDate()

    return when {
        // 1. 같은 날짜
        writtenDate == date -> {
            timePart
        }
        // 2. date의 다음 날
        writtenDate == date.plusDays(1) -> {
            "다음날 $timePart"
        }
        // 3. 같은 년도
        writtenDate.year == date.year -> {
            writtenTime.format(DateTimeFormatter.ofPattern("MM.dd HH:mm:ss"))
        }
        // 4. 그 외 (년도도 다름)
        else -> {
            writtenTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"))
        }
    }
}
fun formatRelativeDateTime(date: LocalDate): String {
    val today = LocalDate.now()


    return when {
        // 1. 같은 날짜
        today == date -> {
            "오늘"
        }
        today.year == date.year -> {
            date.format(DateTimeFormatter.ofPattern("M월 d일"))
        }
        else -> {
            date.format(DateTimeFormatter.ofPattern("y년 M월 d일"))
        }
    }
}

@Composable
fun YearMonthPickerDialog(
    initialYear: Int,
    initialMonth: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedYear by remember { mutableStateOf(initialYear) }
    var selectedMonth by remember { mutableStateOf(initialMonth) }

    // ✅ AlertDialog가 뜬 후에만 Dropdown 초기화
    var isDialogReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isDialogReady = true
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedYear, selectedMonth) }) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("취소")
            }
        },
        title = { Text("연/월 선택") },
        text = {
            if (isDialogReady) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    YearDropdown(selectedYear) { selectedYear = it }
                    MonthSelector(selectedMonth) { selectedMonth = it }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YearDropdown(selectedYear: Int, onYearSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedYear.toString(),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.menuAnchor(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val currentYear = LocalDate.now().year
            (2000..currentYear).reversed().forEach { year ->
                DropdownMenuItem(
                    text = { Text(year.toString()) },
                    onClick = {
                        onYearSelected(year)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun MonthSelector(selectedMonth: Int, onMonthSelected: (Int) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(12) { m ->
            val monthValue = m + 1
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (monthValue == selectedMonth)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { onMonthSelected(monthValue) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    "${monthValue}월",
                    color = if (monthValue == selectedMonth)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

