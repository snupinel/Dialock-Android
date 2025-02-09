package com.example.dailysummary.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.dailysummary.dto.Summary
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface SummaryDAO {
    @Insert
    suspend fun insertSummary(summary: Summary)

    @Query("SELECT * FROM summary WHERE date = :date LIMIT 1")
    fun getSummaryByDate(date: LocalDate): Summary

    @Query("SELECT * FROM summary WHERE strftime('%Y-%m', date) = :yearMonth")
    fun getSummariesByMonth(yearMonth: String): Flow<List<Summary>>

    @Query("SELECT * FROM summary")
    fun getAllSummaries(): Flow<List<Summary>>

    @Delete
    suspend fun deleteSummary(summary: Summary)


    @Query(" UPDATE summary SET title = :title WHERE date = :date")
    suspend fun updateTitleByDate(date: LocalDate, title: String): Int
}