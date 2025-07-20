package com.example.dailysummary.pages

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dailysummary.components.BookmarkButton
import com.example.dailysummary.components.DayRatingSelector
import com.example.dailysummary.dto.DayRating
import com.example.dailysummary.utils.popBackStackExclusive
import com.example.dailysummary.viewModel.WriteDiaryPageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteDiaryPage(
    navController: NavController,
    year: Int,
    month: Int,
    day: Int,
    id:Int? = null,
    viewModel: WriteDiaryPageViewModel = hiltViewModel()
) {
    // 🔹 상태 관리
    val title by viewModel.title.collectAsState()
    val content by viewModel.content.collectAsState()
    val isBookmarked by viewModel.isBookmarked.collectAsState()
    val dayRating by viewModel.dayRating.collectAsState()
    val photoList by viewModel.photoList.collectAsState()

    val dateText = "%04d-%02d-%02d".format(year, month, day)

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit){
        if(id!=null){
            viewModel.initialize(id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    BookmarkButton(isChecked = isBookmarked) {
                        viewModel.toggleBookmark()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        if(id==null){
                            viewModel.saveDiary(LocalDate.of(year,month,day))
                        }
                        else{
                            viewModel.updateDiary(LocalDate.of(year,month,day))
                        }
                        navController.popBackStackExclusive()
                    }
                }
            ) {
                Icon(Icons.Outlined.Check, contentDescription = "Save")
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
            // 제목 작성
            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.setTitle(it) },
                label = { Text("제목") },
                textStyle = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(Modifier.height(12.dp))

            // 내용 작성
            OutlinedTextField(
                value = content,
                onValueChange = { viewModel.setContent(it) },
                label = { Text("내용") },
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                maxLines = Int.MAX_VALUE
            )

            Spacer(Modifier.height(12.dp))

            // 🔹 사진 추가 버튼 및 첨부된 사진들
            Text(
                "사진 첨부",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.height(4.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            ) {
                // 사진 추가 버튼 (맨 앞)
                item {
                    AddPhotoButton { uris ->
                        viewModel.setPhotoList(photoList+uris)
                    }
                }

                // 추가된 사진 미리보기
                items(photoList.size) { index ->
                    PhotoThumbnail(
                        uri = photoList[index],
                        onRemove = {
                            viewModel.setPhotoList(photoList - photoList[index])
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // 하루 평가
            Text(
                "오늘의 하루 평가",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            DayRatingSelector(dayRating = dayRating, setRating = { viewModel.setDayRating(it)})
        }
    }

}

@Composable
fun AddPhotoButton(onPhotosAdded: (List<Uri>) -> Unit) {
    val context = LocalContext.current
    // ✅ 갤러리 실행 Launcher
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris: List<Uri?> ->
        val validUris = uris.filterNotNull()
        validUris.forEach { uri ->
            saveUriToPreferences(uri, context) // ✅ 영구 권한 부여
        }

        onPhotosAdded(validUris)
    }

    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .clickable {
                launcher.launch(arrayOf("image/*"))
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = "Add Photo",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
@Composable
fun PhotoThumbnail(uri: Uri, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        AsyncImage( // Coil 라이브러리 필요
            model = uri,
            contentDescription = "Photo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    CircleShape
                )
                .size(20.dp),
            onClick = onRemove
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Remove Photo",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

fun saveUriToPreferences(uri: Uri, context: Context) {
    val resolver = context.contentResolver
    val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

    try {
        resolver.takePersistableUriPermission(uri, flags) // ✅ 영구 권한 부여
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}