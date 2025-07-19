package com.example.dailysummary.data

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class Converters {


    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val ldtFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDate(value: LocalDate): String {
        return value.format(formatter)
    }

    @TypeConverter
    fun toLocalDate(value: String): LocalDate {
        return LocalDate.parse(value, formatter)
    }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime): String {
        return value.format(ldtFormatter)
    }

    @TypeConverter
    fun toLocalDateTime(value: String): LocalDateTime {
        return LocalDateTime.parse(value, ldtFormatter)
    }

    @TypeConverter
    fun fromUriList(uriList: List<Uri>?): String? {
        return uriList?.joinToString(",") { it.toString() } // ✅ URI 리스트 → 쉼표 구분 문자열로 변환
    }

    @TypeConverter
    fun toUriList(data: String?): List<Uri> {
        return data?.split(",")?.map { Uri.parse(it) } ?: emptyList() // ✅ 쉼표 구분 문자열 → URI 리스트로 변환
    }
}
