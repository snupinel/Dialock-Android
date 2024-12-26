package com.example.dailysummary.model

import java.time.LocalDate

data class CalenderEntry(
    val isBlank:Boolean,
    val isWritten:Boolean,
    val day:Int,
    val summaryIndex: Int,
)
