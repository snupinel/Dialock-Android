package com.example.dailysummary.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.dailysummary.components.EditButton
import com.example.dailysummary.components.ImagePager
import com.example.dailysummary.components.MoreVertButton
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
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit){
        viewModel.initialize(id)
    }
    val listState = rememberLazyListState()
    val alpha by remember{
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0 &&
                listState.firstVisibleItemScrollOffset == 0
            ) 0.2f else 1f
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if(summary!=null){
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                navController.popBackStackExclusive()
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        EditButton {
                            navController.navigate("WriteDiaryPage/${summary!!.date.year}/${summary!!.date.monthValue}/${summary!!.date.dayOfMonth}?id=${summary!!.id}")
                        }
                        MoreVertButton {
                            showDeleteDialog = true
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background.copy(alpha = alpha),
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                        actionIconContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                )
            }
        ) { paddingValues->
            LazyColumn(
                modifier = Modifier
                    .padding(bottom = paddingValues.calculateBottomPadding())
                    .fillMaxSize(),
                state = listState,
                verticalArrangement = Arrangement.Top
            ) {
                item {
                    if (summary!!.imageUris.isNotEmpty()) {
                        ImagePager(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            images = summary!!.imageUris)
                            Spacer(Modifier.height(12.dp))
                    }
                    else{
                        Spacer(modifier = Modifier.height(paddingValues.calculateTopPadding()))

                    }
                    Column(Modifier.padding(horizontal = 24.dp)){
                        Text(
                            text = "작성 시간: ${summary!!.writtenTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(Modifier.height(4.dp))

                        // 제목
                        Text(
                            text = summary!!.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
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
                    }


                }
            }
        }
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("삭제 확인") },
                text = { Text("정말 삭제하시겠습니까?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            scope.launch {
                                viewModel.deleteDiary(id)
                                navController.popBackStackExclusive()
                            }
                        }
                    ) {
                        Text("예")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("아니오")
                    }
                }
            )
        }

    }
}
