package com.example.dailysummary.overlay

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyOverlayActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serviceIntent = Intent(this, SummaryService::class.java).apply {
            putExtra("year", intent.getIntExtra("year", 0))
            putExtra("month", intent.getIntExtra("month", 0))
            putExtra("day", intent.getIntExtra("day", 0))
            putExtra("isNextDay", intent.getBooleanExtra("isNextDay", false))
        }

        this.startService(serviceIntent)
        finish()
    }
}