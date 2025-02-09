package com.example.dailysummary.dto

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "summary")
data class Summary(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Primary key 추가
    @ColumnInfo(name = "written_time") val writtenTime: LocalDate,
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "isThumbUp") val isThumbUp: Boolean,
    @ColumnInfo(name = "isLikeChecked") val isLikeChecked: Boolean,
)

@RequiresApi(Build.VERSION_CODES.O)
val DEFAULT_SUMMARY = Summary(
    writtenTime = LocalDate.of(2000,1,1),
    date = LocalDate.of(2000,1,1),
    title = "",
    content = "",
    isThumbUp = false,
    isLikeChecked = false
)