package com.example.dailysummary.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.dailysummary.dto.Summary
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

class SummaryRepository @Inject constructor(
    private val summaryDAO: SummaryDAO
){
    fun getAllSummaries(): Flow<List<Summary>> = summaryDAO.getAllSummaries()

    fun getSummariesByDate(date: LocalDate): Summary? = summaryDAO.getSummaryByDate(date)

    fun getSummariesByMonth(yearMonth: String): Flow<List<Summary>> {
        return summaryDAO.getSummariesByMonth(yearMonth)
    }

    suspend fun insertSummary(summary: Summary) {
        summaryDAO.insertSummary(summary)
        Log.d("room",summary.toString())
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
    suspend fun updateThumbByDate(date: LocalDate, isThumbUp: Boolean){
        summaryDAO.updateThumbByDate(date, isThumbUp)
    }
    suspend fun updateLikeByDate(date: LocalDate, isLiked: Boolean){
        summaryDAO.updateLikeByDate(date, isLiked)
    }

    suspend fun updateImageUrisByDate(date: LocalDate,imageUris:List<Uri>){
        summaryDAO.updateImageUrisByDate(date,imageUris)
    }

}