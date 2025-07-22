package com.example.dailysummary.dto

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

enum class DayRating{
    GOOD,SOSO,BAD
}
@Entity(tableName = "summary")
data class Summary(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Primary key 추가
    @ColumnInfo(name = "written_time") val writtenTime: LocalDateTime,
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "day_rating") val dayRating: DayRating,
    @ColumnInfo(name = "is_bookmarked") val isBookmarked: Boolean,
    @ColumnInfo(name="image_uris") val imageUris:List<Uri>,
    @ColumnInfo(name="should_block_alarm") val shouldBlockAlarm:Boolean,
){
    companion object{
        fun dummy():Summary{
            return Summary(
                id = 0,
                writtenTime = LocalDateTime.now(),
                date = LocalDate.now(),
                title = "제목",
                content = "내용",
                dayRating = DayRating.SOSO,
                isBookmarked = false,
                imageUris = emptyList(),
                shouldBlockAlarm = false
            )
        }
    }
}


