package com.example.dailysummary.data

import android.net.Uri
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.dailysummary.dto.DayRating
import com.example.dailysummary.dto.Summary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummaryRepository @Inject constructor(
    private val summaryDAO: SummaryDAO
){
    fun getAllSummaries(): Flow<List<Summary>> = summaryDAO.getAllSummaries()

    suspend fun getSummariesByDate(date: LocalDate): List<Summary> = summaryDAO.getSummariesByDate(date)

    suspend fun getSummaryById(id: Int): Summary = summaryDAO.getSummaryById(id)

    suspend fun getSummariesByMonth(yearMonth: String): List<Summary> {
        return summaryDAO.getSummariesByMonth(yearMonth)
    }


    private val _shouldRefresh = MutableStateFlow(false)
    val shouldRefresh: StateFlow<Boolean>  = _shouldRefresh.asStateFlow()

    suspend fun insertSummary(summary: Summary) {
        summaryDAO.insertSummary(summary)
        Log.d("room",summary.toString())
        _shouldRefresh.value = true // ✅ 새로고침 신호
    }
    fun clearRefreshFlag() {
        _shouldRefresh.value = false
    }

    suspend fun deleteSummary(summary: Summary) {
        summaryDAO.deleteSummary(summary)
        _shouldRefresh.value = true // ✅ 새로고침 신호
    }

    suspend fun deleteSummaryById(id: Int) {
        summaryDAO.deleteSummaryById(id)
        _shouldRefresh.value = true // ✅ 새로고침 신호
    }

    suspend fun updateSummary(summary: Summary) {
        summaryDAO.updateSummary(summary)
        _shouldRefresh.value = true // ✅ 새로고침 신호
    }

    suspend fun getSummariesLastYear(): List<Summary>{
        return summaryDAO.getSummariesLastYear()
    }
    suspend fun getSummariesLastMonth(): List<Summary>{
        return summaryDAO.getSummariesLastMonth()
    }

    suspend fun getSummariesLastWeek(): List<Summary>{
        return summaryDAO.getSummariesLastWeek()
    }

    suspend fun getRecentSummariesExcludingToday(n:Int): List<Summary>{
        return summaryDAO.getRecentSummariesExcludingToday(n)
    }

    fun getBookmarkedSummariesPaging(): Flow<PagingData<Summary>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,        // 페이지당 20개 로드
                enablePlaceholders = false
            ),
            pagingSourceFactory = { summaryDAO.getBookmarkedSummariesPaging() }
        ).flow
    }


}