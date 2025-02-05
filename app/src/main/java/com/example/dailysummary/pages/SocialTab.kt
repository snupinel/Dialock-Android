package com.example.dailysummary.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SocialTab(){
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "추가 예정!")
    }
}