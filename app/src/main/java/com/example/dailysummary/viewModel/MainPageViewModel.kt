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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    private val _selectedTab = MutableStateFlow(Tab.Home)
    val selectedTab = _selectedTab.asStateFlow()

    fun updateTab(tab:String){
        _selectedTab.value = Tab.valueOf(tab)
    }

    private val _clickedDay:MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    val clickedDay = _clickedDay.asStateFlow()

    private val _clickedEntry:MutableStateFlow<CalenderEntry?> = MutableStateFlow(null)
    val clickedEntry = _clickedEntry.asStateFlow()

    fun clickDay(date:LocalDate, entry:CalenderEntry){
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

    fun calenderRefresh() {
        _pageCache.clear()
        loadingPages.clear()

        viewModelScope.launch {
            val currentPage = PageYearMonth(clickedDay.value).toPageNum() // 현재 날짜 → page 계산
            val pagesToLoad = listOf(currentPage - 1, currentPage, currentPage + 1)

            pagesToLoad.forEach { page ->
                loadPageIfAbsent(page)
            }

            withContext(Dispatchers.Main){
                // ✅ 2. 3장 로드 후 clickedEntry 초기화
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
        calenderRefresh()
        viewModelScope.launch {
            summaryRepository.shouldRefresh.collect { refresh ->
                if (refresh) {
                    calenderRefresh()
                    summaryRepository.clearRefreshFlag()
                }
            }
        }
    }
}
