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

    private val _clickedDay:MutableStateFlow<Int?> = MutableStateFlow(null)
    val clickedDay = _clickedDay.asStateFlow()

    fun clickDay(day:Int){
        if(day == clickedDay.value) _clickedDay.value = null
        else  _clickedDay.value = day
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

}