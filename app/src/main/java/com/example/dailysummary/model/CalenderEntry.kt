package com.example.dailysummary.model

import android.util.Log
import androidx.compose.runtime.Immutable
import com.example.dailysummary.dto.YearMonth
import com.example.dailysummary.dto.PageYearMonth
import com.example.dailysummary.dto.Summary
import java.time.LocalDate

@Immutable
data class CalenderEntry (
    val date: LocalDate,
    val summaries: List<Summary>,
    val isBlank:Boolean,

    val isWritten:Boolean = summaries.isNotEmpty(),
    val isToday:Boolean = date.isEqual(LocalDate.now()),
    val isFuture:Boolean = date.isAfter(LocalDate.now()),
){
    companion object{
        fun dummy():CalenderEntry{
            return CalenderEntry(
                date = LocalDate.now(),
                summaries = emptyList(),
                isBlank = true,
            )
        }
    }
}

val DEFAULT_CALENDERENTRY = CalenderEntry(
    date = LocalDate.now(),
    summaries = emptyList(),
    isBlank = true
)

@Immutable
data class CalenderOnePage(
    val year:Int,
    val month: Int,
    val calenderEntries:List<CalenderEntry>,
    val yearMonth: YearMonth = YearMonth(year, month)
){
    companion object {
        fun dummy(page: Int): CalenderOnePage {
            val ym = PageYearMonth(page)
            return summaryRefinement(ym.year,ym.month, emptyList())
        }
    }
}
fun summaryRefinement(year: Int, month: Int, summaries: List<Summary>): CalenderOnePage {
    Log.d("summaryRefinement","called")
    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val daysInMonth = firstDayOfMonth.lengthOfMonth()

    // 이 달에 해당하는 Summary를 날짜별로 매핑
    val summaryMap = summaries
        .filter { it.date.year == year && it.date.monthValue == month }
        .groupBy { it.date }

    val calenderEntries = mutableListOf<CalenderEntry>()

    // 📌 앞쪽 공백 (해당 달 시작 전)
    val frontBlankCount = firstDayOfMonth.dayOfWeek.value % 7
    repeat(frontBlankCount) {
        val blankDate = firstDayOfMonth.minusDays((frontBlankCount - it).toLong())
        calenderEntries.add(
            CalenderEntry(
                date = blankDate,
                summaries = emptyList(),
                isBlank = true
            )
        )
    }

    // 📌 해당 달의 실제 날짜
    for (day in 1..daysInMonth) {
        val currentDate = LocalDate.of(year, month, day)
        calenderEntries.add(
            CalenderEntry(
                date = currentDate,
                summaries = summaryMap[currentDate] ?: emptyList(),
                isBlank = false
            )
        )
    }

    // 📌 뒤쪽 공백 (42칸 기준)
    val backBlankCount = 42 - frontBlankCount - daysInMonth
    repeat(backBlankCount) {
        val blankDate = LocalDate.of(year, month, daysInMonth)
            .plusDays((it + 1).toLong())
        calenderEntries.add(
            CalenderEntry(
                date = blankDate,
                summaries = emptyList(),
                isBlank = true
            )
        )
    }

    return CalenderOnePage(
        year = year,
        month = month,
        calenderEntries = calenderEntries
    )
}

