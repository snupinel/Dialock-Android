package com.example.dailysummary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.dailysummary.dto.AdviceOrForcing
import com.example.dailysummary.dto.Setting
import com.example.dailysummary.ui.theme.DailySummaryTheme

@Composable
fun Overlay(
    //viewModel: OverlayViewModel,
    close: () -> Unit,
    //adviceOrForcing: AdviceOrForcing,
    getSetting: () -> Setting,
    //textFieldValue: String,
    //setTextFieldValue: (String) -> Unit,
    saveDiary : (String) -> Unit,
) {
    //val viewModel = hiltViewModel<OverlayViewModel>()

    var adviceOrForcing by remember {
        mutableStateOf(AdviceOrForcing.Advice)
    }

    var textFieldValue by remember {
        mutableStateOf("")
    }

    var isUp:Boolean? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(Unit) {
        adviceOrForcing = getSetting().adviceOrForcing
    }

    DailySummaryTheme(isOverlay = true) {
        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ){
                    MinimizeButton {

                    }
                    CloseButton(isAdvice = adviceOrForcing==AdviceOrForcing.Advice) {
                        close() }
                }

                // 상단의 글 쓰는 박스
                TextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it},
                    label = { Text("Write something...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .focusable()
                )

                // 하단의 버튼 영역
                Thumbs(
                    isSummaryWritten = textFieldValue!="",
                    isUp=isUp,
                ){
                    isUp=it
                }
                SaveButton {
                    saveDiary(textFieldValue)
                    close()
                }
            }
        }
    }
}
@Composable
fun Thumbs(
    isSummaryWritten:Boolean,
    isUp:Boolean?=null,
    isSelected:Boolean=isUp!=null,
    setThumb:(Boolean)->Unit,
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        when (isSelected){
            true->{
                ThumbButton(isUp = isUp!!) {
                    setThumb(!isUp)
                }
            }
            false ->{
                ThumbButton(
                    isSummaryWritten=isSummaryWritten,
                    isUp = true,
                ) {
                    setThumb(true)
                }
                Spacer(modifier = Modifier.width(100.dp))
                ThumbButton(
                    isSummaryWritten=isSummaryWritten,
                    isUp = false,
                ) {
                    setThumb(false)
                }
            }
        }
    }
}

@Composable
fun ThumbButton(
    isSummaryWritten: Boolean = true,
    isUp: Boolean,
    modifier: Modifier=Modifier,
    onClick:()->Unit,
){
    IconButton(
        modifier = modifier,
        onClick = onClick,
        enabled = isSummaryWritten) {
        Icon(
            imageVector = if(isUp) Icons.Outlined.ThumbUp else Icons.Outlined.ThumbDown,
            contentDescription = if(isUp) "ThumbUp" else "ThumbDown",
        )
    }
}

@Composable
fun CloseButton(
    isAdvice:Boolean=false,
    modifier: Modifier=Modifier,
    onClick:()->Unit,
){
    if(isAdvice)
        IconButton(
            modifier = modifier,
            onClick = onClick) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Close",
            )
        }

}
@Composable
fun MinimizeButton(
    modifier: Modifier=Modifier,
    onClick:()->Unit,
){
    IconButton(
        modifier = modifier,
        onClick = onClick) {
        Icon(
            imageVector = Icons.Outlined.KeyboardArrowDown,
            contentDescription = "Minimize",
        )
    }
}

@Composable
fun SaveButton(
    onClick: () -> Unit,
){
    Button(onClick = onClick
    ) {
        Text("저장")
    }
}