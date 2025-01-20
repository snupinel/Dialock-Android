package com.example.dailysummary.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dailysummary.dto.Summary

@RequiresApi(Build.VERSION_CODES.O)
@Database(entities = [Summary::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase:RoomDatabase(){
    abstract fun summaryDAO(): SummaryDAO
}