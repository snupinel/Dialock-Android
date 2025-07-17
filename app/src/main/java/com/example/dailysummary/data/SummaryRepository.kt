package com.example.dailysummary.data

import android.net.Uri
import android.util.Log
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
    }

    suspend fun deleteSummaryById(id: Int) {
        summaryDAO.deleteSummaryById(id)
    }

    suspend fun updateTitleByDate(id: Int, title: String){
        summaryDAO.updateTitleById(id, title)
    }

    suspend fun updateContentByDate(id: Int, content: String){
        summaryDAO.updateContentById(id, content)
    }
    suspend fun updateRatingByDate(id: Int, dayRating: DayRating){
        summaryDAO.updateThumbById(id, dayRating)
    }
    suspend fun updateLikeByDate(id: Int, isLiked: Boolean){
        summaryDAO.updateLikeById(id, isLiked)
    }

    suspend fun updateImageUrisByDate(id: Int,imageUris:List<Uri>){
        summaryDAO.updateImageUrisById(id,imageUris)
    }

}