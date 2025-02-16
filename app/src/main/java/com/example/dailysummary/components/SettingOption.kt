package com.example.dailysummary.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailysummary.dto.AdviceOrForcing
import com.example.dailysummary.dto.AnimationTarget

@Composable
fun SettingOption(
    animatedValue: AnimationTarget= AnimationTarget(1f,0.dp),
    adviceOrForcing: AdviceOrForcing?,
    onOptionSelected: (Boolean) -> Unit,
    isEnabled: Boolean = true,
    startPageAnimationState: Int =0,
    onNextState: () -> Unit = {}
) {
    //Text("sad")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = animatedValue.offsetY)
            .alpha(animatedValue.alpha)
    ) {
        OptionBox(
            isSelected = adviceOrForcing==AdviceOrForcing.Advice,
            label = "권유",
            onCheckedChange = {onOptionSelected(true)},

        )
        Spacer(modifier = Modifier.width(16.dp))
        OptionBox(
            isSelected = adviceOrForcing==AdviceOrForcing.Forcing,
            label = "강요",
            onCheckedChange = {onOptionSelected(false)},

        )
    }
}

@Composable
fun OptionBox(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    label: String,
    onCheckedChange:(Boolean)->Unit
) {
    /*
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(shape = RoundedCornerShape(4.dp))
            .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(4.dp)
            )
            .background(color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label)
    }*/
    Row (
        modifier
            .fillMaxWidth()
            .height(30.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween){
        Text(label)
        Checkbox(checked = isSelected, onCheckedChange = onCheckedChange)
    }
}