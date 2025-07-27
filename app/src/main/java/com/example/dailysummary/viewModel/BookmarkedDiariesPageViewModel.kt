package com.example.dailysummary.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.dailysummary.data.SummaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookmarkedDiariesPageViewModel @Inject constructor(
    private val summaryRepository: SummaryRepository,
): ViewModel() {
    val bookmarkedSummaries = summaryRepository
        .getBookmarkedSummariesPaging()
        .cachedIn(viewModelScope)
}