package com.example.dailysummary.dto

import java.time.LocalDate

data class YearMonth(
    val year:Int,
    val month:Int
){
    constructor(pageNum:Int) : this(pageNum/12+1970,pageNum%12+1)
    constructor() : this(LocalDate.now().year, LocalDate.now().monthValue)

    fun toPageNum():Int{
        return (year-1970)*12 + month -1
    }

    fun prevMonth(): YearMonth {
        return YearMonth(this.toPageNum() - 1)
    }

    fun nextMonth(): YearMonth {
        return YearMonth(this.toPageNum() + 1)
    }
}