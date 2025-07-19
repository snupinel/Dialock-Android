package com.example.dailysummary.data

import android.net.Uri
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.dailysummary.dto.DayRating
import com.example.dailysummary.dto.Summary
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface SummaryDAO {
    @Insert
    suspend fun insertSummary(summary: Summary)

    @Query("SELECT * FROM summary WHERE id = :id")
    suspend fun getSummaryById(id: Int): Summary

    @Query("SELECT * FROM summary WHERE date = :date")
    suspend fun getSummariesByDate(date: LocalDate): List<Summary>

    @Query("SELECT * FROM summary WHERE strftime('%Y-%m', date) = :yearMonth")
    suspend fun getSummariesByMonth(yearMonth: String): List<Summary>

    @Query("SELECT * FROM summary")
    fun getAllSummaries(): Flow<List<Summary>>

    @Delete
    suspend fun deleteSummary(summary: Summary)

    @Query("DELETE FROM summary WHERE id = :id")
    suspend fun deleteSummaryById(id: Int): Int

    @Update
    suspend fun updateSummary(summary: Summary)

    @Query(" UPDATE summary SET title = :title WHERE id = :id")
    suspend fun updateTitleById(id: Int, title: String): Int

    @Query(" UPDATE summary SET content = :content WHERE id = :id")
    suspend fun updateContentById(id: Int, content: String): Int

    @Query(" UPDATE summary SET day_rating = :dayRating WHERE id = :id")
    suspend fun updateThumbById(id: Int, dayRating: DayRating): Int

    @Query(" UPDATE summary SET is_bookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateLikeById(id: Int, isBookmarked: Boolean): Int

    @Query(" UPDATE summary SET image_uris = :imageUris WHERE id = :id")
    suspend fun updateImageUrisById(id: Int, imageUris: List<Uri>): Int
}