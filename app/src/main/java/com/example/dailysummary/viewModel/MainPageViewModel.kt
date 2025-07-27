package com.example.dailysummary.viewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailysummary.data.SummaryRepository
import com.example.dailysummary.dto.PageYearMonth
import com.example.dailysummary.dto.Summary
import com.example.dailysummary.dto.CalenderEntry
import com.example.dailysummary.dto.CalenderOnePage
import com.example.dailysummary.dto.PeriodRatingRatios
import com.example.dailysummary.dto.RatingRatios
import com.example.dailysummary.dto.summaryRefinement
import com.example.dailysummary.pages.mainPageTabs.StatsPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

enum class Tab{
    Home,Calender,My
}

@HiltViewModel
@SuppressLint("NewApi")
class MainPageViewModel @Inject constructor(
    private val summaryRepository: SummaryRepository,
    ):ViewModel(){


    private val _todayDiaries:MutableStateFlow<List<Summary>> = MutableStateFlow(emptyList())
    val todayDiaries = _todayDiaries.asStateFlow()

    fun setTodayDiaries(value:List<Summary>){
        _todayDiaries.value = value
    }

    private val _selectedPeriod = MutableStateFlow(StatsPeriod.MONTH)
    val selectedPeriod = _selectedPeriod.asStateFlow()

    fun setSelectedPeriod(value:StatsPeriod){
        _selectedPeriod.value = value
    }

    private val _periodRatingRatios = MutableStateFlow(PeriodRatingRatios.dummy())
    val periodRatingRatios = _periodRatingRatios.asStateFlow()

    fun setPeriodRatingRatios(value:PeriodRatingRatios){
        _periodRatingRatios.value = value
    }

    private val _recentSummaries:MutableStateFlow<List<Summary>> = MutableStateFlow(emptyList())
    val recentSummaries = _recentSummaries.asStateFlow()

    fun setRecentSummaries(value:List<Summary>){
        _recentSummaries.value = value
    }

    private val _selectedTab = MutableStateFlow(Tab.Home)
    val selectedTab = _selectedTab.asStateFlow()

    fun updateTab(tab:String){
        _selectedTab.value = Tab.valueOf(tab)
    }

    private val _clickedDay:MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    val clickedDay = _clickedDay.asStateFlow()

    private val _clickedEntry:MutableStateFlow<CalenderEntry?> = MutableStateFlow(null)
    val clickedEntry = _clickedEntry.asStateFlow()

    fun clickDay(date:LocalDate, entry: CalenderEntry){
        _clickedDay.value = date
        _clickedEntry.value = entry
        Log.d("aaaa",clickedDay.value.toString() )
    }


    val now = LocalDate.now()

    private val _pageCache = mutableStateMapOf<Int, CalenderOnePage>()
    val pageCache: SnapshotStateMap<Int, CalenderOnePage> = _pageCache

    // 요청 중인 페이지 추적 (중복 로딩 방지)
    private val loadingPages = mutableSetOf<Int>()

    suspend fun loadPageIfAbsent(page: Int) {
        if (_pageCache.containsKey(page) || loadingPages.contains(page)) return

        loadingPages.add(page)

        try {
            val ym = PageYearMonth(page)
            val summaries = summaryRepository.getSummariesByMonth("%04d-%02d".format(ym.year, ym.month))
            _pageCache[page] = summaryRefinement(ym.year,ym.month,summaries)
        } catch (e: Exception) {
            Log.w("ViewModel", "Page $page load failed: ${e.message}")
        } finally {
            loadingPages.remove(page)
        }
    }
    fun homeRefresh(){
        viewModelScope.launch {
            setTodayDiaries(summaryRepository.getSummariesByDate(LocalDate.now()))
            setPeriodRatingRatios(
                PeriodRatingRatios(
                    RatingRatios(7,summaryRepository.getSummariesLastWeek()),
                    RatingRatios(30,summaryRepository.getSummariesLastMonth()),
                    RatingRatios(365,summaryRepository.getSummariesLastYear()),
                )
            )
            setRecentSummaries(summaryRepository.getRecentSummariesExcludingToday(10))
        }
    }

    fun calenderRefresh() {
        _pageCache.clear()
        loadingPages.clear()

        viewModelScope.launch {
            val currentPage = PageYearMonth(clickedDay.value).toPageNum() // 현재 날짜 → page 계산
            loadPageIfAbsent(currentPage)
            withContext(Dispatchers.Main){
                val todayPage = _pageCache[currentPage]
                todayPage?.let { page ->
                    val todayEntry = page.calenderEntries
                        .firstOrNull { it.date.isEqual(clickedDay.value) }
                    _clickedEntry.value = todayEntry
                    Log.d("calenderRefresh",todayEntry!!.toString())
                }
                Log.d("calenderRefresh","refreshed")
            }
        }
    }


    init {
        homeRefresh()
        calenderRefresh()
        viewModelScope.launch {
            summaryRepository.shouldRefresh.collect { refresh ->
                if (refresh) {
                    homeRefresh()
                    calenderRefresh()
                    summaryRepository.clearRefreshFlag()
                }
            }
        }
    }
}
