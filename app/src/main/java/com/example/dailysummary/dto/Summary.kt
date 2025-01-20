package com.example.dailysummary.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "summary")
data class Summary(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Primary key 추가
    @ColumnInfo(name = "written_time") val writtenTime: LocalDate,
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "content") val content: String
)