package com.example.dailysummary.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dailysummary.dto.AdviceOrForcing
import com.example.dailysummary.dto.SAMPLE_ALARM_TIME
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
    saveDiary : (content:String,isThumbUp:Boolean,isLikeChecked:Boolean) -> Unit,
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

    var isLikeChecked:Boolean by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        adviceOrForcing = getSetting().adviceOrForcing
    }

    DailySummaryTheme(isOverlay = true) {
        Surface(modifier = Modifier
            .clip(RoundedCornerShape(8.dp))) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ){
                    MinimizeButton {

                    }
                    CloseButton(isAdvice = adviceOrForcing==AdviceOrForcing.Advice) {
                        close() }
                }

                Spacer(modifier = Modifier.height(20.dp))
                // 상단의 글 쓰는 박스

                TextBox(
                    isLikeChecked = isLikeChecked,
                    toggleLike = {isLikeChecked=!isLikeChecked},
                    textFieldValue = textFieldValue,
                    onValueChange = {textFieldValue=it}
                )
                // 하단의 버튼 영역
                Thumbs(
                    isSummaryWritten = textFieldValue!="",
                    isUp=isUp,
                ){
                    isUp=it
                }
                Spacer(modifier = Modifier.weight(1f))
                RoundedCornerButton(modifier = Modifier.fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 12.dp),
                    onClick = {
                        saveDiary(textFieldValue,isUp!!,isLikeChecked)
                        close()
                    },
                    enabled = textFieldValue!=""
                ){
                    Text("저장", color = MaterialTheme.colorScheme.onPrimary)
                }

            }
        }
    }
}

@Composable
fun LikeButton(
    isChecked: Boolean=true,
    onClick: () -> Unit,
){
    IconButton(onClick = onClick) {
        when(isChecked){
            false->
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    )
            true->
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Favorite")
        }


    }
}

@Composable
fun TextBox(
    isLikeChecked:Boolean=false,
    toggleLike:()->Unit,
    textFieldValue:String,
    onValueChange:(String)->Unit,
){
    Row {
        TextField(
            value = textFieldValue,
            onValueChange = onValueChange,
            label = { Text("Write something...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            trailingIcon = {
                LikeButton(isChecked = isLikeChecked) {
                    toggleLike()
                }
            }
        )

    }
}



@Composable
@Preview
fun OverlayPreview(){
    DailySummaryTheme {
        Overlay(
            close = {},
            getSetting = {  Setting(AdviceOrForcing.Advice,true,List(7){ SAMPLE_ALARM_TIME})},
            saveDiary = { content, isThumbUp, isLikeChecked ->
            }
            //setTextFieldValue =
        )
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
    modifier: Modifier=Modifier,
    isSummaryWritten: Boolean = true,
    isUp: Boolean,
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
    modifier: Modifier=Modifier,
    isAdvice:Boolean=false,
    onClick:()->Unit,
){
    if(isAdvice)
        RoundedCornerButton(
            modifier = modifier
                .fillMaxHeight()
                .width(60.dp),
            onClick=onClick,
            ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

}
@Composable
fun MinimizeButton(
    modifier: Modifier=Modifier,
    onClick:()->Unit,
){
    RoundedCornerButton(
        modifier = modifier
            .fillMaxHeight()
            .width(60.dp),
        onClick=onClick,
        ) {
        Icon(
            imageVector = Icons.Outlined.KeyboardArrowDown,
            contentDescription = "Minimize",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

