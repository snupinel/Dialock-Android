package com.example.dailysummary.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.dailysummary.dto.DayRating
import com.example.dailysummary.dto.Summary
import com.example.dailysummary.utils.popBackStackExclusive
import com.example.dailysummary.viewModel.DiaryPageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryPage(
    navController: NavController,
    id: Int,
    viewModel: DiaryPageViewModel = hiltViewModel()
) {
    val summary by viewModel.summary.collectAsState()
    val backStackEntry = navController.previousBackStackEntry
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit){
        viewModel.initialize(id)
    }

    if(summary!=null){
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = summary!!.date.toString(), // yyyy-MM-dd
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                navController.popBackStackExclusive()
                            }
                        }) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (summary!!.isBookmarked) {
                            Icon(
                                imageVector = Icons.Filled.Bookmark,
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = "Bookmarked"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            // ✅ 수정 페이지로 이동
                            navController.navigate("WriteDiaryPage/${summary!!.date.year}/${summary!!.date.monthValue}/${summary!!.date.dayOfMonth}?id=${summary!!.id}")
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit")
                    }
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                viewModel.deleteDiary(id)
                                navController.popBackStackExclusive()
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                // 최초 작성 시간
                Text(
                    text = "작성 시간: ${summary!!.writtenTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(12.dp))

                // 제목
                Text(
                    text = summary!!.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(8.dp))

                // 내용
                Text(
                    text = summary!!.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(16.dp))

                // 하루 평가 (아이콘으로 표시)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = when (summary!!.dayRating) {
                            DayRating.GOOD -> Icons.Outlined.ThumbUp
                            DayRating.SOSO -> Icons.Outlined.SentimentNeutral
                            DayRating.BAD -> Icons.Outlined.ThumbDown
                        },
                        tint = when (summary!!.dayRating) {
                            DayRating.GOOD -> MaterialTheme.colorScheme.primary
                            DayRating.SOSO -> MaterialTheme.colorScheme.tertiary
                            DayRating.BAD -> MaterialTheme.colorScheme.error
                        },
                        contentDescription = "Day Rating"
                    )
                    Text(
                        text = when (summary!!.dayRating) {
                            DayRating.GOOD -> "좋은 하루였어요"
                            DayRating.SOSO -> "그럭저럭이었어요"
                            DayRating.BAD -> "안 좋은 하루였어요"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(20.dp))

                // 이미지 Horizontal Pager
                if (summary!!.imageUris.isNotEmpty()) {
                    val pagerState = rememberPagerState { summary!!.imageUris.size }

                    HorizontalPager(
                        state = pagerState,
                        pageSpacing = 12.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    ) { page ->
                        AsyncImage(
                            model = summary!!.imageUris[page],
                            contentDescription = "Diary Image $page",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // 페이지 인디케이터
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(summary!!.imageUris.size) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (pagerState.currentPage == index)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
