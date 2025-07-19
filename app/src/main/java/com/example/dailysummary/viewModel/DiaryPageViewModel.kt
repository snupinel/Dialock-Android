package com.example.dailysummary.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailysummary.data.SummaryRepository
import com.example.dailysummary.dto.Summary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryPageViewModel @Inject constructor(
    private val summaryRepository: SummaryRepository,
): ViewModel() {

    private val _summary:MutableStateFlow<Summary?> = MutableStateFlow(null)
    val summary: StateFlow<Summary?> = _summary.asStateFlow()

    fun initialize(id:Int){
        viewModelScope.launch {
            val sum = summaryRepository.getSummaryById(id)
            _summary.value = sum
        }
    }

    suspend fun deleteDiary(id:Int){
        summaryRepository.deleteSummaryById(id)
    }
}