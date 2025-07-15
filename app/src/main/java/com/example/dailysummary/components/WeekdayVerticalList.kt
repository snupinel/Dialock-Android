package com.example.dailysummary.components

import android.os.Build
import android.text.format.DateFormat
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dailysummary.dto.AlarmTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

val dayNames = listOf("일","월","화","수","목","금","토")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekdayVerticalList(
    modifier: Modifier = Modifier,
    items: List<AlarmTime>,
    onItemClick: (Int) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items.forEachIndexed{ i, info ->
            DayListItem(
                modifier = Modifier.weight(1f),
                dayName = dayNames[i],
                dayColor = weekDayList[i].first?:MaterialTheme.colorScheme.onSurface,
                info = info,
                onClick = { onItemClick(i) }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayListItem(
    modifier: Modifier,
    dayName:String,
    dayColor:Color,
    info: AlarmTime,
    onClick: () -> Unit
) {
    val formattedTime = formatTimeBySystemSetting(LocalTime.of(info.hour,info.minute))

    val labelText = when {
        info.isNextDay -> "다음날"
        else -> "당일"
    }

    val labelColor = when {
        info.isNextDay -> Color(0xFF1976D2)
        else -> Color(0xFF388E3C)
    }

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Left,
                text = dayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = dayColor
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = formattedTime,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right,
                text = labelText,
                style = MaterialTheme.typography.labelMedium,
                color = labelColor
            )
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun formatTimeBySystemSetting(time: LocalTime): String {
    val context = LocalContext.current
    val is24Hour = remember { DateFormat.is24HourFormat(context) }

    val pattern = if (is24Hour) "HH:mm" else "a h:mm"
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    return time.format(formatter)
}

