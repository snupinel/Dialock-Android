package com.example.dailysummary.dto

import com.example.dailysummary.pages.mainPageTabs.StatsPeriod

data class RatingRatios(
    val goodRatio: Float,
    val sosoRatio: Float,
    val badRatio: Float,
    val writtenDays:Int,
    val totalDays:Int,
){
    constructor(
        totalDays: Int,
        summaries: List<Summary>
    ) : this(
        totalDays = totalDays,
        writtenDays = summaries.map { it.date }.toSet().size,
        goodRatio = summaries.count { it.dayRating == DayRating.GOOD }.toFloat() / (summaries.size.takeIf { it > 0 } ?: 1),
        sosoRatio = summaries.count { it.dayRating == DayRating.SOSO }.toFloat() / (summaries.size.takeIf { it > 0 } ?: 1),
        badRatio = summaries.count { it.dayRating == DayRating.BAD }.toFloat() / (summaries.size.takeIf { it > 0 } ?: 1)
    )

}

data class PeriodRatingRatios(
    val weekRatios:RatingRatios,
    val monthRatios:RatingRatios,
    val yearRatios:RatingRatios,
){
    companion object{
        fun dummy():PeriodRatingRatios{
            return PeriodRatingRatios(
                RatingRatios(0, emptyList()),
                RatingRatios(0, emptyList()),
                RatingRatios(0, emptyList()),
            )
        }
    }
    operator fun get(period: StatsPeriod): RatingRatios {
        return when (period) {
            StatsPeriod.WEEK -> weekRatios
            StatsPeriod.MONTH -> monthRatios
            StatsPeriod.YEAR -> yearRatios
        }
    }
}