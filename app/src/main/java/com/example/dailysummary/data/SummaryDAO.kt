package com.example.dailysummary.data

import android.net.Uri
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

    @Query("DELETE FROM summary WHERE date = :date")
    suspend fun deleteSummaryByDate(date: LocalDate): Int

    @Query(" UPDATE summary SET title = :title WHERE date = :date")
    suspend fun updateTitleByDate(date: LocalDate, title: String): Int
    @Query(" UPDATE summary SET content = :content WHERE date = :date")
    suspend fun updateContentByDate(date: LocalDate, content: String): Int

    @Query(" UPDATE summary SET is_thumb_up = :isThumbUp WHERE date = :date")
    suspend fun updateThumbByDate(date: LocalDate, isThumbUp: Boolean): Int

    @Query(" UPDATE summary SET is_like_checked = :isLikeChecked WHERE date = :date")
    suspend fun updateLikeByDate(date: LocalDate, isLikeChecked: Boolean): Int

    @Query(" UPDATE summary SET image_uris = :imageUris WHERE date = :date")
    suspend fun updateImageUrisByDate(date: LocalDate, imageUris: List<Uri>): Int
}