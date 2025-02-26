package com.example.dailysummary.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.dailysummary.data.CalenderPagingSource
import com.example.dailysummary.data.YearMonth
import dagger.assisted.AssistedFactory

@AssistedFactory
interface CalenderPagingSourceFactory {
    @RequiresApi(Build.VERSION_CODES.O)
    fun create(yearMonth: YearMonth = YearMonth()): CalenderPagingSource
}