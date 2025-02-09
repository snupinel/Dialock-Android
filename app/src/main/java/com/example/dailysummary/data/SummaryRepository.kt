package com.example.dailysummary.data

import android.content.Context
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

    suspend fun updateTitleByDate(date: LocalDate, title: String){
        summaryDAO.updateTitleByDate(date, title)
    }
}