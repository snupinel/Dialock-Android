package com.example.dailysummary.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailysummary.data.PrefRepository
import com.example.dailysummary.data.SummaryRepository
import com.example.dailysummary.dto.DayRating
import com.example.dailysummary.dto.Summary
import com.example.dailysummary.overlay.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class WriteDiaryPageViewModel @Inject constructor(
    private val summaryRepository: SummaryRepository,
): ViewModel() {

    private val _id = MutableStateFlow(0)
    val id: StateFlow<Int> = _id.asStateFlow()
    fun setId(value: Int) {
        _id.value = value
    }

    private val _writtenTime = MutableStateFlow(LocalDateTime.now())
    val writtenTime: StateFlow<LocalDateTime> = _writtenTime.asStateFlow()
    fun setWrittenTime(value: LocalDateTime) {
        _writtenTime.value = value
    }


    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()
    fun setTitle(value: String) {
        _title.value = value
    }

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()
    fun setContent(value: String) {
        _content.value = value
    }

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked.asStateFlow()
    fun toggleBookmark() {
        _isBookmarked.value = !isBookmarked.value
    }

    fun setBookmark(value:Boolean) {
        _isBookmarked.value = value
    }

    private val _dayRating = MutableStateFlow(DayRating.SOSO)
    val dayRating: StateFlow<DayRating> = _dayRating.asStateFlow()
    fun setDayRating(value:DayRating) {
        _dayRating.value = value
    }

    private val _photoList:MutableStateFlow<List<Uri>> = MutableStateFlow(emptyList())
    val photoList: StateFlow<List<Uri>> = _photoList.asStateFlow()
    fun setPhotoList(value:List<Uri>) {
        _photoList.value = value
    }

    suspend fun saveDiary(
        date: LocalDate,
    ){
        summaryRepository.insertSummary(
            Summary(
                writtenTime = LocalDateTime.now(),
                date = date,
                title = title.value,
                content = content.value,
                dayRating = dayRating.value,
                isBookmarked = isBookmarked.value,
                imageUris = photoList.value,
                shouldBlockAlarm = false

            )
        )
        Log.d("saveDiary",photoList.value.size.toString())
    }

    suspend fun updateDiary(
        date: LocalDate,
    ){
        summaryRepository.updateSummary(
            Summary(
                id = id.value,
                writtenTime = writtenTime.value,
                date = date,
                title = title.value,
                content = content.value,
                dayRating = dayRating.value,
                isBookmarked = isBookmarked.value,
                imageUris = photoList.value,
                shouldBlockAlarm = false

            )
        )
    }

    fun initialize(id:Int){
        viewModelScope.launch {
            val summary = summaryRepository.getSummaryById(id)
            setId(summary.id)
            setWrittenTime(summary.writtenTime)
            setTitle(summary.title)
            setContent(summary.content)
            setBookmark(summary.isBookmarked)
            setDayRating(summary.dayRating)
            setPhotoList(summary.imageUris)
        }
    }
}