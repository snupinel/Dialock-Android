package com.example.dailysummary.pages.mainPageTabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun HomeTab(){
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "아 하단 탭 UI도 수정해야되는대", overflow = TextOverflow.Ellipsis)
    }
}