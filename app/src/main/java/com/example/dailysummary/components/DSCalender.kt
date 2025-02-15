package com.example.dailysummary.components

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dailysummary.viewModel.MainPageViewModel
import java.time.LocalDate
import java.time.Month
import java.time.Year

@Composable
fun DSCalender(
    modifier: Modifier=Modifier,
    navController: NavController,
){


    val viewModel = hiltViewModel<MainPageViewModel>()

    val context = LocalContext.current

    val selectedYearAndMonth by viewModel.selectedYearAndMonth.collectAsState()
    val calenderEntries by viewModel.calenderEntries.collectAsState()
    val isCurrentYear = selectedYearAndMonth.first == viewModel.currentYear

    val showPopup by viewModel.showPopup.collectAsState()
    val popupPosition by viewModel.popupPosition.collectAsState()
    val clickedBoxIndex by viewModel.clickedBoxIndex.collectAsState()
    //var showPopup by remember { mutableStateOf(false) }
    //val buttonPosition = remember { mutableStateOf(IntOffset(0, 0)) }

    Box(){
        Column(modifier = modifier) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                AdjustMonthButton(isPrev = true,Modifier.weight(1f)){ viewModel.prevMonth()}
                CalenderMonth(
                    modifier = Modifier.weight(1f),
                    isCurrentYear=isCurrentYear,
                    year = selectedYearAndMonth.first,
                    month = selectedYearAndMonth.second
                )
                AdjustMonthButton(isPrev = false,Modifier.weight(1f)) {viewModel.nextMonth()}
            }
            Row {
                val weekList= listOf(
                    Pair(Color.Red,"S"),
                    Pair(null,"M"),
                    Pair(null,"T"),
                    Pair(null,"W"),
                    Pair(null,"T"),
                    Pair(null,"F"),
                    Pair(Color.Blue,"S"),
                )
                repeat(7){
                    CalenderDate(
                        modifier = Modifier.weight(1f),
                        color = weekList[it].first,
                        text = weekList[it].second
                    )
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
            ){
                itemsIndexed(calenderEntries){index, calenderEntry->
                    CalenderBox(
                        isBlank = calenderEntry.isBlank,
                        isWritten = calenderEntry.isWritten,
                        day = calenderEntry.day,
                    ){intOffset->
                        if(calenderEntry.isWritten){
                            //Toast.makeText(context,viewModel.readSummary(it.summaryIndex).content,Toast.LENGTH_SHORT).show()
                            if(!showPopup){
                                viewModel.setClickedBoxIndex(index)
                                viewModel.setPopupPosition(intOffset)
                                viewModel.setShowPopup(true)

                            }
                        }
                        else{
                            val targetDate = LocalDate.of(selectedYearAndMonth.first,selectedYearAndMonth.second,calenderEntry.day)

                            if(targetDate.isAfter(LocalDate.now())){

                            }
                            else {
                                navController.navigate("SummaryPage/${selectedYearAndMonth.first}/${selectedYearAndMonth.second}/${calenderEntry.day}")
                            }
                        }

                    }
                }
            }
        }
        if (showPopup) {
            Popup(
                onDismissRequest = { viewModel.setShowPopup(false) },
                popupPositionProvider = object : PopupPositionProvider {
                    override fun calculatePosition(anchorBounds: IntRect, windowSize: IntSize, layoutDirection: LayoutDirection, popupContentSize: IntSize): IntOffset {
                        return IntOffset(
                            x = popupPosition.x,
                            y = popupPosition.y - popupContentSize.height - 16  // 버튼 위에 위치
                        )
                    }
                }
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.Black, shape = RoundedCornerShape(10.dp))
                        .padding(12.dp)
                        .clickable {
                            navController.navigate("SummaryPage/${selectedYearAndMonth.first}/${selectedYearAndMonth.second}/${calenderEntries[clickedBoxIndex].day}")
                        }
                ) {
                    Text(viewModel.readSummary(calenderEntries[clickedBoxIndex].summaryIndex).title, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CalenderMonth(
    modifier: Modifier = Modifier,
    isCurrentYear: Boolean,
    year:Int,
    month: Int
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(!isCurrentYear) Text(text = year.toString())
        Text(text = month.toString())
    }
}

@Composable
fun CalenderDate(
    modifier: Modifier=Modifier,
    color:Color?=null,
    text:String,
){
    val resolvedColor = color ?: MaterialTheme.colorScheme.primary
    Text(text = text, modifier = modifier,color=resolvedColor, textAlign = TextAlign.Center)
}

@Composable
fun CalenderBox(
    modifier: Modifier=Modifier,
    isBlank:Boolean = false,
    isNotBlank: Boolean = !isBlank,
    isWritten:Boolean = false,
    isNotWritten:Boolean = !isWritten,
    day:Int=0,
    onClick: (IntOffset) -> Unit,
){

    var buttonPosition by remember { mutableStateOf(IntOffset(0, 0)) }

    Box(modifier = modifier
        .aspectRatio(1f)
        .padding(4.dp)
        .clip(shape = RoundedCornerShape(12.dp))
        .then(
            if (isBlank) Modifier.background(color = Color.Transparent)
            else {
                Modifier
                    .clickable { onClick(buttonPosition) }
                    .background(
                        color =
                        if (isWritten)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.primaryContainer
                    )
                    .onGloballyPositioned { coordinates ->
                        buttonPosition = coordinates
                            .positionInWindow()
                            .round()
                    }

            }
        ),
        contentAlignment = Alignment.Center

    ){
        if(isNotBlank)
            Text(
                text = "$day",
                color = if(isWritten) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.primary
            )
    }
}

@Composable
fun AdjustMonthButton(
    isPrev:Boolean=true,
    modifier: Modifier = Modifier,
    onClick : () ->Unit,
    ){

    IconButton(
        modifier = modifier
            .fillMaxHeight(0.5f)
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.primary),
        onClick = onClick) {
        Icon(
            imageVector =
            if(isPrev) Icons.Outlined.KeyboardArrowLeft
            else Icons.Outlined.KeyboardArrowRight,
            contentDescription = if(isPrev) "Prev" else "Next",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}
