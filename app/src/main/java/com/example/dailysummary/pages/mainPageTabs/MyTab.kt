package com.example.dailysummary.pages.mainPageTabs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dailysummary.dto.PeriodRatingRatios
import com.example.dailysummary.viewModel.MainPageViewModel

@Composable
fun MyTab(navController: NavController,viewModel:MainPageViewModel = hiltViewModel()){

    val periodRatingRatios by viewModel.periodRatingRatios.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        item {
            BigAlarmSettingButton {
                navController.navigate("AlarmSettingPage")
            }
            MonthlyStatsSection(
                periodRatingRatios,
                onPeriodChange = {
                    viewModel.setSelectedPeriod(it)
                },
                selectedPeriod = selectedPeriod
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("BookmarkedDiariesPage") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "북마크한 일기 보기")
            }
        }
    }
}
@Composable
fun BigAlarmSettingButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(60.dp), // 큼지막하게
        shape = RoundedCornerShape(16.dp),
        elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Alarm, // 기본 제공 아이콘 사용 (import 필요)
            contentDescription = "Alarm Settings",
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = "알람 설정하기",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun MonthlyStatsSection(
    periodRatingRatios: PeriodRatingRatios,
    selectedPeriod: StatsPeriod,
    onPeriodChange: (StatsPeriod) -> Unit,
) {
    val goodRatio = periodRatingRatios[selectedPeriod].goodRatio
    val sosoRatio = periodRatingRatios[selectedPeriod].sosoRatio
    val badRatio = periodRatingRatios[selectedPeriod].badRatio
    val writtenDays =periodRatingRatios[selectedPeriod].writtenDays
    val totalDays = periodRatingRatios[selectedPeriod].totalDays
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ✅ 제목 + 기간 선택 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("통계", style = MaterialTheme.typography.titleMedium)

                PeriodSelector(
                    selectedPeriod = selectedPeriod,
                    onPeriodChange = onPeriodChange
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ✅ 왼쪽: 텍스트 통계
                Column {
                    Text(
                        text = "${(goodRatio * 100).toInt()}% GOOD",
                        color = Color(0xFF4CAF50), // Green
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${(sosoRatio * 100).toInt()}% SOSO",
                        color = Color(0xFFFFC107), // Yellow
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${(badRatio * 100).toInt()}% BAD",
                        color = Color(0xFFF44336), // Red
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "$writtenDays / $totalDays days written",
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // ✅ 오른쪽: 파이 차트
                PieChart(
                    data = listOf(goodRatio, sosoRatio, badRatio),
                    colors = listOf(
                        Color(0xFF4CAF50),
                        Color(0xFFFFC107),
                        Color(0xFFF44336)
                    ),
                    size = 80.dp
                )
            }
        }
    }
}
enum class StatsPeriod(val label: String) {
    WEEK("1W"),
    MONTH("1M"),
    YEAR("1Y")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodSelector(
    selectedPeriod: StatsPeriod,
    onPeriodChange: (StatsPeriod) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        StatsPeriod.values().forEach { period ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodChange(period) },
                label = { Text(period.label) },
                modifier = Modifier.heightIn(max = 32.dp)
            )
        }
    }
}


@Composable
fun PieChart(
    data: List<Float>,
    colors: List<Color>,
    size: Dp = 100.dp
) {
    Canvas(modifier = Modifier.size(size)) {
        var startAngle = -90f
        data.forEachIndexed { index, value ->
            val sweep = value * 360f
            drawArc(
                color = colors[index],
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = true
            )
            startAngle += sweep
        }
    }
}
