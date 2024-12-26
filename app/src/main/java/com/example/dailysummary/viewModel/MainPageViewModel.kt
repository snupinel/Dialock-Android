package com.example.dailysummary.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.example.dailysummary.data.PrefRepository
import com.example.dailysummary.dto.Summary
import com.example.dailysummary.model.CalenderEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject

enum class Tab{
    Calender,Setting
}

@HiltViewModel
@SuppressLint("NewApi")
class MainPageViewModel @Inject constructor(
    private val prefRepository: PrefRepository,
):ViewModel(){
    private val _selectedTab = MutableStateFlow(Tab.Calender)
    val selectedTab = _selectedTab.asStateFlow()

    fun updateTab(tab:String){
        _selectedTab.value = Tab.valueOf(tab)
    }

    @SuppressLint("NewApi")
    val currentDate = LocalDate.now() // 현재 날짜 가져오기
    @SuppressLint("NewApi")
    val currentYear = currentDate.year // 현재 연도
    @SuppressLint("NewApi")
    val currentMonth = currentDate.monthValue // 현재 월 (숫자로)

    private val _selectedYearAndMonth = MutableStateFlow(Pair(currentYear,currentMonth))
    val selectedYearAndMonth = _selectedYearAndMonth.asStateFlow()

    fun setSelectedYearAndMonth(year:Int,month: Int){
        _selectedYearAndMonth.value= Pair(year,month)
    }


    val summarySamples = listOf(
        Summary(
            writtenTime = LocalDate.of(2024,12,26),
            date = LocalDate.of(2024,12,26),
            content = "박성학 접선"),
        Summary(
            writtenTime = LocalDate.of(2024,12,24),
            date = LocalDate.of(2024,12,24),
            content = "솔크"),
        Summary(
            writtenTime = LocalDate.of(2024,12,31),
            date = LocalDate.of(2024,12,31),
            content = "솔크"),
        Summary(
            writtenTime = LocalDate.of(2024,12,25),
            date = LocalDate.of(2024,12,25),
            content = "솔크"),
        Summary(
            writtenTime = LocalDate.of(2024,12,1),
            date = LocalDate.of(2024,12,1),
            content = "솔크"),


    )

    //private val _currentMonthSummaries = MutableStateFlow(listOf<Summary>())
    private val _currentMonthSummaries = MutableStateFlow(summarySamples)

    val currentMonthSummaries = _currentMonthSummaries.asStateFlow()

    private val _calenderEntries = MutableStateFlow(listOf<CalenderEntry>())

    val calenderEntries = _calenderEntries.asStateFlow()

    fun setCalenderEntries(){
        val list = mutableListOf<CalenderEntry>()
        val frontBlankCount=LocalDate.of(selectedYearAndMonth.value.first,selectedYearAndMonth.value.second,1).dayOfWeek.value%7
        repeat(frontBlankCount){
            list.add(CalenderEntry(isBlank = true,isWritten = false, day = 0 , summaryIndex = 0))
        }
        val daysInMonth = LocalDate.of(selectedYearAndMonth.value.first,selectedYearAndMonth.value.second,1).lengthOfMonth()
        repeat(daysInMonth){
            list.add(CalenderEntry(isBlank = false,isWritten = false, day = it+1 , summaryIndex = 0))
        }
        currentMonthSummaries.value.forEachIndexed{ index, summary ->
            list[frontBlankCount-1+summary.date.dayOfMonth] = CalenderEntry(isBlank = false,isWritten = true, day = summary.date.dayOfMonth , summaryIndex = index)
        }
        val backBlankCount = (7 - (frontBlankCount + daysInMonth) % 7) % 7
        repeat(backBlankCount){
            list.add(CalenderEntry(isBlank = true,isWritten = false, day = 0 , summaryIndex = 0))
        }

        _calenderEntries.value=list
    }

    private val _adviceOrForcing = MutableStateFlow(Pair(false,false))
    val adviceOrForcing: StateFlow<Pair<Boolean, Boolean>> = _adviceOrForcing.asStateFlow()

    fun clickAdviceOrForcing(clickedIsLeft:Boolean){
        _adviceOrForcing.value=Pair(clickedIsLeft,!clickedIsLeft)
    }

}