package com.example.dailysummary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun PortraitLikeWrapper(content: @Composable () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp.dp

    // ✅ 세로 높이의 60%를 폭으로 사용 (원하는 비율로 변경 가능)
    val maxContentWidth = screenHeightDp * 0.6f

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // ✅ 실제 앱 콘텐츠
        Box(
            modifier = Modifier
                .widthIn(max = maxContentWidth)
                .fillMaxHeight()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(0.dp)
                )
        ) {
            content()
        }
    }
}


