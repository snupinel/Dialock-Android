package com.example.dailysummary.pages.mainPageTabs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dailysummary.dto.DayRating
import com.example.dailysummary.dto.PeriodRatingRatios
import com.example.dailysummary.dto.Summary
import com.example.dailysummary.viewModel.MainPageViewModel
import java.time.LocalDate

@Composable
fun HomeTab(navController: NavController,
            viewModel:MainPageViewModel = hiltViewModel()) {

    val todayDiaries by viewModel.todayDiaries.collectAsState()
    val periodRatingRatios by viewModel.periodRatingRatios.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val recentSummaries by viewModel.recentSummaries.collectAsState()
    val today = LocalDate.now()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {

        item{
            TodaySummaryCard(todayDiaries = todayDiaries,
                onWrite = {
                          navController.navigate("WriteDiaryPage/${today.year}/${today.monthValue}/${today.dayOfMonth}")
                          },
                onEdit = {
                    navController.navigate("DiaryPage/$it")
                })
            Spacer(modifier = Modifier.height(16.dp))
            MonthlyStatsSection(
                periodRatingRatios,
                onPeriodChange = {
                                 viewModel.setSelectedPeriod(it)
                },
                selectedPeriod = selectedPeriod
            )
            Spacer(modifier = Modifier.height(16.dp))
            RecentEntriesSection(
                recentSummaries = recentSummaries,
                onClickDetail = { id ->
                    navController.navigate("DiaryPage/$id")
                }
            )
        }

    }


}

@Composable
fun TodaySummaryCard(
    todayDiaries: List<Summary>,
    onWrite: () -> Unit,
    onEdit: (Int) -> Unit // ✅ 특정 일기를 수정하려면 id 필요
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ✅ 제목
            Text(
                text = "오늘",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            // ✅ 오늘 작성된 일기 리스트
            if (todayDiaries.isEmpty()) {
                Text(
                    text = "No diaries written today.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    todayDiaries.take(3).forEach { summary ->
                        DiaryPreviewCard(summary = summary) {
                            onEdit(summary.id)
                        }
                    }
                    if (todayDiaries.size > 3) {
                        Text(
                            text = "+${todayDiaries.size - 3} more...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
            // ✅ 작성 버튼
            OutlinedButton(onClick = onWrite) {
                Text("새 일기 작성")
            }
        }
    }
}

@Composable
fun MonthlyStatsSection(
    periodRatingRatios:PeriodRatingRatios,
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

@Composable
fun RecentEntriesSection(
    recentSummaries: List<Summary>,
    onClickDetail: (Int) -> Unit
) {
    Column{
        Text("최근 일기들", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        recentSummaries.forEach { summary ->
            DiaryPreviewCard(summary = summary, showDate = true) {
                onClickDetail(summary.id)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun MotivationalMessage(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.secondary
    )
}

