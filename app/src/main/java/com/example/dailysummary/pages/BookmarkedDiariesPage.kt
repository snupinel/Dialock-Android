package com.example.dailysummary.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.dailysummary.components.BackButton
import com.example.dailysummary.components.BookmarkButton
import com.example.dailysummary.pages.mainPageTabs.DiaryPreviewCard
import com.example.dailysummary.utils.popBackStackExclusive
import com.example.dailysummary.viewModel.BookmarkedDiariesPageViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkedDiariesPage(navController: NavController, viewModel:BookmarkedDiariesPageViewModel = hiltViewModel()){
    val lazyPagingItems = viewModel.bookmarkedSummaries.collectAsLazyPagingItems()
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    BackButton {
                        scope.launch {
                            navController.popBackStackExclusive()
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
            )
        },
    ) { paddingValues->
        LazyColumn(modifier = Modifier.padding(paddingValues).fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            items(lazyPagingItems.itemCount) { index ->
                val summary = lazyPagingItems[index]
                summary?.let {
                    DiaryPreviewCard(summary = it, showDate = true) {
                        navController.navigate("DiaryPage/${summary.id}")
                    }
                }
            }
        }
    }

}