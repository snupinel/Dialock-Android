package com.example.dailysummary.viewModel

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailysummary.data.SummaryRepository
import com.example.dailysummary.dto.Summary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import java.time.LocalDate
import javax.inject.Inject

enum class EditState{
    Editing, Asking, Text
}

@HiltViewModel
//@SuppressLint("NewApi")
class SummaryPageViewModel @Inject constructor(
    //private val prefRepository: PrefRepository,
    //private val alarmScheduler: AlarmScheduler,
    private val summaryRepository: SummaryRepository,
): ViewModel(){


    private val _uploadImages = MutableStateFlow(listOf<Uri>())
    val uploadImages: StateFlow<List<Uri>> = _uploadImages.asStateFlow()
    fun updateUploadImages(newContent: List<Uri>) {
        _uploadImages.value=newContent
        //Log.d("aaaa","update gallery called")
    }

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    fun setTitle(text:String){
        _title.value=text
    }

    private val _editTitleValue = MutableStateFlow("제목")
    val editTitleValue: StateFlow<String> = _editTitleValue.asStateFlow()

    fun setEditTextValue(text:String){
        _editTitleValue.value=text
    }

    private val _year = MutableStateFlow(0)
    val year: StateFlow<Int> = _year.asStateFlow()

    private val _month = MutableStateFlow(0)
    val month: StateFlow<Int> = _month.asStateFlow()

    private val _day = MutableStateFlow(0)
    val day: StateFlow<Int> = _day.asStateFlow()



    @RequiresApi(Build.VERSION_CODES.O)
    fun initialize(year:Int, month:Int, day:Int){
        _year.value=year
        _month.value=month
        _day.value=day

        viewModelScope.launch{
            val summary = withContext(Dispatchers.IO){
                summaryRepository.getSummariesByDate(LocalDate.of(year,month,day))
            }
            if (summary == null) {
               //
            } else {
                _title.value = summary.title
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateTitleByDate(
        date: LocalDate = LocalDate.of(year.value,month.value,day.value),
        title:String=_title.value
    ){
        viewModelScope.launch{
            summaryRepository.updateTitleByDate(date, title)
        }
    }

    private val _isEditingTitle = MutableStateFlow(false)
    val isEditingTitle: StateFlow<Boolean> = _isEditingTitle.asStateFlow()

    fun setIsEditingTitle(isEditing:Boolean){
        if(isEditing) _editTitleValue.value = _title.value
        _isEditingTitle.value=isEditing
    }
}

