package com.example.dailysummary.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.dailysummary.dto.DayRating
import com.example.dailysummary.dto.Summary
import dagger.hilt.android.qualifiers.ApplicationContext
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

    suspend fun getSummaryByDate(date: LocalDate): Summary? = summaryDAO.getSummaryByDate(date)

    fun getSummariesByMonth(yearMonth: String): Flow<List<Summary>> {
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
    }

    suspend fun deleteSummaryByDate(date: LocalDate) {
        summaryDAO.deleteSummaryByDate(date)
    }

    suspend fun updateTitleByDate(date: LocalDate, title: String){
        summaryDAO.updateTitleByDate(date, title)
    }

    suspend fun updateContentByDate(date: LocalDate, content: String){
        summaryDAO.updateContentByDate(date, content)
    }
    suspend fun updateRatingByDate(date: LocalDate, dayRating: DayRating){
        summaryDAO.updateThumbByDate(date, dayRating)
    }
    suspend fun updateLikeByDate(date: LocalDate, isLiked: Boolean){
        summaryDAO.updateLikeByDate(date, isLiked)
    }

    suspend fun updateImageUrisByDate(date: LocalDate,imageUris:List<Uri>){
        summaryDAO.updateImageUrisByDate(date,imageUris)
    }

}