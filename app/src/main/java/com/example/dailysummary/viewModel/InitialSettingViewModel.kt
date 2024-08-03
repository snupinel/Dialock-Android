package com.example.dailysummary.viewModel

import androidx.lifecycle.ViewModel
import com.example.dailysummary.data.PrefRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class InitialSettingViewModel @Inject constructor(
    private val prefRepository: PrefRepository,
):ViewModel(){

    private val _startPageAnimationState = MutableStateFlow(0)
    val startPageAnimationState: StateFlow<Int> = _startPageAnimationState.asStateFlow()

    fun setStartPageAnimationState(num:Int){
        _startPageAnimationState.value=num
    }
}