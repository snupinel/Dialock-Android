package com.example.dailysummary.dto

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class DayRating{
    GOOD,SOSO,BAD
}
@Entity(tableName = "summary")
data class Summary(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Primary key 추가
    @ColumnInfo(name = "written_time") val writtenTime: LocalDate,
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "day_rating") val dayRating: DayRating,
    @ColumnInfo(name = "is_like_checked") val isLikeChecked: Boolean,
    @ColumnInfo(name="image_uris") val imageUris:List<Uri>,
)

@RequiresApi(Build.VERSION_CODES.O)
val DEFAULT_SUMMARY = Summary(
    writtenTime = LocalDate.of(2000,1,1),
    date = LocalDate.of(2000,1,1),
    title = "",
    content = "",
    dayRating = DayRating.SOSO,
    isLikeChecked = false,
    imageUris = emptyList(),
)