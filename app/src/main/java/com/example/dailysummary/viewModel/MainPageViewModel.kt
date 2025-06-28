package com.example.dailysummary.viewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.dailysummary.data.PrefRepository
import com.example.dailysummary.data.SummaryRepository
import com.example.dailysummary.dto.PageYearMonth
import com.example.dailysummary.dto.Summary
import com.example.dailysummary.model.CalenderEntry
import com.example.dailysummary.model.CalenderOnePage
import com.example.dailysummary.model.summaryRefinement
import com.example.dailysummary.overlay.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import javax.inject.Inject

enum class Tab{
    Home,My,Social
}

@HiltViewModel
@SuppressLint("NewApi")
class MainPageViewModel @Inject constructor(
    private val prefRepository: PrefRepository,
    private val alarmScheduler: AlarmScheduler,
    private val summaryRepository: SummaryRepository,
    ):ViewModel(){

    private val _selectedTab = MutableStateFlow(Tab.Home)
    val selectedTab = _selectedTab.asStateFlow()

    fun updateTab(tab:String){
        _selectedTab.value = Tab.valueOf(tab)
    }

    val now = LocalDate.now()

    private val _currentYMPage = MutableStateFlow(PageYearMonth(now.year,now.monthValue))
    val currentYMPage = _currentYMPage.asStateFlow()

    fun setCurrentYMPage(value:PageYearMonth){
        if (currentYMPage.value != value) _currentYMPage.value = value
    }

    private val _pageCache = mutableStateMapOf<Int, CalenderOnePage>()
    val pageCache: SnapshotStateMap<Int, CalenderOnePage> = _pageCache

    // 요청 중인 페이지 추적 (중복 로딩 방지)
    private val loadingPages = mutableSetOf<Int>()

    fun loadPageIfAbsent(page: Int) {
        if (_pageCache.containsKey(page) || loadingPages.contains(page)) return

        loadingPages.add(page)

        viewModelScope.launch {
            try {
                val ym = PageYearMonth(page)
                val summaries = summaryRepository.getSummariesByMonth("%04d-%02d".format(ym.year, ym.month)).firstOrNull()?: emptyList()
                _pageCache[page] = summaryRefinement(ym.year,ym.month,summaries)
            } catch (e: Exception) {
                Log.w("ViewModel", "Page $page load failed: ${e.message}")
            } finally {
                loadingPages.remove(page)
            }
        }
    }
    /*
    val pager = Pager(
        config = PagingConfig(
            pageSize = 1, // 한 달 단위로 가져오므로 1개씩 가져옴
            enablePlaceholders = false,
            initialLoadSize = 3,
        ),
        pagingSourceFactory = { calenderPagingSourceFactory.create() }
    ).flow.cachedIn(viewModelScope)*/

    /*
    fun getCalenderPager(start: YearMonth = currentYMPage.value):Flow<PagingData<CalenderOnePage>>{
        return Pager(
            config = PagingConfig(
                pageSize = 1, // 한 달 단위로 가져오므로 1개씩 가져옴
                enablePlaceholders = false
            ),
            pagingSourceFactory = { calenderPagingSourceFactory.create(start) }
        ).flow.cachedIn(viewModelScope)
    }*/



    /*

    @SuppressLint("NewApi")
    val currentDate = LocalDate.now() // 현재 날짜 가져오기
    @SuppressLint("NewApi")
    val currentYear = currentDate.year // 현재 연도
    @SuppressLint("NewApi")
    val currentMonth = currentDate.monthValue // 현재 월 (숫자로)



    private val _selectedYearAndMonth = MutableStateFlow(Pair(currentYear,currentMonth))
    val selectedYearAndMonth = _selectedYearAndMonth.asStateFlow()
*/
    /*
    fun setSelectedYearAndMonth(year:Int,month: Int){
        _selectedYearAndMonth.value= Pair(year,month)
        calenderRefresh()
    }

    fun prevMonth(){
        if(selectedYearAndMonth.value.second==1)
            setSelectedYearAndMonth(selectedYearAndMonth.value.first-1,12)
        else setSelectedYearAndMonth(selectedYearAndMonth.value.first,selectedYearAndMonth.value.second-1)
    }

    fun nextMonth(){
        if(selectedYearAndMonth.value.second==12)
            setSelectedYearAndMonth(selectedYearAndMonth.value.first+1,1)
        else setSelectedYearAndMonth(selectedYearAndMonth.value.first,selectedYearAndMonth.value.second+1)
    }

    */

    //private val _currentMonthSummaries = MutableStateFlow(listOf<Summary>())

    //val currentMonthSummaries = _currentMonthSummaries.asStateFlow()

    /*
    fun setCurrentMonthSummaries(list:List<Summary>){
        _currentMonthSummaries.value=list
        Log.d("currentsum","list:$list")
    }*/
    /*

    private val _calenderEntries = MutableStateFlow(listOf<CalenderEntry>())

    val calenderEntries = _calenderEntries.asStateFlow()

    fun setCalenderEntries(){
        val list = mutableListOf<CalenderEntry>()
        val frontBlankCount=LocalDate.of(selectedYearAndMonth.value.first,selectedYearAndMonth.value.second,1).dayOfWeek.value%7
        repeat(frontBlankCount){
            list.add(CalenderEntry(isBlank = true,isWritten = false, day = 0 , summaryIndex = 0))
        }
        val daysInMonth = LocalDate.of(selectedYearAndMonth.value.first,selectedYearAndMonth.value.second,1).lengthOfMonth()
        repeat(daysInMonth){
            list.add(CalenderEntry(isBlank = false,isWritten = false, day = it+1 , summaryIndex = 0))
        }
        currentMonthSummaries.value.forEachIndexed{ index, summary ->
            list[frontBlankCount-1+summary.date.dayOfMonth] = CalenderEntry(isBlank = false,isWritten = true, day = summary.date.dayOfMonth , summaryIndex = index)
        }
        val backBlankCount = (7 - (frontBlankCount + daysInMonth) % 7) % 7
        repeat(backBlankCount){
            list.add(CalenderEntry(isBlank = true,isWritten = false, day = 0 , summaryIndex = 0))
        }

        _calenderEntries.value=list
    }
    */
    /*
    fun getSummaries(year: Int,month: Int):List<Summary>{
        val yearMonth = "%04d-%02d".format(year, month)
        return runBlocking {
            getSummariesByMonth(yearMonth).first()
        }
    }*/



    /*
    fun readSummary(index:Int):Summary{
        return currentMonthSummaries.value[index]
    }*/




}