package com.example.dailysummary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dailysummary.dto.AnimationTarget

@Composable
fun AnimatedActionButton(
    text: String,
    animatedValue: AnimationTarget=AnimationTarget(1f,0.dp),
    isEnabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    textColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .offset(y = animatedValue.offsetY)
            .alpha(animatedValue.alpha)
            .height(60.dp)
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(4.dp))
            .background(color = backgroundColor)
            .then(
                if (isEnabled) {
                    Modifier.clickable { onClick() }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = textColor)
    }
}