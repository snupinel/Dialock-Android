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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.SentimentNeutral
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.dailysummary.dto.AdviceOrForcing
import com.example.dailysummary.dto.DayRating
import com.example.dailysummary.dto.Setting
import com.example.dailysummary.ui.theme.DailySummaryTheme

@Composable
fun Overlay(
    //viewModel: OverlayViewModel,
    close: () -> Unit,
    isWritten:Boolean,
    //adviceOrForcing: AdviceOrForcing,
    getSetting: () -> Setting,
    //textFieldValue: String,
    //setTextFieldValue: (String) -> Unit,
    saveDiary : (content:String,isBookmarked:Boolean,dayRating: DayRating) -> Unit,
) {
    //val viewModel = hiltViewModel<OverlayViewModel>()

    var adviceOrForcing by remember {
        mutableStateOf(AdviceOrForcing.Advice)
    }

    var textFieldValue by remember {
        mutableStateOf("")
    }

    var dayRating: DayRating by remember {
        mutableStateOf(DayRating.SOSO)
    }

    var isBookmarked:Boolean by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        adviceOrForcing = getSetting().adviceOrForcing
        if(isWritten) adviceOrForcing = AdviceOrForcing.Advice
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
                        close()
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                // 상단의 글 쓰는 박스

                TextBox(
                    isBookmarked = isBookmarked,
                    toggleBookmark = {isBookmarked=!isBookmarked},
                    textFieldValue = textFieldValue,
                    onValueChange = {textFieldValue=it}
                )
                // 하단의 버튼 영역
                DayRatingSelector(dayRating = dayRating, setRating = {
                    dayRating = it
                })
                Spacer(modifier = Modifier.weight(1f))
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 12.dp),
                    onClick = {
                        saveDiary(textFieldValue,isBookmarked,dayRating)
                        close()
                    },
                    enabled = textFieldValue!="",
                    shape = RoundedCornerShape(8.dp)

                ){
                    Text("저장")
                }

            }
        }
    }
}

@Composable
fun BookmarkButton(
    isChecked: Boolean=true,
    onClick: () -> Unit,
){
    IconButton(onClick = onClick) {
        when(isChecked){
            false->
                Icon(
                    imageVector = Icons.Outlined.Bookmark,
                    contentDescription = "Bookmark",
                    )
            true->
                Icon(
                    imageVector = Icons.Filled.Bookmark,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Bookmark")
        }


    }
}

@Composable
fun TextBox(
    isBookmarked:Boolean=false,
    toggleBookmark:()->Unit,
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
                BookmarkButton(isChecked = isBookmarked) {
                    toggleBookmark()
                }
            }
        )

    }
}





@Composable
fun DayRatingSelector(
    dayRating: DayRating,
    setRating:(DayRating)->Unit,
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        IconButton(
            modifier = Modifier.alpha(if(dayRating==DayRating.GOOD)1f else 0.5f),
            onClick = { setRating(DayRating.GOOD) }) {
            Icon(imageVector = Icons.Outlined.ThumbUp, contentDescription = "ThumbUp")
        }
        Spacer(modifier = Modifier.width(50.dp))
        IconButton(
            modifier = Modifier.alpha(if(dayRating==DayRating.SOSO)1f else 0.5f),
            onClick = { setRating(DayRating.SOSO) }) {
            Icon(imageVector = Icons.Outlined.SentimentNeutral, contentDescription = "Neutral")
        }
        Spacer(modifier = Modifier.width(50.dp))
        IconButton(
            modifier = Modifier.alpha(if(dayRating==DayRating.BAD)1f else 0.5f),
            onClick = { setRating(DayRating.BAD) }) {
            Icon(imageVector = Icons.Outlined.ThumbDown, contentDescription = "ThumbUp")
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
        Button(
            modifier = modifier
                .fillMaxHeight()
                .width(60.dp),
            onClick=  onClick,
            shape = RoundedCornerShape(8.dp)
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
    Button(
        modifier = modifier
            .fillMaxHeight()
            .width(60.dp),
        shape = RoundedCornerShape(8.dp),
        onClick= onClick,
        ) {
        Icon(
            imageVector = Icons.Outlined.KeyboardArrowDown,
            contentDescription = "Minimize",
        )
    }
}

