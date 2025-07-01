package com.example.dailysummary.model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Immutable
import com.example.dailysummary.data.YearMonth
import com.example.dailysummary.dto.PageYearMonth
import com.example.dailysummary.dto.Summary
import java.time.LocalDate

@Immutable
data class CalenderEntry(
    val isBlank:Boolean,
    val isWritten:Boolean,
    val day:Int,
    val summaryIndex: Int,
)

@Immutable
data class CalenderOnePage(
    val year:Int,
    val month: Int,
    val calenderEntries:List<CalenderEntry>,
    val yearMonth: YearMonth = YearMonth(year, month)
){
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun dummy(page: Int): CalenderOnePage {
            val ym = PageYearMonth(page)
            return summaryRefinement(ym.year,ym.month, emptyList())
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
fun summaryRefinement(year:Int, month:Int, summaries:List<Summary>):CalenderOnePage{
    Log.d("summaryRefinement", "activate")
    return CalenderOnePage(
        year = year,
        month = month,
        calenderEntries = run {
            val list = mutableListOf<CalenderEntry>()
            val frontBlankCount=LocalDate.of(year,month,1).dayOfWeek.value%7
            repeat(frontBlankCount){
                list.add(CalenderEntry(isBlank = true,isWritten = false, day = 0 , summaryIndex = 0))
            }
            val daysInMonth = LocalDate.of(year,month,1).lengthOfMonth()
            repeat(daysInMonth){
                list.add(CalenderEntry(isBlank = false,isWritten = false, day = it+1 , summaryIndex = 0))
            }
            summaries.forEachIndexed{ index, summary ->
                list[frontBlankCount-1+summary.date.dayOfMonth] = CalenderEntry(isBlank = false,isWritten = true, day = summary.date.dayOfMonth , summaryIndex = index)
            }
            val backBlankCount = (7 - (frontBlankCount + daysInMonth) % 7) % 7
            repeat(backBlankCount){
                list.add(CalenderEntry(isBlank = true,isWritten = false, day = 0 , summaryIndex = 0))
            }
            list
        }
    )
}
