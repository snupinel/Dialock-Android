package com.example.dailysummary.dto

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

data class PageYearMonth(
    val year:Int,
    val month:Int
){
    constructor(pageNum:Int) : this(pageNum/12+1970,pageNum%12+1)
    constructor() : this(LocalDate.now().year, LocalDate.now().monthValue)

    constructor(date: LocalDate) : this(date.year,date.monthValue)

    fun toPageNum():Int{
        return (year-1970)*12 + month -1
    }

    fun prevMonth():PageYearMonth{
        return PageYearMonth(this.toPageNum()-1)
    }

    fun nextMonth():PageYearMonth{
        return PageYearMonth(this.toPageNum()+1)
    }
}
