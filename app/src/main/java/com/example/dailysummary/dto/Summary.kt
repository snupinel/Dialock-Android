package com.example.dailysummary.dto

import java.time.LocalDate

data class Summary (
    val writtenTime: LocalDate,
    val date: LocalDate,
    val content:String
)