package com.example.dailysummary.pages

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailysummary.dto.AnimaionTarget
import kotlinx.coroutines.delay

@Composable
fun StartPage(){
    /*
    Column (
        horizontalAlignment =  Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Row (verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(100.dp)){

            Text(text = "Daily", fontSize = 50.sp, fontWeight = FontWeight.Light, color = Color.LightGray)
            Text(text = "Summary", fontSize = 50.sp,fontWeight = FontWeight.Light, color = Color.Gray)
        }
        Text(text = "에 오신 것을 환영합니다!", fontSize = 20.sp, fontWeight = FontWeight.Light,  color=Color.DarkGray)
    }*/
    Greeting()
}
@Composable
fun Greeting() {
    var startPageAnimationState by remember { mutableIntStateOf(0) }

    // Start the animation after a slight delay
    LaunchedEffect(Unit) {
        delay(500) // Delay before starting the animation
        startPageAnimationState = 1
    }

    // Animation definitions
    val alpha0 by animateFloatAsState(
        targetValue = getTargetValue(0,startPageAnimationState).alpha,
        animationSpec = tween(durationMillis = 1000), label = ""
    )
    val offsetY0 by animateDpAsState(
        targetValue = getTargetValue(0,startPageAnimationState).offsetY.dp,
        animationSpec = tween(durationMillis = 1000), label = ""
    )
    val alpha1 by animateFloatAsState(
        targetValue = getTargetValue(1,startPageAnimationState).alpha,
        animationSpec = tween(durationMillis = 1000), label = ""
    )
    val offsetY1 by animateDpAsState(
        targetValue = getTargetValue(1,startPageAnimationState).offsetY.dp,
        animationSpec = tween(durationMillis = 1000), label = ""
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(100.dp)
                .offset(y = offsetY0)
                .alpha(alpha0)
        ) {
            Text(
                text = "Daily",
                fontSize = 50.sp,
                fontWeight = FontWeight.Light,
                color = Color.LightGray
            )
            Text(
                text = "Summary",
                fontSize = 50.sp,
                fontWeight = FontWeight.Light,
                color = Color.Gray
            )
        }
        Text(
            text = "에 오신 것을 환영합니다!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Light,
            color = Color.DarkGray,
            modifier = Modifier
                .offset(y = offsetY1)
                .alpha(alpha1)
        )
    }

}
val targetList0= listOf(
    AnimaionTarget(0f,50),
    AnimaionTarget(1f,0)
)
val targetList1= listOf(
    AnimaionTarget(0f,100),
    AnimaionTarget(1f,0)
)
fun getTargetValue(obId:Int,startPageAnimationState:Int,):AnimaionTarget{
    return when(obId){
        0-> targetList0[startPageAnimationState]
        1-> targetList1[startPageAnimationState]
        else->AnimaionTarget(0f,0)
    }
}