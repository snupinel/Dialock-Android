package com.example.dailysummary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RoundedCornerButton(
    modifier: Modifier=Modifier,
    color: Color =MaterialTheme.colorScheme.primary,
    onClick:()->Unit={},
    enabled:Boolean=true,
    content: @Composable() (BoxScope.() -> Unit)
){
    Box(
        modifier=modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color = if(enabled)color else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(enabled=enabled) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}