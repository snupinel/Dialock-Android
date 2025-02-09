package com.example.dailysummary.viewModel

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailysummary.data.SummaryRepository
import com.example.dailysummary.dto.DEFAULT_SUMMARY
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
@RequiresApi(Build.VERSION_CODES.O)
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
    private val _isWritten= MutableStateFlow(false)

    val isWritten:StateFlow<Boolean> = _isWritten.asStateFlow()

    fun setIsWritten(isWritten:Boolean){
        _isWritten.value=isWritten
    }

    private val _summary= MutableStateFlow(DEFAULT_SUMMARY)

    val summary:StateFlow<Summary> = _summary.asStateFlow()

    fun setSummary(summary:Summary){
        _summary.value=summary
    }

    fun setTitle(title:String){
        setSummary(summary.value.copy(title = title))
    }

    fun setContent(content:String){
        setSummary(summary.value.copy(content = content))
    }

    fun setDate(date:LocalDate){
        setSummary(summary.value.copy(date = date))
    }

    fun setThumb(isThumbUp:Boolean){
        setSummary(summary.value.copy(isThumbUp = isThumbUp))
    }

    fun setLike(isLikeChecked:Boolean){
        setSummary(summary.value.copy(isLikeChecked = isLikeChecked))
    }

    /*
    private val _editTitleValue = MutableStateFlow("제목")
    val editTitleValue: StateFlow<String> = _editTitleValue.asStateFlow()

    fun setEditTextValue(text:String){
        _editTitleValue.value=text
    }



    private val _editContentValue = MutableStateFlow("제목")
    val editContentValue: StateFlow<String> = _editContentValue.asStateFlow()

    fun setEditContentValue(text:String){
        _editTitleValue.value=text
    }

*/

    fun initialize(year:Int, month:Int, day:Int){


        viewModelScope.launch{
            val gotSummary = withContext(Dispatchers.IO){
                summaryRepository.getSummariesByDate(LocalDate.of(year,month,day))
            }
            if (gotSummary == null) {
                setDate(LocalDate.of(year,month,day))
                setIsWritten(false)
            } else {
                setSummary(gotSummary)
                setIsWritten(true)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateTitleByDate(
        date: LocalDate = summary.value.date,
        title:String=summary.value.title
    ){
        viewModelScope.launch{
            summaryRepository.updateTitleByDate(date, title)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateContentByDate(
        date: LocalDate = summary.value.date,
        content:String=summary.value.content
    ){
        viewModelScope.launch{
            summaryRepository.updateContentByDate(date, content)
        }
    }



    private val _isEditingTitle = MutableStateFlow(false)
    val isEditingTitle: StateFlow<Boolean> = _isEditingTitle.asStateFlow()

    fun setIsEditingTitle(isEditing:Boolean){
        _isEditingTitle.value=isEditing
    }


    private val _isEditingContent = MutableStateFlow(false)
    val isEditingContent: StateFlow<Boolean> = _isEditingContent.asStateFlow()

    fun setIsEditingContent(isEditing:Boolean){
        _isEditingContent.value=isEditing
    }


}

