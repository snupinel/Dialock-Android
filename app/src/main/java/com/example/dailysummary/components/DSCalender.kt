package com.example.dailysummary.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.snapshotFlow
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.dailysummary.dto.PageYearMonth
import com.example.dailysummary.model.CalenderOnePage
import com.example.dailysummary.viewModel.MainPageViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
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
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    Log.d("recompose", "DSCalender recomposition")

    val backStackEntry = navController.currentBackStackEntryAsState().value
    val shouldRefresh = backStackEntry?.savedStateHandle?.get<Boolean>("shouldRefresh")?:false //글을 작성하거나 수정했을 때, 새로고침이 필요함을 전달
    val viewModel = hiltViewModel<MainPageViewModel>()
    //val lazyPagingItems = viewModel.pager.collectAsLazyPagingItems()
    //val hasData = lazyPagingItems.itemCount > 0

    val pagerState = rememberPagerState(
        initialPage = PageYearMonth().toPageNum(),
        pageCount = { 1200 }
    )



    val cache = viewModel.pageCache


    LaunchedEffect(shouldRefresh){
        //viewModel.setShowPopup(false)
        //viewModel.calenderRefresh()
        //viewModel.setCalenderEntries()
        if(shouldRefresh){
            val targetPage = pagerState.currentPage
            //lazyPagingItems.refresh()
            /*
            snapshotFlow { lazyPagingItems.loadState.refresh }
                .distinctUntilChanged()
                .collect { state ->
                    if (state is LoadState.NotLoading) {
                        // 🪄 새로고침 완료되었으므로 원래 페이지로 복귀
                        pagerState.scrollToPage(targetPage)
                        cancel() // snapshotFlow collect 빠져나오기
                    }
                }
            */
            backStackEntry?.savedStateHandle?.set("shouldRefresh", false)
        }
    }
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


    Column(modifier = modifier) {

        CalenderMonth(
            modifier = Modifier.align(Alignment.Start).padding(horizontal = 12.dp),
            isCurrentYear = isCurrentYear,
            year = currentPageYM.year,
            month = currentPageYM.month
        )
        Row(modifier = Modifier.padding(horizontal = 12.dp)) {
            weekDayList.forEach { d ->
                CalenderDate(modifier = Modifier.weight(1f), d.first, d.second)
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.height(300.dp),
            verticalAlignment = Alignment.Top,
            beyondViewportPageCount = 4,
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


            val pageContent:@Composable ()->Unit = remember(pageData) {
                {
                    CalenderDayGrid(
                        calenderOnePage = pageData ?: CalenderOnePage.dummy(page),
                        onNav = { y, m, d ->
                            navController.navigate("SummaryPage/$y/$m/$d")
                        }
                    )
                }
            }
            pageContent()

        }


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
    val resolvedColor = color ?: MaterialTheme.colorScheme.primary
    Text(text = text, modifier = modifier,color=resolvedColor, textAlign = TextAlign.Center)
}

@Composable
fun CalenderBox(
    modifier: Modifier=Modifier,
    isWritten:Boolean = false,
    day:Int=0,
    onClick: () -> Unit,
){


    Box(modifier = modifier
        .aspectRatio(1f)
        .clip(shape = RoundedCornerShape(12.dp))
        .clickable { onClick() }
        .background(
            color =
            if (isWritten)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.primaryContainer
        ),
        contentAlignment = Alignment.Center

    ){
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
    calenderOnePage:CalenderOnePage,
    showPopup:Boolean = false,
    onNav:(Int,Int,Int)->Unit,
){
    Log.d("recompose", "CalenderDayGrid recomposed with page:")


    val year = calenderOnePage.year
    val month = calenderOnePage.month

    LazyVerticalGrid(
        modifier = Modifier.height(300.dp).padding(horizontal = 12.dp),
        columns = GridCells.Fixed(7),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(0.dp),
        userScrollEnabled = false,
    ){


        itemsIndexed(calenderOnePage.calenderEntries, key = {index, item -> "$index-${item.day}"}){ index, calenderEntry->
            if(calenderEntry.isBlank){

            } else{
                CalenderBox(
                    isWritten = calenderEntry.isWritten,
                    day = calenderEntry.day,
                ){
                    if(calenderEntry.isWritten){
                        //Toast.makeText(context,viewModel.readSummary(it.summaryIndex).content,Toast.LENGTH_SHORT).show()
                        onNav(year,month,calenderEntry.day)
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
}
